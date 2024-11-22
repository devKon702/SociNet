package com.example.socinetandroid.viewmodel;

import android.support.annotation.NonNull;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socinetandroid.enums.RealtimeStatus;
import com.example.socinetandroid.model.Conversation;
import com.example.socinetandroid.model.Invitation;
import com.example.socinetandroid.model.RealtimeChat;
import com.example.socinetandroid.model.RealtimeRoom;
import com.example.socinetandroid.model.RealtimeUser;
import com.example.socinetandroid.model.Room;
import com.example.socinetandroid.model.RoomsActivity;
import com.example.socinetandroid.model.User;

import java.util.ArrayList;
import java.util.List;

public class AppViewModel extends ViewModel {
    private final MutableLiveData<User> liveUser = new MutableLiveData<>();
    private final MutableLiveData<List<Long>> liveFriendByUserIdList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> liveNewMessageNumber = new MutableLiveData<>(0);
    private final MutableLiveData<List<Conversation>> liveNewConversationList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<RoomsActivity>> liveNewRoomActivityList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<RealtimeUser>> liveRealtimeUserList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<RealtimeRoom>> liveRealtimeRoomList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<RealtimeChat>> liveRealtimeChatList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Invitation>> liveRealtimeInvitationList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<RealtimeUser> liveNewestRealtimeUser = new MutableLiveData<>();
    private final MutableLiveData<Conversation> liveNewestConversation = new MutableLiveData<>();
    private final MutableLiveData<RoomsActivity> liveNewestRoomsActivity = new MutableLiveData<>();
    private final MutableLiveData<Invitation> liveNewestFriendInvitation = new MutableLiveData<>();

    public MutableLiveData<User> getLiveUser() {
        return liveUser;
    }
    public User getUser(){
        return liveUser.getValue();
    }
    public void setUser(@NonNull User user){
        liveUser.postValue(user);
    }

    public MutableLiveData<Integer> getLiveNewMessageNumber() {
        return liveNewMessageNumber;
    }

    public MutableLiveData<List<Long>> getLiveFriendByUserIdList() {
        return liveFriendByUserIdList;
    }
    public void setFriendList(@NonNull List<Long> userIdList){
        liveFriendByUserIdList.postValue(userIdList);
    }

    public MutableLiveData<List<Conversation>> getLiveNewConversationList() {
        return liveNewConversationList;
    }

    public MutableLiveData<List<RoomsActivity>> getLiveNewRoomActivityList() {
        return liveNewRoomActivityList;
    }

    public MutableLiveData<List<RealtimeUser>> getLiveRealtimeUserList() {
        return liveRealtimeUserList;
    }

    public MutableLiveData<RealtimeUser> getLiveNewestRealtimeUser() {
        return liveNewestRealtimeUser;
    }

    public MutableLiveData<Conversation> getLiveNewestConversation() {
        return liveNewestConversation;
    }

    public MutableLiveData<RoomsActivity> getLiveNewestRoomsActivity() {
        return liveNewestRoomsActivity;
    }

    public MutableLiveData<Invitation> getLiveNewestFriendInvitation() {
        return liveNewestFriendInvitation;
    }

    public MutableLiveData<List<RealtimeRoom>> getLiveRealtimeRoomList() {
        return liveRealtimeRoomList;
    }

    public MutableLiveData<List<RealtimeChat>> getLiveRealtimeChatList() {
        return liveRealtimeChatList;
    }

    public MutableLiveData<List<Invitation>> getLiveRealtimeInvitationList() {
        return liveRealtimeInvitationList;
    }

    public int getSizeOfLiveList(List<?> list){
        if(list == null) return 0;
        else return list.size();
    }

    public void newOnlineUser(@NonNull User user) {
        if(!liveFriendByUserIdList.getValue().contains(user.getId())) return;
        liveNewestRealtimeUser.postValue(new RealtimeUser(user, RealtimeStatus.ONLINE, "", false));
        for(RealtimeUser item : liveRealtimeUserList.getValue()){
            if(item.getUser().getId() == user.getId()){
                item.setStatus(RealtimeStatus.ONLINE);
                break;
            }
        }
        liveRealtimeUserList.postValue(new ArrayList<>(liveRealtimeUserList.getValue()));
    }

    public void newOfflineUser(@NonNull User user) {
        if(!liveFriendByUserIdList.getValue().contains(user.getId())) return;
        liveNewestRealtimeUser.postValue(new RealtimeUser(user, RealtimeStatus.OFFLINE, "", false));
        for(RealtimeUser item : liveRealtimeUserList.getValue()){
            if(item.getUser().getId() == user.getId()){
                item.setStatus(RealtimeStatus.OFFLINE);
                break;
            }
        }
        liveRealtimeUserList.postValue(new ArrayList<>(liveRealtimeUserList.getValue()));
    }
    public void newStrangeUser(@NonNull User user){
        liveNewestRealtimeUser.postValue(new RealtimeUser(user, RealtimeStatus.STRANGE, "", false));
        liveRealtimeUserList.getValue().add(0, liveNewestRealtimeUser.getValue());
        liveRealtimeUserList.postValue(new ArrayList<>(liveRealtimeUserList.getValue()));
    }
    public void filterFriendStatus(@NonNull List<Long> onlineUserId,@NonNull List<Long> hasUnreadUserId){
        List<RealtimeUser> updatedList = new ArrayList<>();
        for(RealtimeUser item : liveRealtimeUserList.getValue()){
            if(onlineUserId.contains(item.getUser().getId())){
                item.setStatus(RealtimeStatus.ONLINE);
            } else if(liveFriendByUserIdList.getValue().contains(item.getUser().getId())){
                item.setStatus(RealtimeStatus.OFFLINE);
            } else {
                item.setStatus(RealtimeStatus.STRANGE);
            }

            if(hasUnreadUserId.contains(item.getUser().getId())){
                item.setHasUnread(true);
            } else {
                item.setHasUnread(false);
            }
            updatedList.add(item);
        }
        liveRealtimeUserList.postValue(updatedList);
    }

    public void filterRoomStatus(@NonNull List<Long> unreadRoomIdList){
        liveRealtimeRoomList.getValue().forEach(item -> {
           if(unreadRoomIdList.contains(item.getRoom().getId())){
               item.setHasUnread(true);
           } else {
               item.setHasUnread(false);
           }
        });
        liveRealtimeRoomList.postValue(new ArrayList<>(liveRealtimeRoomList.getValue()));
    }

    public void newConversation(@NonNull Conversation conversation){
        liveNewestConversation.postValue(conversation);
        if(conversation.getSenderId() == liveUser.getValue().getId()){
            return;
        } else {
            // neu sender có trong list user nhắn tin
            for(RealtimeUser realtimeUser : liveRealtimeUserList.getValue()){
                if(realtimeUser.getUser().getId() == conversation.getSenderId()){
                    realtimeUser.setNewMessage(conversation.getContent());
                    realtimeUser.setHasUnread(true);
                    liveRealtimeUserList.postValue(new ArrayList<>(liveRealtimeUserList.getValue()));
                    return;
                }
            }
            // Nếu sender ko có trong list user nhắn tin - người lạ
            liveNewestRealtimeUser.postValue(new RealtimeUser(conversation.getSender(), RealtimeStatus.STRANGE, conversation.getContent(), true));
            liveRealtimeUserList.getValue().add(0, liveNewestRealtimeUser.getValue());
            liveRealtimeUserList.postValue(new ArrayList<>(liveRealtimeUserList.getValue()));
        }
    }

    public void newUpdateConversation(@NonNull Conversation conversation){
        liveNewestConversation.postValue(conversation);
    }
    public void readConversation(long userId){
        liveRealtimeUserList.getValue().forEach(realtimeUser -> {
            if(realtimeUser.getUser().getId() == userId){
                realtimeUser.setNewMessage("");
                realtimeUser.setHasUnread(false);
                liveRealtimeUserList.postValue(new ArrayList<>(liveRealtimeUserList.getValue()));
            }
        });
    }

    public void newRoomMessage(@NonNull RoomsActivity message){
        if(message.getType().equals(RoomsActivity.CHAT)){
            liveNewestRoomsActivity.postValue(message);
            for(RealtimeRoom realtimeRoom : liveRealtimeRoomList.getValue()){
                if(realtimeRoom.getRoom().getId() == message.getRoomId()){
                    realtimeRoom.setNewMessage(message.getContent());
                    realtimeRoom.setHasUnread(true);
                    liveRealtimeRoomList.postValue(new ArrayList<>(liveRealtimeRoomList.getValue()));
                    return;
                }
            }
        }
    }
    public void newUpdateRoomMessage(@NonNull RoomsActivity message){
        liveNewestRoomsActivity.postValue(message);
    }
    public void readRoomActivities(long roomId){
        for(RealtimeRoom realtimeRoom : liveRealtimeRoomList.getValue()){
            if(realtimeRoom.getRoom().getId() == roomId){
                realtimeRoom.setHasUnread(false);
                realtimeRoom.setNewMessage("");
                liveRealtimeRoomList.postValue(new ArrayList<>(liveRealtimeRoomList.getValue()));
                return;
            }
        }
    }
    public void inviteToRoom(Room room){
        RealtimeRoom realtimeRoom = new RealtimeRoom(room, "", false);
        liveRealtimeRoomList.getValue().add(0, realtimeRoom);
        liveRealtimeRoomList.postValue(new ArrayList<>(liveRealtimeRoomList.getValue()));
    }
    public void newMember(RoomsActivity activity){
        if(activity.getType().equals(RoomsActivity.INVITE)){
            liveNewestRoomsActivity.postValue(activity);
        }
    }
    public void newMemberQuit(RoomsActivity activity){
        if(activity.getType().equals(RoomsActivity.QUIT)){
            liveNewestRoomsActivity.postValue(activity);
        }
    }
    public void updateRoom(Room room){
        for(RealtimeRoom item : liveRealtimeRoomList.getValue()){
            if(item.getRoom().getId() == room.getId()){
                item = new RealtimeRoom(room, "", false);
                liveRealtimeRoomList.postValue(new ArrayList<>(liveRealtimeRoomList.getValue()));
                return;
            }
        }
    }
    public void quitRoom(long roomId){
        for(int i=0; i<liveRealtimeRoomList.getValue().size(); i++){
            if(liveRealtimeRoomList.getValue().get(i).getRoom().getId() == roomId){
                liveRealtimeRoomList.getValue().remove(i);
                liveRealtimeRoomList.postValue(new ArrayList<>(liveRealtimeRoomList.getValue()));
                return;
            }
        }
    }
    public void disableRoom(long roomId){
        for(int i=0; i<liveRealtimeRoomList.getValue().size(); i++){
            if(liveRealtimeRoomList.getValue().get(i).getRoom().getId() == roomId){
                liveRealtimeRoomList.getValue().remove(i);
                liveRealtimeRoomList.postValue(new ArrayList<>(liveRealtimeRoomList.getValue()));
                return;
            }
        }
    }

    public void newFriendInvitation(Invitation invitation){
        liveNewestFriendInvitation.postValue(invitation);
        liveRealtimeInvitationList.getValue().add(0, invitation);
        liveRealtimeInvitationList.postValue(new ArrayList<>(liveRealtimeInvitationList.getValue()));
    }
    public void removeFriendInvitation(Invitation invitation){
        for(int i=0; i<liveRealtimeInvitationList.getValue().size(); i++){
            Invitation liveItem = liveRealtimeInvitationList.getValue().get(i);
            if(liveItem.getId() == invitation.getId()){
                liveRealtimeInvitationList.getValue().remove(i);
                liveRealtimeInvitationList.postValue(new ArrayList<>(liveRealtimeInvitationList.getValue()));
                return;
            }
        }
    }
    public void newAcceptedFriendInvitation(@NonNull User user){
        liveFriendByUserIdList.getValue().add(user.getId());
        liveRealtimeUserList.getValue().add(0, new RealtimeUser(user, RealtimeStatus.ONLINE, "", false));
        liveRealtimeUserList.postValue(new ArrayList<>(liveRealtimeUserList.getValue()));
    }
    public void newFriend(@NonNull User user){
        for(Invitation item : liveRealtimeInvitationList.getValue()){
            if(item.getSender().getId() == user.getId()){
                item.setAccepted(true);
                liveRealtimeInvitationList.postValue(new ArrayList<>(liveRealtimeInvitationList.getValue()));
                break;
            }
        }
        liveFriendByUserIdList.getValue().add(user.getId());
        liveRealtimeUserList.getValue().add(0, new RealtimeUser(user, RealtimeStatus.OFFLINE, "", false));
        liveRealtimeUserList.postValue(new ArrayList<>(liveRealtimeUserList.getValue()));
    }
}
