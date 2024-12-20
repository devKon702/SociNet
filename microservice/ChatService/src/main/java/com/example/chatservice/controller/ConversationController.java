package com.example.chatservice.controller;

import com.example.chatservice.dto.ConversationDto;
import com.example.chatservice.service.ConversationService;
import com.example.chatservice.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
@AllArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @GetMapping("")
    public ResponseEntity<?> getConversationListWithUser(@RequestParam Long userId,
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
                                                @RequestParam(required = false) String content,
                                                @RequestParam(required = false) MultipartFile file) throws Exception{
        ConversationDto result = conversationService.createConversation(receiverId, content, file);
        return Helper.returnSuccessResponse("Create conversation success", result);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateConversation(@PathVariable Long id,
                                                @RequestParam String content) throws Exception{
        ConversationDto result = conversationService.updateConversation(id, content);
        return Helper.returnSuccessResponse("Update conversation success", result);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> removeConversation(@PathVariable Long id) throws Exception{
        ConversationDto result = conversationService.removeConversation(id);
        return Helper.returnSuccessResponse("Remove conversation success", result);
    }
}
