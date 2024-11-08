package com.example.socinetandroid.interfaces;

import androidx.recyclerview.widget.RecyclerView;

import com.example.socinetandroid.model.Post;

public interface IPostListener {
    void onActionClick(RecyclerView.ViewHolder holder, Post post);
    void onReactClick(RecyclerView.ViewHolder holder, Post post);
    void onReactLongClick(RecyclerView.ViewHolder holder, Post post);
    void onCommentClick(RecyclerView.ViewHolder holder, Post post);
    void onShareClick(RecyclerView.ViewHolder holder, Post post);
}
