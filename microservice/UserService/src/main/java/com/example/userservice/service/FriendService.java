package com.example.userservice.service;

import com.example.userservice.dto.FriendInvitationDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.Friend;
import com.example.userservice.entity.User;
import com.example.userservice.enums.FriendStatus;
import com.example.userservice.repository.FriendRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    public List<UserDto> suggestFriend(int size){
        List<UserDto> suggestion = new ArrayList<>();
        // Lấy ngẫu nhiên 5 bạn
        Pageable pageable = PageRequest.of(0, 5);
        List<Friend> randomFriendList = friendRepository.findRandomFriend(Helper.getUserId(), pageable);
        if(randomFriendList.isEmpty()){
            List<UserDto> randomUserList = userRepository.getRandomUser(PageRequest.of(0, 25))
                    .stream()
                    .filter(item -> item.getId() != Helper.getUserId())
                    .map(item -> new UserDto(item))
                    .collect(Collectors.toList());
            suggestion.addAll(randomUserList);
        } else{
            List<User> randomUserList = randomFriendList
                    .stream()
                    .map(friend -> {
                        if(friend.getSender().getId() != Helper.getUserId()) return friend.getSender();
                        else return friend.getReceiver();
                    })
                    .collect(Collectors.toList());

            // Lấy mỗi người 5 người bạn ngẫu nhiên
            for(User user : randomUserList){
                List<User> list = friendRepository.findRandomFriend(user.getId(), PageRequest.of(0, 6))
                        .stream().map(f -> {
                            if(f.getSender().getId() != user.getId()) return f.getSender();
                            else return f.getReceiver();
                        })
                        .collect(Collectors.toList());
                suggestion.addAll(list.stream().filter(item -> item.getId() != Helper.getUserId()).map(item -> new UserDto(item)).limit(5).collect(Collectors.toList()));
            }
        }
        return scoreFriend(suggestion)
                .stream()
                .limit(size)
                .collect(Collectors.toList());
    }
    private List<UserDto> scoreFriend(List<UserDto> list){
        Map<UserDto, Integer> scoreTable = new HashMap<>();
        User currentUser = Helper.getAccountDetail().getUser();
        list.forEach(item -> {
            int score = 0;
            if(Helper.isSimilar(item.getAddress(), currentUser.getAddress())) score++;
            if(Helper.isSimilar(item.getSchool(), currentUser.getSchool())) score++;
            if(item.isMale() == currentUser.isMale()) score++;
            scoreTable.put(item, score);
        });
        // Sắp xếp giảm dần score
        return scoreTable.entrySet()
                .stream()
                .sorted((a,b) -> b.getValue().compareTo(a.getValue()))
                .map(item -> item.getKey())
                .collect(Collectors.toList());
    }
}
