package com.example.socinet.service;

import com.example.socinet.dto.ConversationDto;
import com.example.socinet.entity.Conversation;
import com.example.socinet.entity.User;
import com.example.socinet.repository.ConversationRepository;
import com.example.socinet.repository.UserRepository;
import com.example.socinet.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final FirebaseStorageService storageService;
    private final long MAX_FILE_SIZE = 1 * 1024 * 1024; // 1MB
    private final String[] AVAILABLE_EXTENSIONS = {"png", "jpg", "jpeg", "jfif"};


    public List<ConversationDto> getConversationList(Long userId) throws Exception{
        userRepository.findById(userId).orElseThrow(() -> new Exception("User is not exist"));

        List<Conversation> conversationList = conversationRepository.getConversations(Helper.getUserId(), userId);
        List<ConversationDto> result = new ArrayList<>();
        conversationList.forEach(item -> result.add(0, new ConversationDto(item)));
        return result;
    }

    public ConversationDto getConversation(Long id) throws Exception{
        Conversation conversation = conversationRepository.findById(id).orElseThrow(() -> new Exception("Conversation is not exist"));
        return new ConversationDto(conversation);
    }

    public ConversationDto createConversation(Long receiverId, String content, MultipartFile file) throws Exception{
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new Exception("User is not exist"));
        User sender = Helper.getAccountDetail().getUser();

        if(receiver.getId() == sender.getId()) throw new Exception("Can not create conversation");

        String fileUrl = null;
        if(file != null){
            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            if(!Arrays.stream(AVAILABLE_EXTENSIONS).anyMatch((item) -> item.equals(extension)))
                throw new Exception("Extension " + extension + " is not allowed");

            if(file.getSize() > MAX_FILE_SIZE) throw new Exception("File's size must be <= 3MB");

            fileUrl = storageService.upload("images", file);
        }

        Conversation newConversation = Conversation.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .fileUrl(fileUrl)
                .isActive(true)
                .build();
        return new ConversationDto(conversationRepository.save(newConversation));
    }

    public ConversationDto updateConversation(Long id, String content) throws Exception{
        Conversation conversation = conversationRepository.findById(id).orElseThrow(() -> new Exception("Conversation is not exist"));
        conversation.setContent(content);
        return new ConversationDto(conversationRepository.save(conversation));
    }

    public ConversationDto removeConversation(Long id) throws Exception{
        Conversation conversation = conversationRepository.findById(id).orElseThrow(() -> new Exception("Conversation is not exist"));
        if(conversation.getSender().getId() != Helper.getUserId()) throw new Exception("Not allow to remove this conversation");
        conversation.setActive(false);
        return new ConversationDto(conversationRepository.save(conversation));
    }
}
