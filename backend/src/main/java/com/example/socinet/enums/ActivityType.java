package com.example.socinet.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ActivityType {
    CHAT("CHAT"), NOTIFY("NOTIFY"), INVITE("INVITE"), JOIN("JOIN"), QUIT("QUIT"), KICK("KICK");

    private String type;

    ActivityType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

    public static boolean isActivityType(String type){
        List<String> stringList = Arrays.asList("CHAT", "NOTIFY", "INVITE", "JOIN", "QUIT", "KICK");
        return stringList.contains(type);
    }
}
