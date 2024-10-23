package com.example.socinet.controller;

import com.example.socinet.dto.RoomActivityDto;
import com.example.socinet.dto.RoomDto;
import com.example.socinet.dto.RoomMemberDto;
import com.example.socinet.entity.Room;
import com.example.socinet.entity.RoomMember;
import com.example.socinet.enums.ActivityType;
import com.example.socinet.repository.RoomRepository;
import com.example.socinet.service.RoomActivityService;
import com.example.socinet.service.RoomMemberService;
import com.example.socinet.service.RoomService;
import com.example.socinet.util.Helper;
import lombok.AllArgsConstructor;
import org.checkerframework.common.util.report.qual.ReportUnqualified;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("api/v1/rooms")
public class RoomController {
    private final RoomService roomService;
    private final RoomMemberService roomMemberService;
    private final RoomActivityService roomActivityService;
    private final RoomRepository roomRepository;

    @GetMapping("")
    public ResponseEntity<?> getRoomsOfMe(){
        List<RoomDto> roomDtoList = roomService.getJoinedRooms();
        return Helper.returnSuccessResponse("Get room success", roomDtoList);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getRoom(@PathVariable Long id) throws Exception{
        return Helper.returnSuccessResponse("Get room success", roomService.getRoom(id));
    }

    @GetMapping("code/{code}")
    public ResponseEntity<?> getRoomWithCode(@PathVariable String code){
        return null;
    }

    @PostMapping("")
    public ResponseEntity<?> createRoom(@RequestParam String name,
                                        @RequestParam(required = false) MultipartFile avatarFile) throws Exception{
        RoomDto result = roomService.createRoom(name, null, avatarFile);
        return Helper.returnSuccessResponse("Create room success", result);

    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Long id,
                                        @RequestParam String name,
                                        @RequestParam(required = false) MultipartFile avatarFile) throws Exception{
        RoomDto result = roomService.updateRoom(id, name, avatarFile);
        return Helper.returnSuccessResponse("Update room info success", result);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id) throws Exception{
        RoomDto result = roomService.deleteRoom(id);
        return Helper.returnSuccessResponse("Delete room success", result);
    }

    @PostMapping("{id}/invite")
    public ResponseEntity<?> invite(@PathVariable Long id, @RequestParam List<Long> usersId) throws Exception{
        List<RoomActivityDto> result = roomMemberService.addMultiMember(id, usersId);
        return Helper.returnSuccessResponse("Invite users success", result);
    }

    @DeleteMapping("kick")
    public ResponseEntity<?> kickMember(@RequestParam Long memberId) throws Exception{
        RoomActivityDto result = roomMemberService.kickMember(memberId);
        return Helper.returnSuccessResponse("Kick member success", result);
    }

    @PostMapping("{id}/join")
    public ResponseEntity<?> join(@PathVariable Long id){
        return null;
    }

    @DeleteMapping("{id}/quit")
    public ResponseEntity<?> quit(@PathVariable Long id) throws Exception{
        RoomActivityDto result = roomMemberService.quitRoom(id);
        return Helper.returnSuccessResponse("Quit room success", result);
    }

    @GetMapping("{roomId}/activities")
    public ResponseEntity<?> getActivities(@PathVariable Long roomId,
                                           @RequestParam(defaultValue = "-1") Long lastIndex,
                                           @RequestParam(defaultValue = "20") int size) throws Exception{
        List<RoomActivityDto> result = roomActivityService.getActivities(roomId, lastIndex, size);
        return Helper.returnSuccessResponse("Get activities success", result);
    }

    @PostMapping("{roomId}/chat")
    public ResponseEntity<?> createChat(@PathVariable Long roomId,
                                            @RequestParam(required = false, defaultValue = "") String content,
                                            @RequestParam(required = false) MultipartFile file) throws Exception{
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new Exception("ROOM NOT EXIST"));
        RoomActivityDto result = roomActivityService.createActivity(room, null, ActivityType.CHAT.getType(), content, file);
        return Helper.returnSuccessResponse("Create chat success", result);
    }

    @PutMapping("chat/{id}")
    public ResponseEntity<?> updateChat(@PathVariable Long id, @RequestParam String content) throws Exception{
        RoomActivityDto result = roomActivityService.updateActivity(id, content);
        return Helper.returnSuccessResponse("Update message success", result);
    }

    @DeleteMapping("chat/{id}")
    public ResponseEntity<?> removeChat(@PathVariable Long id) throws Exception{
        RoomActivityDto result = roomActivityService.removeActivity(id);
        return Helper.returnSuccessResponse("Remove message success", result);
    }
}
