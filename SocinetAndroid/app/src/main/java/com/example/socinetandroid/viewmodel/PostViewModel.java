package com.example.socinetandroid.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socinetandroid.model.Post;

public class PostViewModel extends ViewModel {
    public final static int NORMAL = 0;
    public final static int EDITED = 1;
    public final static int DELETED = 2;
    public final static int CREATED = 3;

    private final MutableLiveData<Post> currentPost = new MutableLiveData<>();
    private final MutableLiveData<Post> newPost = new MutableLiveData<>();
    private final MutableLiveData<Integer> state = new MutableLiveData<>(NORMAL);

    public PostViewModel(){
    }

    public Post getCurrentPost() {
        return currentPost.getValue();
    }

    public void setCurrentPost(Post post) {
        currentPost.setValue(post);
    }

    public Post getNewPost(){
        return newPost.getValue();
    }

    public void setNewPost(Post post){
        this.newPost.setValue(post);
    }

    public MutableLiveData<Integer> getState(){
        return state;
    }

    public void setState(int state){
        this.state.setValue(state);
    }

    public void editPost(Post post){
        newPost.setValue(post);
        state.setValue(EDITED);
    }

    public void deletePost(Post post){
        newPost.setValue(post);
        state.setValue(DELETED);
    }
    public void addPost(Post post){
        newPost.setValue(post);
        state.setValue(CREATED);
    }
}
