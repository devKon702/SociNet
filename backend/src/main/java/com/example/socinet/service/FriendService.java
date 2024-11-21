package com.example.socinet.service;

import com.example.socinet.dto.FriendInvitationDto;
import com.example.socinet.dto.UserDto;
import com.example.socinet.entity.Friend;
import com.example.socinet.entity.User;
import com.example.socinet.enums.FriendStatus;
import com.example.socinet.repository.FriendRepository;
import com.example.socinet.repository.UserRepository;
import com.example.socinet.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    public List<UserDto> getFriendList(Long userId) throws Exception{
        Optional<User> userOpt = userRepository.findById(userId);
        if(userOpt.isEmpty()) throw new Exception("User is not exist");

        List<Friend> friendList = friendRepository.findFiends(userId);
        List<UserDto> result = new ArrayList<>();
        friendList.forEach(friend -> {
            // Kiểm tra sender và receiver, tìm người khác với user cần tìm danh sách bạn bè
            UserDto userDto = new UserDto(friend.getSender().getId() != userId ? friend.getSender() : friend.getReceiver());
            result.add(0,userDto);
        });
        return result;
    }

    public List<FriendInvitationDto> getInvitations() {
        Long userId = Helper.getUserId();
        List<Friend> invitationList = friendRepository.findInvitations(userId);
        List<FriendInvitationDto> result = new ArrayList<>();
        invitationList.forEach(invitation -> result.add(0, new FriendInvitationDto(invitation)));
        return result;
    }

    public FriendInvitationDto makeFriendInvitation(Long id) throws Exception{
        Long currentUserId = Helper.getUserId();
        if(id == currentUserId) throw new Exception("CANNOT CREATE INVITATION");

        // Kiểm tra người nhận tồn tại
        Optional<User> receiverOpt = userRepository.findById(id);
        if(receiverOpt.isEmpty()) throw new Exception("USER NOT EXIST");

        // Kiểm tra đã mời
        Optional<Friend> friendOpt = friendRepository.findInvitation(currentUserId, id);
        if(friendOpt.isEmpty()){
            Friend invitation = Friend.builder()
                    .sender(Helper.getAccountDetail().getUser())
                    .receiver(receiverOpt.get())
                    .isAccepted(false)
                    .build();
            return new FriendInvitationDto(friendRepository.save(invitation));
        } else{
            throw new Exception("INVITATION EXISTED");
        }
    }

    public FriendInvitationDto responseFriendInvitation(Long invitationId, boolean isAccept) throws Exception{
        Optional<Friend> friendOpt = friendRepository.findById(invitationId);
        if(friendOpt.isPresent()){
            // Kiểm tra có phải người gửi lời mời
            if(friendOpt.get().getSender().getId() == Helper.getUserId()) throw new Exception("Sender cannot response");

            if(friendOpt.get().isAccepted()) throw new Exception("ACCEPTED INVITATION");

            // Chấp nhận
            if(isAccept){
                friendOpt.get().setAccepted(true);
                return new FriendInvitationDto(friendRepository.save(friendOpt.get()));
            }
            // Từ chối
            else{
                friendOpt.get().setAccepted(false);
                friendRepository.delete(friendOpt.get());
                return null;
            }
        } else{
            throw new Exception("INVITATION NOT EXIST");
        }
    }

    public String checkIsFriend(Long userId){
        Optional<Friend> invitationOpt = friendRepository.findInvitation(Helper.getUserId(), userId);
        if(invitationOpt.isPresent()){
            if(invitationOpt.get().isAccepted()) return FriendStatus.FRIEND.getType();
            else return FriendStatus.INVITED.getType();
        } else return FriendStatus.NO.getType();
    }
}
