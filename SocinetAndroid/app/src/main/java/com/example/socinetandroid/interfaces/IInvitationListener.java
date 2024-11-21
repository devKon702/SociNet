package com.example.socinetandroid.interfaces;

import com.example.socinetandroid.model.Invitation;

public interface IInvitationListener {
    void onAccept(Invitation invitation);
    void onReject(Invitation invitation);
}
