package com.example.chatservice.service;

import com.example.chatservice.dto.RoomDto;
import com.example.chatservice.entity.Room;
import com.example.chatservice.entity.RoomMember;
import com.example.chatservice.repository.RoomMemberRepository;
import com.example.chatservice.repository.RoomRepository;
import com.example.chatservice.util.FileChecker;
import com.example.chatservice.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final FirebaseStorageService storageService;
    private final RoomMemberService roomMemberService;
    private final RoomActivityService roomActivityService;

    public RoomDto getRoom(Long id) throws Exception{
        Room room = roomRepository.findById(id).orElseThrow(() -> new Exception("ROOM NOT EXIST"));
        return new RoomDto(room);
    }

    public List<RoomDto> getJoinedRooms(){
        List<RoomMember> roomMember =  roomMemberRepository.findAllByUser_Id(Helper.getUserId());
        List<RoomDto> roomDtoList = new ArrayList<>();
        roomMember.forEach(item -> roomDtoList.add(new RoomDto(item.getRoom())));
        return roomDtoList;
    }

    public RoomDto createRoom(String name, String code, MultipartFile roomAvatar) throws Exception{
        if(name.isEmpty()) throw new Exception("NAME CANNOT BE EMPTY");
        String fileUrl = null;
        // Kiem tra image
        if(FileChecker.isValidImage(roomAvatar, FileChecker.MAX_IMAGE_SIZE)){
            fileUrl = storageService.upload("images", roomAvatar);
        }
//        // Kiem tra video
//        else if (FileChecker.isValidVideo(roomAvatar, FileChecker.MAX_VIDEO_SIZE)) {
//            fileUrl = storageService.upload("videos", roomAvatar);
//        }
        // Khong phai image va video
        else if(roomAvatar != null) throw new Exception("UNSUPPORTED FILE");

        Room newRoom = Room.builder()
                .name(name)
                .avatarUrl(fileUrl)
                .isActive(true)
                .build();
        newRoom = roomRepository.save(newRoom);

        RoomMember roomAdmin = RoomMember.builder()
                .user(Helper.getAccountDetail().getUser())
                .room(newRoom)
                .isAdmin(true)
                .build();
        roomAdmin = roomMemberRepository.save(roomAdmin);
        return new RoomDto(roomAdmin.getRoom());
    }

    public RoomDto updateRoom(Long roomId, String name, MultipartFile roomAvatar) throws Exception{
        if(roomMemberRepository.existsByUser_IdAndRoom_IdAndIsAdminTrue(Helper.getUserId(), roomId)){
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new Exception("ROOM NOT EXIST"));
            if(name.isEmpty()) throw new Exception("NAME CANNOT BE EMPTY");
            else room.setName(name);

            String fileUrl = null;
            // Kiem tra image
            if(FileChecker.isValidImage(roomAvatar, FileChecker.MAX_IMAGE_SIZE)){
                fileUrl = storageService.upload("images", roomAvatar);
            }
            // Kiem tra video
//            else if (FileChecker.isValidVideo(roomAvatar, FileChecker.MAX_VIDEO_SIZE)) {
//                fileUrl = storageService.upload("videos", roomAvatar);
//            }
            // Khong phai image va video
            else if(roomAvatar != null) throw new Exception("UNSUPPORTED FILE");

            if(fileUrl != null) room.setAvatarUrl(fileUrl);
            return new RoomDto(roomRepository.save(room));
        } else throw new Exception("PERMISSION DENY");
    }

    public RoomDto deleteRoom(Long roomId) throws Exception{
        if(roomMemberRepository.existsByUser_IdAndRoom_IdAndIsAdminTrue(Helper.getUserId(), roomId)){
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new Exception("ROOM NOT EXIST"));
            room.setActive(false);
            return new RoomDto(roomRepository.save(room));
        }else throw new Exception("PERMISSION DENY");
    }
}
