package com.example.chatservice.service;

import com.example.chatservice.dto.RoomActivityDto;
import com.example.chatservice.entity.Room;
import com.example.chatservice.entity.RoomActivity;
import com.example.chatservice.entity.User;
import com.example.chatservice.enums.ActivityType;
import com.example.chatservice.repository.RoomActivityRepository;
import com.example.chatservice.repository.RoomMemberRepository;
import com.example.chatservice.repository.RoomRepository;
import com.example.chatservice.repository.UserRepository;
import com.example.chatservice.util.FileChecker;
import com.example.chatservice.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RoomActivityService {
    private final RoomActivityRepository roomActivityRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final FirebaseStorageService storageService;

    public List<RoomActivityDto> getActivities(Long roomId, Long lastIndex, int size) throws Exception{
        // Kiem tra la thanh vien nhom
        if(roomMemberRepository.existsByUser_IdAndRoom_Id(Helper.getUserId(),roomId)){
            List<RoomActivity> activities = roomActivityRepository.findAllByRoom_Id(roomId);
            List<RoomActivityDto> activityDtos = new ArrayList<>();
            activities.forEach(item -> {activityDtos.add(0,new RoomActivityDto(item));});
            return activityDtos;
        } else throw new Exception("PERMISSION DENY");
    }

    public RoomActivityDto createActivity(Room room, User receiver, String type, String content, MultipartFile file) throws Exception{
        if(!ActivityType.isActivityType(type)) throw new Exception("INVALID TYPE");
        if(type.equals(ActivityType.CHAT.getType()) && (content == null || content.isEmpty()) && file == null) throw new Exception("EMPTY CHAT");

        String fileUrl = null;
        // Kiem tra file la file valid image
        if(FileChecker.isValidImage(file, FileChecker.MAX_IMAGE_SIZE)){
            fileUrl = storageService.upload("images", file);
        } else if(file != null) throw new Exception("UNSUPPORTED FILE");

        RoomActivity activity = RoomActivity.builder()
                .room(room)
                .sender(Helper.getAccountDetail().getUser())
                .receiver(receiver)
                .type(type)
                .content(content)
                .fileUrl(fileUrl)
                .isActive(true)
                .build();
        return new RoomActivityDto(roomActivityRepository.save(activity));
    }

    public RoomActivityDto updateActivity(Long id, String content) throws Exception{
        RoomActivity activity = roomActivityRepository.findById(id).orElseThrow(() -> new Exception("ACTIVITY NOT EXIST"));
        if(!activity.getType().equals(ActivityType.CHAT.getType())) throw new Exception("ACTIVITY CANNOT UPDATE");
        if(activity.getSender().getId() != Helper.getUserId()) throw new Exception("CANNOT UPDATE OTHER'S ACTIVITY");
        if(content.isEmpty()) throw new Exception("CONTENT CANNOT BE EMPTY");
        activity.setContent(content);
        return new RoomActivityDto(roomActivityRepository.save(activity));
    }

    public RoomActivityDto removeActivity(Long id) throws Exception{
        RoomActivity activity = roomActivityRepository.findById(id).orElseThrow(() -> new Exception("ACTIVITY NOT EXIST"));
        if(activity.getSender().getId() != Helper.getUserId()) throw new Exception("CANNOT REMOVE OTHER'S ACTIVITY");
        if(!activity.getType().equals(ActivityType.CHAT.getType())) throw new Exception("ACTIVITY CANNOT REMOVE");
        activity.setActive(false);
        return new RoomActivityDto(roomActivityRepository.save(activity));
    }
}
