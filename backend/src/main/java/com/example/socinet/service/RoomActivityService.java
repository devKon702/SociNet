package com.example.socinet.service;

import com.example.socinet.dto.RoomActivityDto;
import com.example.socinet.dto.RoomMemberDto;
import com.example.socinet.entity.Room;
import com.example.socinet.entity.RoomActivity;
import com.example.socinet.entity.User;
import com.example.socinet.enums.ActivityType;
import com.example.socinet.repository.RoomActivityRepository;
import com.example.socinet.repository.RoomMemberRepository;
import com.example.socinet.repository.RoomRepository;
import com.example.socinet.repository.UserRepository;
import com.example.socinet.util.FileChecker;
import com.example.socinet.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RoomActivityService {
    private final RoomActivityRepository roomActivityRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
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
