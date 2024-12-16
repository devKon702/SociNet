package com.example.userservice.service;

import com.example.userservice.dto.FriendInvitationDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserItemProfile;
import com.example.userservice.entity.*;
import com.example.userservice.enums.FriendStatus;
import com.example.userservice.repository.*;
import com.example.userservice.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;
    private final ConversationRepository conversationRepository;

    public List<UserDto> getFriendList(Long userId) throws Exception{
        Optional<User> userOpt = userRepository.findById(userId);
        if(userOpt.isEmpty()) throw new Exception("User is not exist");

        List<Friend> friendList = friendRepository.findAllFriendsByUserId(userId);
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

    public List<UserDto> recommendFriend(int size){
        List<UserDto> suggestionList = new ArrayList<>();
        // Lấy danh sách bạn bè hiện tại
        List<UserDto> friendList = friendRepository.findAllFriendsByUserId(Helper.getUserId())
                .stream()
                .map(friend -> {
                    if (friend.getSender().getId() != Helper.getUserId()) return friend.getSender();
                    else return friend.getReceiver();
                })
                .map(UserDto::new)
                .toList();
        // Lấy ds bạn bè của bạn bè hiện tại
        Set<UserDto> friendOfFriendSet = new HashSet<>();
        friendList.forEach(friend -> {
            friendOfFriendSet.addAll(
                    friendRepository.findAllFriendsByUserId(friend.getId())
                            .stream()
                            .map(item -> {
                                if (item.getSender().getId() != Helper.getUserId()) return item.getSender();
                                else return item.getReceiver();
                            })
                            .map(UserDto::new)
                            .toList()
            );
        });
        // Chỉ lấy những người ko phải bạn bè
        List<UserDto> strangeFriendOfFriendList = new ArrayList<>(friendOfFriendSet
                .stream()
                .filter(item -> friendList.stream().noneMatch(friend -> item.getId().equals(item.getId())))
                .toList());
        // Trộn để tạo độ mới cho recommendList trong trường hợp 2 item bằng điểm nhau
        Collections.shuffle(strangeFriendOfFriendList);
        // Lấy danh sách user ta đã tương tác
        List<UserDto> reactedUsers = reactionRepository.findAllByUser_Id(Helper.getUserId())
                .stream()
                .map(item -> item.getPost().getUser())
                .filter(item -> item.getId() != Helper.getUserId())
                .map(UserDto::new)
                .distinct()
                .toList();
        List<UserDto> commentedUsers = commentRepository.findAllByUser_Id(Helper.getUserId())
                .stream()
                .map(item -> item.getPost().getUser())
                .filter(item -> item.getId() != Helper.getUserId())
                .map(UserDto::new)
                .distinct()
                .toList();
        Set<UserDto> interactedUserSet = new HashSet<>();
        // Tổng hợp các user đã tương tác + lọc những user chưa kết bạn
        interactedUserSet.addAll(reactedUsers
                .stream()
                .filter(item -> friendList.stream().noneMatch(friend -> friend.getId().equals(item.getId())))
                .toList());
        interactedUserSet.addAll(commentedUsers
                .stream()
                .filter(item -> friendList.stream().noneMatch(friend -> friend.getId().equals(item.getId())))
                .toList());

        // Lấy danh sách user chung nhóm + lọc user chưa kết bạn
        Set<UserDto> sameGroupUserSet = new HashSet<>();
        roomMemberRepository.findAllByUser_Id(Helper.getUserId())
                .stream().map(item -> item.getRoom().getMembers())
                .forEach(memberList -> {
                    memberList.forEach(member -> {
                        if(member.getUser().getId() != Helper.getUserId() && friendList.stream().noneMatch(friend -> friend.getId().equals(member.getUser().getId())))
                            sameGroupUserSet.add(new UserDto(member.getUser()));
                    });
                });
        // Lấy danh sách user đã trò chuyện + lọc user chưa kết bạn (xử lí trong query)
        List<UserDto> chattedStrangeUserList = new ArrayList<>();
        conversationRepository.getChattedStrangeUserId(Helper.getUserId())
                .forEach(userId -> {
                    userRepository.findById(userId)
                            .ifPresent(userItem -> chattedStrangeUserList.add(new UserDto(userItem)));
                });

        // Tạo item profile
        List<UserItemProfile> itemProfileList = new ArrayList<>();
        strangeFriendOfFriendList.forEach(item -> {
            UserItemProfile itemProfile = new UserItemProfile(item);
            itemProfile.setSameFriend(1);
            itemProfileList.add(itemProfile);
        });
        interactedUserSet.forEach(item -> {
            int index = itemProfileList.indexOf(new UserItemProfile(item));
            if(index != -1){
                itemProfileList.get(index).setInteracted(1);
            } else {
                UserItemProfile itemProfile = new UserItemProfile(item);
                itemProfile.setInteracted(1);
                itemProfileList.add(itemProfile);
            }
        });
        sameGroupUserSet.forEach(item -> {
            int index = itemProfileList.indexOf(new UserItemProfile(item));
            if(index != -1){
                itemProfileList.get(index).setSameGroup(1);
            } else {
                UserItemProfile itemProfile = new UserItemProfile(item);
                itemProfile.setSameGroup(1);
                itemProfileList.add(itemProfile);
            }
        });
        chattedStrangeUserList.forEach(item -> {
            UserItemProfile itemProfile = new UserItemProfile(item);
            itemProfile.setChatted(1);
            itemProfileList.add(itemProfile);
        });

        if(itemProfileList.size() < size){
            List<Long> excludeIds = new ArrayList<>();
            excludeIds.addAll(friendList
                    .stream()
                    .map(item -> item.getId())
                    .toList());
            excludeIds.addAll(itemProfileList
                    .stream()
                    .map(item -> item.getUser().getId())
                    .toList());
            userRepository.getRandomUserExclude(excludeIds, PageRequest.of(0,size - itemProfileList.size()))
                    .stream()
                    .map(UserDto::new)
                    .forEach(user -> itemProfileList.add(new UserItemProfile(user)));
        }

        return scoreFriend(itemProfileList)
                .stream()
                .limit(size)
                .collect(Collectors.toList());
    }
    private List<UserDto> scoreFriend(List<UserItemProfile> list){
        int SAME_FRIEND_WEIGHT = 1;
        int SAME_GROUP_WEIGHT = 2;
        int INTERACTED_WEIGHT = 2;
        int CHATTED_WEIGHT = 3;

        Map<UserItemProfile, Integer> scoreTable = new HashMap<>();
        list.forEach(item -> {
            int score = 0;
            score += item.getSameFriend() * SAME_FRIEND_WEIGHT;
            score += item.getSameGroup() * SAME_GROUP_WEIGHT;
            score += item.getInteracted() * INTERACTED_WEIGHT;
            score += item.getChatted() * CHATTED_WEIGHT;
            scoreTable.put(item, score);
        });

        // Sắp xếp giảm dần score
        return scoreTable.entrySet()
                .stream()
                .sorted((a,b) -> b.getValue().compareTo(a.getValue()))
                .map(item -> item.getKey().getUser())
                .collect(Collectors.toList());
    }
}
