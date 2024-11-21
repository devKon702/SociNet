package com.example.socinetandroid.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socinetandroid.model.Room;
import com.example.socinetandroid.model.RoomsActivity;

import java.util.ArrayList;
import java.util.List;

public class RoomViewModel extends ViewModel {
    public static final int NORMAL = 0;
    public static final int UPDATING = 1;
    public static final int NEW_ACTIVITY = 2;
    public static final int CHANGED_ACTIVITY = 3;
    public static final int NEW_MEMBER_ADDED = 4;

    private final MutableLiveData<RoomsActivity> liveCurrentActivity = new MutableLiveData<>();
    private final MutableLiveData<RoomsActivity> liveNewActivity = new MutableLiveData<>();
    private final MutableLiveData<Integer> liveState = new MutableLiveData<>(NORMAL);
    private final MutableLiveData<Room> liveRoom = new MutableLiveData<>(null);
    private final MutableLiveData<List<RoomsActivity>> liveActivityList = new MutableLiveData<>(new ArrayList<>());

    public RoomViewModel(){}
    public MutableLiveData<Integer> getLiveState(){
        return this.liveState;
    }
    public RoomsActivity getCurrentActivity(){
        return this.liveCurrentActivity.getValue();
    }
    public RoomsActivity getNewActivity(){
        return this.liveNewActivity.getValue();
    }

    public void updatingActivity(RoomsActivity activity){
        liveCurrentActivity.setValue(activity);
        liveState.setValue(UPDATING);
    }
    public void createdActivity(RoomsActivity activity){
        liveNewActivity.setValue(activity);
        liveState.setValue(NEW_ACTIVITY);
    }
    public void updatedActivity(RoomsActivity activity){
        liveNewActivity.setValue(activity);
        liveState.setValue(CHANGED_ACTIVITY);
    }
    public void reset(){
        liveCurrentActivity.setValue(null);
        liveNewActivity.setValue(null);
        liveActivityList.setValue(new ArrayList<>());
        liveState.setValue(NORMAL);
    }

    public MutableLiveData<Room> getLiveRoom(){
        return this.liveRoom;
    }
    public void setRoom(Room room){
        this.liveRoom.setValue(room);
    }
    public void addedMember(List<RoomsActivity> list){
        this.liveActivityList.setValue(list);
        liveState.setValue(NEW_MEMBER_ADDED);
    }
    public List<RoomsActivity> getActivityList(){
        return liveActivityList.getValue();
    }
}
