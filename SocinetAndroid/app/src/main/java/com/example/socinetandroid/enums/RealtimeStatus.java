package com.example.socinetandroid.enums;

public enum RealtimeStatus {
    ONLINE("ONLINE"), OFFLINE("OFFLINE"), STRANGE("STRANGE");
    private String type;
    RealtimeStatus(String type){
        this.type = type;
    }
    public String getString(){
        return this.type;
    }
}
