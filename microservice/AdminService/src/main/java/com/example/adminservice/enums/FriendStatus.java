package com.example.adminservice.enums;

public enum FriendStatus {
    FRIEND("FRIEND"), INVITED("INVITED"), NO("NO");
    private String type;
    FriendStatus(String type){this.type = type;}
    public String getType(){return this.type;}

}