package com.example.socinetandroid.interfaces;

import com.example.socinetandroid.adapter.ConversationAdapter;
import com.example.socinetandroid.model.Conversation;

public interface IConversationListener {
    boolean onLongClick(Conversation conversation);
    void onClick(ConversationAdapter.SelfConversationViewHolder holder, Conversation conversation);
}
