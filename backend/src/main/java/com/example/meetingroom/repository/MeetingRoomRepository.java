package com.example.meetingroom.repository;

import com.example.meetingroom.entity.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {

    List<MeetingRoom> findByActiveTrueOrderByName();

    boolean existsByName(String name);
}
