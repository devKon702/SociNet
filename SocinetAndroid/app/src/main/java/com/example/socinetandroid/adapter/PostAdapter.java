package com.example.socinetandroid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socinetandroid.R;
import com.example.socinetandroid.interfaces.IPostListener;
import com.example.socinetandroid.model.Post;
import com.example.socinetandroid.model.Reaction;
import com.example.socinetandroid.utils.DateFormatter;
import com.example.socinetandroid.utils.FileSupporter;
import com.example.socinetandroid.utils.GlobalData;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int POST_TYPE = 1;
    private static final int SHARE_POST_TYPE = 2;
    private Context context;
    private List<Post> mPostList;
    private IPostListener iPostListener;

    public PostAdapter(Context context, List<Post> mPostList, IPostListener iPostListener) {
        this.context = context;
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
            bindPostViewHolder((PostViewHolder) holder, post);
        } else if(holder instanceof SharePostViewHolder){
            bindSharePostViewHolder((SharePostViewHolder) holder, post);
        }
    }

    @Override
    public int getItemCount() {
        if(mPostList == null) return 0;
        return mPostList.size();
    }

    public void addItem(int position, Post post){
        mPostList.add(position, post);
        notifyItemInserted(position);
    }

    public void updateItem(Post post){
        int position = -1;
        for(int i=0; i<mPostList.size(); i++){
            if(post.getId() == mPostList.get(i).getId()){
                position = i;
                break;
            }
        }

        if(position != -1){
            mPostList.set(position, post);
            notifyItemChanged(position);
        }
    }

    public void deleteItem(Post post){
        int position = -1;
        for(int i=0; i<mPostList.size(); i++){
            if(post.getId() == mPostList.get(i).getId()){
                position = i;
                break;
            }
        }
        if(position != -1){
            mPostList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivAvatar, ivImage;
        private PlayerView playerView;
        private TextView tvName, tvTime, tvCaption;
        private MaterialButton btnReact, btnComment, btnShare;
        private ImageButton btnAction;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            ivImage = itemView.findViewById(R.id.ivImage);
            playerView = itemView.findViewById(R.id.playerView);
            btnAction = itemView.findViewById(R.id.btnAction);
            btnReact = itemView.findViewById(R.id.btnReact);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnShare = itemView.findViewById(R.id.btnShare);
        }
    }

    public static class SharePostViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivAvatar, ivChildAvatar, ivImage;
        private TextView tvName, tvTime, tvCaption, tvChildName, tvChildTime, tvChildCaption;
        private PlayerView playerView;
        private MaterialButton btnReact, btnComment;
        private ImageButton btnAction;
        public SharePostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            ivChildAvatar = itemView.findViewById(R.id.ivChildAvatar);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvChildName = itemView.findViewById(R.id.tvChildName);
            tvChildTime = itemView.findViewById(R.id.tvChildTime);
            tvChildCaption = itemView.findViewById(R.id.tvChildCaption);
            playerView = itemView.findViewById(R.id.playerView);
            btnReact = itemView.findViewById(R.id.btnReact);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }

    private void bindPostViewHolder(PostViewHolder holder, Post post){
        holder.tvName.setText(post.getUser().getName());
        holder.tvCaption.setText(post.getCaption());
        holder.tvTime.setText(post.getCreatedAt().equals(post.getUpdatedAt()) ? "" : "Đã chỉnh sửa " + DateFormatter.getTimeDifference(post.getUpdatedAt()));

        if(GlobalData.user.getId() == post.getUser().getId()){
            holder.btnAction.setVisibility(View.VISIBLE);
        } else {
            holder.btnAction.setVisibility(View.GONE);
        }

        if(post.getUser().getAvatarUrl() != null && !post.getUser().getAvatarUrl().isEmpty()){
            FileSupporter.loadImage(holder.ivAvatar, post.getUser().getAvatarUrl());
        }

        if(FileSupporter.isImageUrl(post.getImageUrl())){
            FileSupporter.loadImage(holder.ivImage, post.getImageUrl());
            holder.ivImage.setVisibility(View.VISIBLE);
        } else if(FileSupporter.isVideoUrl(post.getImageUrl())) {
            FileSupporter.loadVideo(context, holder.playerView, post.getImageUrl());
            holder.playerView.setVisibility(View.VISIBLE);
        }



        // Setup Reaction button UI
        showReactionButton((PostViewHolder) holder, post.getSelfReaction());

        holder.btnAction.setOnClickListener(v -> iPostListener.onActionClick(holder, post));
        holder.btnReact.setOnClickListener(v -> iPostListener.onReactClick(holder, post));
        holder.btnComment.setOnClickListener(v ->iPostListener.onCommentClick(holder, post));
        holder.btnShare.setOnClickListener(v ->iPostListener.onShareClick(holder, post));
    }

    private void bindSharePostViewHolder(SharePostViewHolder holder, Post post){
        Post sharedPost = post.getSharedPost();
        // Parent header
        if(post.getUser().getAvatarUrl() != null && !post.getUser().getAvatarUrl().isEmpty()){
            FileSupporter.loadImage(holder.ivAvatar, post.getUser().getAvatarUrl());
        }
        holder.tvName.setText(post.getUser().getName());
        holder.tvTime.setText(post.getCreatedAt().equals(post.getUpdatedAt()) ? "" : "Đã chỉnh sửa " + DateFormatter.getTimeDifference(post.getUpdatedAt()));
        if(GlobalData.user.getId() == post.getUser().getId()){
            holder.btnAction.setVisibility(View.VISIBLE);
        } else {
            holder.btnAction.setVisibility(View.GONE);
        }
        holder.tvCaption.setText(post.getCaption());
        // Child
        if(sharedPost.getUser().getAvatarUrl() != null && !sharedPost.getUser().getAvatarUrl().isEmpty()){
            FileSupporter.loadImage(holder.ivChildAvatar, sharedPost.getUser().getAvatarUrl());
        }
        holder.tvChildName.setText(sharedPost.getUser().getName());
        holder.tvChildTime.setText(sharedPost.getCreatedAt().equals(sharedPost.getUpdatedAt()) ? "" : "Đã chỉnh sửa " + DateFormatter.getTimeDifference(sharedPost.getUpdatedAt()));
        holder.tvChildCaption.setText(sharedPost.getCaption());

        if(FileSupporter.isImageUrl(sharedPost.getImageUrl())){
            FileSupporter.loadImage(holder.ivImage, sharedPost.getImageUrl());
            holder.ivImage.setVisibility(View.VISIBLE);
        } else if(FileSupporter.isVideoUrl(sharedPost.getImageUrl())) {
            FileSupporter.loadVideo(context, holder.playerView, sharedPost.getImageUrl());
            holder.playerView.setVisibility(View.VISIBLE);
        }

        // Setup reaction button UI
        showReactionButton((SharePostViewHolder) holder, post.getSelfReaction());

        holder.btnAction.setOnClickListener(v -> iPostListener.onActionClick(holder, post));
        holder.btnReact.setOnClickListener(v -> iPostListener.onReactClick(holder, post));
        holder.btnReact.setOnLongClickListener(v -> {
            iPostListener.onReactLongClick(holder, post);
            return true;
        });
        holder.btnComment.setOnClickListener(v -> iPostListener.onCommentClick(holder, post));
    }

    private void showReactionButton(RecyclerView.ViewHolder holder, String type){
        if(type != null){
            int color = ContextCompat.getColor(context, R.color.darkGray);
            Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_like);
            String text = "Thích";
            if(type.equals(Reaction.LIKE_TYPE)){
                color = ContextCompat.getColor(context, R.color.likeColor);
                icon = ContextCompat.getDrawable(context, R.drawable.ic_like_react);
                text = "Thích";
            }
            else if(type.equals(Reaction.LOVE_TYPE)){
                color = ContextCompat.getColor(context, R.color.loveColor);
                icon = ContextCompat.getDrawable(context, R.drawable.ic_love_react);
                text = "Yêu thích";
            }
            else if(type.equals(Reaction.HAHA_TYPE)){
                color = ContextCompat.getColor(context, R.color.hahaColor);
                icon = ContextCompat.getDrawable(context, R.drawable.ic_haha_react);
                text = "Haha";
            }
            else if(type.equals(Reaction.SAD_TYPE)){
                color = ContextCompat.getColor(context, R.color.sadColor);
                icon = ContextCompat.getDrawable(context, R.drawable.ic_sad_react);
                text = "Buồn";
            }

            if(holder instanceof PostViewHolder){
                ((PostViewHolder) holder).btnReact.setTextColor(color);
                ((PostViewHolder) holder).btnReact.setIconTintMode(PorterDuff.Mode.MULTIPLY);
                ((PostViewHolder) holder).btnReact.setIcon(icon);
                ((PostViewHolder) holder).btnReact.setText(text);
            }

            if(holder instanceof SharePostViewHolder){
                ((SharePostViewHolder) holder).btnReact.setTextColor(color);
                ((SharePostViewHolder) holder).btnReact.setIconTintMode(PorterDuff.Mode.MULTIPLY);
                ((SharePostViewHolder) holder).btnReact.setIcon(icon);
                ((SharePostViewHolder) holder).btnReact.setText(text);
            }
        }
    }
}
