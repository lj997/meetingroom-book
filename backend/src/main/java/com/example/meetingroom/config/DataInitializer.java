package com.example.meetingroom.config;

import com.example.meetingroom.entity.MeetingRoom;
import com.example.meetingroom.repository.MeetingRoomRepository;
import com.example.meetingroom.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final MeetingRoomRepository meetingRoomRepository;

    public DataInitializer(UserService userService, MeetingRoomRepository meetingRoomRepository) {
        this.userService = userService;
        this.meetingRoomRepository = meetingRoomRepository;
    }

    @Override
    public void run(String... args) {
        userService.initDefaultAdmin();
        initDefaultMeetingRooms();
    }

    private void initDefaultMeetingRooms() {
        if (meetingRoomRepository.count() == 0) {
            MeetingRoom room1 = new MeetingRoom();
            room1.setName("小型会议室 A");
            room1.setLocation("3楼 301室");
            room1.setCapacity(6);
            room1.setEquipment("投影仪, 白板, 视频会议系统");
            room1.setDescription("适合小型团队会议，可容纳6人");
            room1.setActive(true);
            meetingRoomRepository.save(room1);

            MeetingRoom room2 = new MeetingRoom();
            room2.setName("小型会议室 B");
            room2.setLocation("3楼 302室");
            room2.setCapacity(8);
            room2.setEquipment("投影仪, 白板");
            room2.setDescription("适合小型团队会议，可容纳8人");
            room2.setActive(true);
            meetingRoomRepository.save(room2);

            MeetingRoom room3 = new MeetingRoom();
            room3.setName("中型会议室");
            room3.setLocation("4楼 401室");
            room3.setCapacity(15);
            room3.setEquipment("投影仪, 白板, 视频会议系统, 音响系统");
            room3.setDescription("适合中型部门会议，可容纳15人");
            room3.setActive(true);
            meetingRoomRepository.save(room3);

            MeetingRoom room4 = new MeetingRoom();
            room4.setName("大型会议室");
            room4.setLocation("5楼 501室");
            room4.setCapacity(30);
            room4.setEquipment("投影仪, 白板, 视频会议系统, 音响系统, 麦克风");
            room4.setDescription("适合大型会议或培训，可容纳30人");
            room4.setActive(true);
            meetingRoomRepository.save(room4);
        }
    }
}
