package com.example.meetingroom.controller;

import com.example.meetingroom.dto.BookingRequest;
import com.example.meetingroom.entity.Booking;
import com.example.meetingroom.entity.User;
import com.example.meetingroom.service.BookingService;
import com.example.meetingroom.service.UserService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    public BookingController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @GetMapping("/my")
    public List<Booking> getMyBookings(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return bookingService.getBookingsByUser(user);
    }

    @GetMapping("/room/{roomId}/date/{date}")
    public List<Booking> getBookingsByRoomAndDate(
            @PathVariable Long roomId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return bookingService.getBookingsByRoomAndDate(roomId, date);
    }

    @GetMapping("/date/{date}")
    public List<Booking> getBookingsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return bookingService.getBookingsByDate(date);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(
            @PathVariable Long id,
            Authentication authentication) {
        return bookingService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication) {
        User user = getCurrentUser(authentication);
        Booking booking = bookingService.createBooking(request, user);
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, String>> cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {
        User user = getCurrentUser(authentication);
        bookingService.cancelBooking(id, user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "取消成功");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("未登录");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}
