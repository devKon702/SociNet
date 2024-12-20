package com.example.socinet.service;

import com.example.socinet.dto.RoomActivityDto;
import com.example.socinet.dto.RoomMemberDto;
import com.example.socinet.entity.Room;
import com.example.socinet.entity.RoomActivity;
import com.example.socinet.entity.RoomMember;
import com.example.socinet.entity.User;
import com.example.socinet.enums.ActivityType;
import com.example.socinet.repository.RoomMemberRepository;
import com.example.socinet.repository.RoomRepository;
import com.example.socinet.repository.UserRepository;
import com.example.socinet.util.Helper;
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

    public List<RoomMemberDto> getMembersOfRoom(Long roomId){
        List<RoomMemberDto> memberListDto = new ArrayList<>();
        roomMemberRepository.findAllByRoom_Id(roomId).forEach((member) -> {
            memberListDto.add(new RoomMemberDto(member));
        });
        return memberListDto;
    }

    public RoomMemberDto addMember(Long userId, Long roomId) throws Exception{
        // Kiem tra user ton tai
        User user = userRepository.findById(userId).orElseThrow(() ->  new Exception("USER NOT EXIST"));
        // Kiem tra room ton tai
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new Exception("ROOM NOT EXIST"));
        // Kiem tra la thanh vien cua nhom
        if(roomMemberRepository.existsByUser_IdAndRoom_Id(Helper.getUserId(), roomId)) {
            // Kiem tra user hien dang la thanh vien
            if(roomMemberRepository.existsByUser_IdAndRoom_Id(userId, roomId)) throw new Exception("USER JOINED");
            RoomMember newMember = RoomMember.builder()
                    .user(user)
                    .room(room)
                    .isAdmin(false)
                    .build();
            return new RoomMemberDto(roomMemberRepository.save(newMember));
        } else throw new Exception("PERMISSION DENY");
    }

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

    public RoomActivityDto kickMember(Long memberId) throws Exception{
        RoomMember member = roomMemberRepository.findById(memberId).orElseThrow(() -> new Exception("MEMBER NOT EXIST"));
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
