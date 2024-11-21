package com.example.socinetandroid.viewmodel;

import android.support.annotation.NonNull;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socinetandroid.model.User;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<User> liveUserInfo = new MutableLiveData<>();
    public ProfileViewModel(){}
    public MutableLiveData<User> getLiveUser(){
        return liveUserInfo;
    }
    public void setUser(@NonNull User user){
        this.liveUserInfo.setValue(user);
    }
}
