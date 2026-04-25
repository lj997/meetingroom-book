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

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.meetingRoom LEFT JOIN FETCH b.user WHERE b.user = :user ORDER BY b.date DESC, b.startTime DESC")
    List<Booking> findByUserOrderByDateDescStartTimeDesc(@Param("user") User user);

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.meetingRoom LEFT JOIN FETCH b.user WHERE b.meetingRoom = :meetingRoom AND b.date = :date AND b.status = :status ORDER BY b.startTime")
    List<Booking> findByMeetingRoomAndDateAndStatusOrderByStartTime(
            @Param("meetingRoom") MeetingRoom meetingRoom, 
            @Param("date") LocalDate date, 
            @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.meetingRoom LEFT JOIN FETCH b.user WHERE b.date = :date AND b.status = :status ORDER BY b.date DESC, b.startTime DESC")
    List<Booking> findByDateAndStatusOrderByDateDescStartTimeDesc(
            @Param("date") LocalDate date, 
            @Param("status") BookingStatus status);

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

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.meetingRoom LEFT JOIN FETCH b.user WHERE b.id = :id AND b.user = :user")
    Optional<Booking> findByIdAndUser(@Param("id") Long id, @Param("user") User user);

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.meetingRoom LEFT JOIN FETCH b.user WHERE b.id = :id")
    Optional<Booking> findById(@Param("id") Long id);

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.meetingRoom LEFT JOIN FETCH b.user WHERE b.meetingRoom = :meetingRoom ORDER BY b.date DESC, b.startTime DESC")
    List<Booking> findByMeetingRoomOrderByDateDescStartTimeDesc(@Param("meetingRoom") MeetingRoom meetingRoom);
}
