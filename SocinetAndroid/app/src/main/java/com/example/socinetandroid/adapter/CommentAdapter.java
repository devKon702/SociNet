package com.example.socinetandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socinetandroid.R;
import com.example.socinetandroid.interfaces.ICommentListener;
import com.example.socinetandroid.model.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> mCommentList;
    private ICommentListener iCommentListener;

    public CommentAdapter(List<Comment> commentList, ICommentListener listener){
        this.mCommentList = commentList;
        this.iCommentListener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = mCommentList.get(position);
        if(comment == null) return;

        holder.tvUserName.setText(comment.getUser().getName());
        holder.tvContent.setText(comment.getContent());
        holder.tvTime.setText(comment.getUpdatedAt().toString());
        holder.tvContent.setOnLongClickListener(v -> {
            iCommentListener.onLongClick(holder, comment);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        if(mCommentList == null) return 0;
        return mCommentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivUserAvatar;
        private TextView tvUserName, tvContent, tvTime;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            this.tvUserName = itemView.findViewById(R.id.tvUserName);
            this.tvContent = itemView.findViewById(R.id.tvContent);
            this.tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
