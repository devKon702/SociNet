package com.example.chatservice.service;

import com.example.chatservice.dto.RoomActivityDto;
import com.example.chatservice.dto.RoomMemberDto;
import com.example.chatservice.entity.Room;
import com.example.chatservice.entity.RoomMember;
import com.example.chatservice.entity.User;
import com.example.chatservice.enums.ActivityType;
import com.example.chatservice.repository.RoomMemberRepository;
import com.example.chatservice.repository.RoomRepository;
import com.example.chatservice.repository.UserRepository;
import com.example.chatservice.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RoomMemberService {
    private final RoomMemberRepository roomMemberRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomActivityService roomActivityService;

    public RoomMemberDto getMember(Long userId, Long roomId) throws Exception{
        RoomMember member = roomMemberRepository.findByUser_IdAndRoom_Id(userId, roomId).orElseThrow(() -> new Exception("NOT FOUND"));
        return new RoomMemberDto(member);
    }

//    public RoomMemberDto addMember(Long userId, Long roomId) throws Exception{
//        // Kiem tra user ton tai
//        User user = userRepository.findById(userId).orElseThrow(() ->  new Exception("USER NOT EXIST"));
//        // Kiem tra room ton tai
//        Room room = roomRepository.findById(roomId).orElseThrow(() -> new Exception("ROOM NOT EXIST"));
//        // Kiem tra la thanh vien cua nhom
//        if(roomMemberRepository.existsByUser_IdAndRoom_Id(Helper.getUserId(), roomId)) {
//            // Kiem tra user hien dang la thanh vien
//            if(roomMemberRepository.existsByUser_IdAndRoom_Id(userId, roomId)) throw new Exception("USER JOINED");
//            RoomMember newMember = RoomMember.builder()
//                    .user(user)
//                    .room(room)
//                    .isAdmin(false)
//                    .build();
//            return new RoomMemberDto(roomMemberRepository.save(newMember));
//        } else throw new Exception("PERMISSION DENY");
//    }

    public List<RoomActivityDto> addMultiMember(Long roomId, List<Long> usersId) throws Exception{
        // Kiem tra room ton tai
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new Exception("ROOM NOT EXIST"));
        List<RoomMemberDto> memberDtoList = new ArrayList<>();
        List<RoomActivityDto> activityDtoList = new ArrayList<>();
        if(usersId == null) throw new Exception("USER LIST MISSED");
        usersId.forEach(userId -> {
            // Kiem tra chua la thanh vien room
            if(!roomMemberRepository.existsByUser_IdAndRoom_Id(userId, roomId)){
                Optional<User> user = userRepository.findById(userId);
                if(user.isPresent()){
                    RoomMember member = RoomMember.builder()
                            .user(user.get())
                            .room(room)
                            .isAdmin(false)
                            .build();
                    try {
                        roomMemberRepository.save(member);
                        activityDtoList.add(roomActivityService.createActivity(room, user.get(), ActivityType.INVITE.getType(), null, null));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        return activityDtoList;
    }

    public RoomActivityDto kickMember(Long userId, Long roomId) throws Exception{
        RoomMember member = roomMemberRepository.findByUser_IdAndRoom_Id(userId, roomId).orElseThrow(() -> new Exception("MEMBER NOT EXIST"));
        if(roomMemberRepository.existsByUser_IdAndRoom_IdAndIsAdminTrue(Helper.getUserId(), member.getRoom().getId())){
            if(member.getUser().getId() == Helper.getUserId()) throw new Exception("CANNOT KICK YOURSELF");
            roomMemberRepository.delete(member);
            return roomActivityService.createActivity(member.getRoom(), member.getUser(), ActivityType.KICK.getType(),null, null);
        } else throw new Exception("PERMISSION DENY");
    }

    public RoomActivityDto quitRoom(Long roomId) throws Exception{
        RoomMember me = roomMemberRepository.findByUser_IdAndRoom_Id(Helper.getUserId(), roomId).orElseThrow(() -> new Exception("MEMBER NOT EXIST"));
        if(me.isAdmin()) throw new Exception("ROOM ADMIN CANNOT QUIT");
        roomMemberRepository.delete(me);
        return roomActivityService.createActivity(me.getRoom(), null, ActivityType.QUIT.getType(), null, null);
    }
}
