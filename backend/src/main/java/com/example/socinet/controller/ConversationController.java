package com.example.socinet.controller;

import com.example.socinet.dto.ConversationDto;
import com.example.socinet.service.ConversationService;
import com.example.socinet.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversation")
@AllArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @GetMapping("user/{userId}")
    public ResponseEntity<?> getConversationListWithUser(@PathVariable Long userId,
                                                     @RequestParam(defaultValue ="0") int page,
                                                     @RequestParam(defaultValue = "50") int size) throws Exception{
        List<ConversationDto> result = conversationService.getConversationList(userId);
        return Helper.returnSuccessResponse("Get conversation list success", result);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getConversation(@PathVariable Long id) throws Exception{
        ConversationDto result = conversationService.getConversation(id);
        return Helper.returnSuccessResponse("Get conversation success", result);
    }

    @PostMapping("")
    public ResponseEntity<?> createConversation(@RequestParam Long receiverId,
                                                @RequestParam String content,
                                                @RequestParam MultipartFile file) throws Exception{
        ConversationDto result = conversationService.createConversation(receiverId, content, file);
        return Helper.returnSuccessResponse("Create conversation success", result);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateConversation(@PathVariable Long id,
                                                @RequestParam String content) throws Exception{
        ConversationDto result = conversationService.updateConversation(id, content);
        return Helper.returnSuccessResponse("Update conversation success", result);
    }

    @DeleteMapping("id")
    public ResponseEntity<?> removeConversation(@PathVariable Long id) throws Exception{
        ConversationDto result = conversationService.removeConversation(id);
        return Helper.returnSuccessResponse("Remove conversation success", result);
    }
}
