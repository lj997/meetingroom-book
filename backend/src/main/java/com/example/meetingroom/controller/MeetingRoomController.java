package com.example.meetingroom.controller;

import com.example.meetingroom.dto.MeetingRoomRequest;
import com.example.meetingroom.entity.MeetingRoom;
import com.example.meetingroom.service.MeetingRoomService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meeting-rooms")
public class MeetingRoomController {

    private final MeetingRoomService meetingRoomService;

    public MeetingRoomController(MeetingRoomService meetingRoomService) {
        this.meetingRoomService = meetingRoomService;
    }

    @GetMapping
    public List<MeetingRoom> getAllMeetingRooms() {
        return meetingRoomService.getAllMeetingRooms();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public List<MeetingRoom> getAllMeetingRoomsForAdmin() {
        return meetingRoomService.getAllMeetingRoomsIncludingInactive();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingRoom> getMeetingRoomById(@PathVariable Long id) {
        return meetingRoomService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MeetingRoom> createMeetingRoom(@Valid @RequestBody MeetingRoomRequest request) {
        MeetingRoom room = meetingRoomService.createMeetingRoom(request);
        return ResponseEntity.ok(room);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MeetingRoom> updateMeetingRoom(
            @PathVariable Long id,
            @Valid @RequestBody MeetingRoomRequest request) {
        MeetingRoom room = meetingRoomService.updateMeetingRoom(id, request);
        return ResponseEntity.ok(room);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteMeetingRoom(@PathVariable Long id) {
        meetingRoomService.deleteMeetingRoom(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}
