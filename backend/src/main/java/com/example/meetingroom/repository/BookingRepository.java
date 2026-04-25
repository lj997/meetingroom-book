package com.example.meetingroom.repository;

import com.example.meetingroom.entity.Booking;
import com.example.meetingroom.entity.BookingStatus;
import com.example.meetingroom.entity.MeetingRoom;
import com.example.meetingroom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserOrderByDateDescStartTimeDesc(User user);

    List<Booking> findByMeetingRoomAndDateAndStatusOrderByStartTime(
            MeetingRoom meetingRoom, 
            LocalDate date, 
            BookingStatus status);

    List<Booking> findByDateAndStatusOrderByDateDescStartTimeDesc(
            LocalDate date, 
            BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.meetingRoom = :meetingRoom " +
           "AND b.date = :date AND b.status = :status " +
           "AND b.startTime < :endTime AND b.endTime > :startTime")
    List<Booking> findConflictingBookings(
            @Param("meetingRoom") MeetingRoom meetingRoom,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.meetingRoom = :meetingRoom " +
           "AND b.date = :date AND b.status = :status AND b.id != :excludeId " +
           "AND b.startTime < :endTime AND b.endTime > :startTime")
    List<Booking> findConflictingBookingsExcludeId(
            @Param("meetingRoom") MeetingRoom meetingRoom,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("status") BookingStatus status,
            @Param("excludeId") Long excludeId);

    Optional<Booking> findByIdAndUser(Long id, User user);

    List<Booking> findByMeetingRoomOrderByDateDescStartTimeDesc(MeetingRoom meetingRoom);
}
