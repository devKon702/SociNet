package com.example.socinetandroid.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socinetandroid.model.Comment;

public class CommentViewModel extends ViewModel {
    public static final int NORMAL = 0;
    public static final int UPDATING = 1;
    public static final int REPLYING = 2;
    public static final int NEW_COMMENT = 3;
    public static final int NEW_REPLY_COMMENT = 4;
    public static final int CHANGED_COMMENT = 5;
    public static final int REMOVED_COMMENT = 6;

    private final MutableLiveData<Comment> liveCurrentComment = new MutableLiveData<>();
    private final MutableLiveData<Comment> liveNewComment = new MutableLiveData<>();
    private final MutableLiveData<Integer> liveState = new MutableLiveData<>(NORMAL);

    public CommentViewModel(){
    }

    public MutableLiveData<Integer> getLiveState(){
        return liveState;
    }
    public Comment getCurrentComment(){
        return liveCurrentComment.getValue();
    }
    public Comment getNewComment(){return liveNewComment.getValue();}
    public void updatingComment(Comment comment){
        liveCurrentComment.setValue(comment);
        liveState.setValue(UPDATING);
    }
    public void replyingComment(Comment comment){
        liveCurrentComment.setValue(comment);
        liveState.setValue(REPLYING);
    }

    public void createdComment(Comment comment){
        liveNewComment.setValue(comment);
        liveState.setValue(NEW_COMMENT);
    }
    public void updatedComment(Comment comment){
        liveNewComment.setValue(comment);
        liveState.setValue(CHANGED_COMMENT);
    }
    public void repliedComment(Comment comment){
        liveNewComment.setValue(comment);
        liveState.setValue(NEW_REPLY_COMMENT);
    }
    public void deletedComment(Comment comment){
        liveNewComment.setValue(comment);
        liveState.setValue(REMOVED_COMMENT);
    }
    public void reset(){
        liveCurrentComment.setValue(null);
        liveNewComment.setValue(null);
        liveState.setValue(NORMAL);
    }
}
