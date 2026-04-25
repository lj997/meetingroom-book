package com.example.meetingroom.service;

import com.example.meetingroom.dto.BookingRequest;
import com.example.meetingroom.entity.Booking;
import com.example.meetingroom.entity.BookingStatus;
import com.example.meetingroom.entity.MeetingRoom;
import com.example.meetingroom.entity.User;
import com.example.meetingroom.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final MeetingRoomService meetingRoomService;

    public BookingService(BookingRepository bookingRepository, MeetingRoomService meetingRoomService) {
        this.bookingRepository = bookingRepository;
        this.meetingRoomService = meetingRoomService;
    }

    public List<Booking> getBookingsByUser(User user) {
        return bookingRepository.findByUserOrderByDateDescStartTimeDesc(user);
    }

    public List<Booking> getBookingsByRoomAndDate(Long roomId, LocalDate date) {
        MeetingRoom room = meetingRoomService.findById(roomId)
                .orElseThrow(() -> new RuntimeException("会议室不存在"));
        return bookingRepository.findByMeetingRoomAndDateAndStatusOrderByStartTime(
                room, date, BookingStatus.CONFIRMED);
    }

    public List<Booking> getBookingsByDate(LocalDate date) {
        return bookingRepository.findByDateAndStatusOrderByDateDescStartTimeDesc(
                date, BookingStatus.CONFIRMED);
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    public Optional<Booking> findByIdAndUser(Long id, User user) {
        return bookingRepository.findByIdAndUser(id, user);
    }

    @Transactional
    public Booking createBooking(BookingRequest request, User user) {
        MeetingRoom room = meetingRoomService.findById(request.getMeetingRoomId())
                .orElseThrow(() -> new RuntimeException("会议室不存在"));

        if (!room.isActive()) {
            throw new RuntimeException("会议室已停用");
        }

        validateBookingTime(request.getDate(), request.getStartTime(), request.getEndTime());

        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                room,
                request.getDate(),
                request.getStartTime(),
                request.getEndTime(),
                BookingStatus.CONFIRMED
        );

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("该时间段已有预约，请选择其他时间");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setMeetingRoom(room);
        booking.setTitle(request.getTitle());
        booking.setDate(request.getDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setDescription(request.getDescription());
        booking.setStatus(BookingStatus.CONFIRMED);

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking cancelBooking(Long bookingId, User user) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("预约不存在"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权取消此预约");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("预约已取消");
        }

        if (isBookingStarted(booking)) {
            throw new RuntimeException("已开始的预约不可取消");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    private void validateBookingTime(LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (date.isBefore(LocalDate.now())) {
            throw new RuntimeException("不能预约过去的日期");
        }

        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new RuntimeException("开始时间必须早于结束时间");
        }

        if (startTime.getMinute() % 30 != 0 || endTime.getMinute() % 30 != 0) {
            throw new RuntimeException("预约时间必须以30分钟为单位");
        }

        long durationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
        if (durationMinutes < 30) {
            throw new RuntimeException("预约时长至少为30分钟");
        }
    }

    private boolean isBookingStarted(Booking booking) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (booking.getDate().isBefore(today)) {
            return true;
        }

        if (booking.getDate().equals(today) && booking.getStartTime().isBefore(now)) {
            return true;
        }

        return false;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
