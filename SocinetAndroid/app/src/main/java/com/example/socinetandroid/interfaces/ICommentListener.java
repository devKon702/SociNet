package com.example.socinetandroid.interfaces;

import com.example.socinetandroid.adapter.CommentAdapter;
import com.example.socinetandroid.model.Comment;

public interface ICommentListener {
    void onLongClick(CommentAdapter.CommentViewHolder holder, Comment comment);
    void onEdit(Comment comment);
    void onDelete(Comment comment);
}
