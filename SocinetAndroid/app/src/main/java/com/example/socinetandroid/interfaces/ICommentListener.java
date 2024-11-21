package com.example.socinetandroid.interfaces;

import com.example.socinetandroid.adapter.CommentAdapter;
import com.example.socinetandroid.model.Comment;

public interface ICommentListener {
    boolean onLongClick(Comment comment, boolean isCanReply);
}
