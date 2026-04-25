package com.example.meetingroom.service;

import com.example.meetingroom.dto.MeetingRoomRequest;
import com.example.meetingroom.entity.MeetingRoom;
import com.example.meetingroom.repository.MeetingRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;

    public MeetingRoomService(MeetingRoomRepository meetingRoomRepository) {
        this.meetingRoomRepository = meetingRoomRepository;
    }

    public List<MeetingRoom> getAllMeetingRooms() {
        return meetingRoomRepository.findByActiveTrueOrderByName();
    }

    public List<MeetingRoom> getAllMeetingRoomsIncludingInactive() {
        return meetingRoomRepository.findAll();
    }

    public Optional<MeetingRoom> findById(Long id) {
        return meetingRoomRepository.findById(id);
    }

    @Transactional
    public MeetingRoom createMeetingRoom(MeetingRoomRequest request) {
        MeetingRoom room = new MeetingRoom();
        room.setName(request.getName());
        room.setLocation(request.getLocation());
        room.setCapacity(request.getCapacity());
        room.setEquipment(request.getEquipment());
        room.setDescription(request.getDescription());
        room.setActive(request.isActive());

        return meetingRoomRepository.save(room);
    }

    @Transactional
    public MeetingRoom updateMeetingRoom(Long id, MeetingRoomRequest request) {
        MeetingRoom room = meetingRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("会议室不存在"));

        room.setName(request.getName());
        room.setLocation(request.getLocation());
        room.setCapacity(request.getCapacity());
        room.setEquipment(request.getEquipment());
        room.setDescription(request.getDescription());
        room.setActive(request.isActive());

        return meetingRoomRepository.save(room);
    }

    @Transactional
    public void deleteMeetingRoom(Long id) {
        MeetingRoom room = meetingRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("会议室不存在"));
        room.setActive(false);
        meetingRoomRepository.save(room);
    }
}
