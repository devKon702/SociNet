package com.example.socinetandroid.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socinetandroid.model.Conversation;

public class ConversationViewModel extends ViewModel {
    public static final int NORMAL = 0;
    public static final int UPDATING = 1;
    public static final int NEW_CONVERSATION = 2;
    public static final int CHANGED_CONVERSATION = 3;

    private final MutableLiveData<Conversation> liveCurrentConversation = new MutableLiveData<>();
    private final MutableLiveData<Conversation> liveNewConversation = new MutableLiveData<>();
    private final MutableLiveData<Integer> liveState = new MutableLiveData<>(NORMAL);

    public ConversationViewModel(){}
    public MutableLiveData<Integer> getLiveState(){
        return this.liveState;
    }
    public Conversation getCurrentConversation(){
        return this.liveCurrentConversation.getValue();
    }
    public Conversation getNewConversation(){
        return this.liveNewConversation.getValue();
    }

    public void updatingConversation(Conversation conversation){
        liveCurrentConversation.setValue(conversation);
        liveState.setValue(UPDATING);
    }
    public void createdConversation(Conversation conversation){
        liveNewConversation.setValue(conversation);
        liveState.setValue(NEW_CONVERSATION);
    }
    public void updatedConversation(Conversation conversation){
        liveNewConversation.setValue(conversation);
        liveState.setValue(CHANGED_CONVERSATION);
    }
    public void reset(){
        liveCurrentConversation.setValue(null);
        liveNewConversation.setValue(null);
        liveState.setValue(NORMAL);
    }
}
