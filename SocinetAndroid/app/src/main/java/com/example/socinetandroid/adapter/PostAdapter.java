package com.example.socinetandroid.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socinetandroid.R;
import com.example.socinetandroid.interfaces.IPostListener;
import com.example.socinetandroid.model.Post;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int POST_TYPE = 1;
    private static final int SHARE_POST_TYPE = 2;
    private List<Post> mPostList;
    private IPostListener iPostListener;

    public PostAdapter(List<Post> mPostList, IPostListener iPostListener) {
        this.mPostList = mPostList;
        this.iPostListener = iPostListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<Post> postList){
        this.mPostList = postList;
        notifyDataSetChanged();
    }

    public void setItem(int position, Post post){
        if(this.mPostList.get(position) != null) {
            this.mPostList.set(position, post);
            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Post post = mPostList.get(position);
        if(post.getSharedPost() != null){
            return SHARE_POST_TYPE;
        } else return POST_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SHARE_POST_TYPE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_share_post_item, parent, false);
            return new SharePostViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_item, parent, false);
            return new PostViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = mPostList.get(position);
        if(post == null) return;
        if(holder instanceof PostViewHolder){
            ((PostViewHolder) holder).tvName.setText(post.getUser().getName());
            ((PostViewHolder) holder).tvCaption.setText(post.getCaption());
            ((PostViewHolder) holder).tvTime.setText(post.getUpdatedAt().toString());
            ((PostViewHolder) holder).btnReact.setOnClickListener(v -> {
                iPostListener.onReactClick();
            });
            ((PostViewHolder) holder).btnComment.setOnClickListener(v -> {
                iPostListener.onCommentClick();
            });
            ((PostViewHolder) holder).btnShare.setOnClickListener(v -> {
                iPostListener.onShareClick();
            });
        } else if(holder instanceof SharePostViewHolder){
            ((SharePostViewHolder) holder).tvName.setText(post.getUser().getName());
            ((SharePostViewHolder) holder).tvCaption.setText(post.getCaption());
            ((SharePostViewHolder) holder).tvTime.setText(post.getUpdatedAt().toString());
            ((SharePostViewHolder) holder).tvChildName.setText(post.getSharedPost().getUser().getName());
            ((SharePostViewHolder) holder).tvChildCaption.setText(post.getSharedPost().getCaption());
            ((SharePostViewHolder) holder).tvChildTime.setText(post.getSharedPost().getUpdatedAt().toString());
            ((SharePostViewHolder) holder).btnReact.setOnClickListener(v -> iPostListener.onReactClick());
            ((SharePostViewHolder) holder).btnComment.setOnClickListener(v -> iPostListener.onCommentClick());
        }
    }

    @Override
    public int getItemCount() {
        if(mPostList == null) return 0;
        return mPostList.size();
    }


    public static class PostViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivAvatar;
        private TextView tvName, tvTime, tvCaption;
        private MaterialButton btnReact, btnComment, btnShare;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            btnReact = itemView.findViewById(R.id.btnReact);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnShare = itemView.findViewById(R.id.btnShare);
        }
    }

    public static class SharePostViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivAvatar, ivChildAvatar;
        private TextView tvName, tvTime, tvCaption, tvChildName, tvChildTime, tvChildCaption;
        private MaterialButton btnReact, btnComment;
        public SharePostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            ivChildAvatar = itemView.findViewById(R.id.ivChildAvatar);
            tvChildName = itemView.findViewById(R.id.tvChildName);
            tvChildTime = itemView.findViewById(R.id.tvChildTime);
            tvChildCaption = itemView.findViewById(R.id.tvChildCaption);

            btnReact = itemView.findViewById(R.id.btnReact);
            btnComment = itemView.findViewById(R.id.btnComment);
        }
    }
}
