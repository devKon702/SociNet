package com.example.socinetandroid.utils;

import androidx.lifecycle.MutableLiveData;

import com.example.socinetandroid.model.User;

import java.util.ArrayList;
import java.util.List;

public class GlobalData {

    public static User user = null;
    public static List<Long> friendIdList = new ArrayList<>();
}
