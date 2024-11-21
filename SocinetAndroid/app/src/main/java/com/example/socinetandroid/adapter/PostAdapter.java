package com.example.socinetandroid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
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

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.R;
import com.example.socinetandroid.interfaces.IPostListener;
import com.example.socinetandroid.model.Post;
import com.example.socinetandroid.model.Reaction;
import com.example.socinetandroid.utils.DateFormatter;
import com.example.socinetandroid.utils.FileSupporter;
import com.example.socinetandroid.viewmodel.AppViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int POST_TYPE = 1;
    private static final int SHARE_POST_TYPE = 2;
    private final AppViewModel appViewModel;
    private Context context;
    private List<Post> mPostList;
    private IPostListener iPostListener;


    public PostAdapter(Context context, List<Post> mPostList, IPostListener iPostListener) {
        this.context = context;
        this.mPostList = mPostList;
        this.iPostListener = iPostListener;
        this.appViewModel = ((MyApplication) context.getApplicationContext()).getAppViewModel();
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
            ((PostViewHolder) holder).bind(context, post, iPostListener, false);
        } else if(holder instanceof SharePostViewHolder){
            ((SharePostViewHolder) holder).bind(context, post, iPostListener, false);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()){
            Post post = mPostList.get(position);
            if(post == null) return;
            if(holder instanceof PostViewHolder){
                ((PostViewHolder) holder).bind(context, post, iPostListener, false);
            } else if(holder instanceof SharePostViewHolder){
                ((SharePostViewHolder) holder).bind(context, post, iPostListener, false);
            }
        } else{
            Post post = (Post) payloads.get(0);
            if(post == null) return;
            if(holder instanceof PostViewHolder){
                ((PostViewHolder) holder).bind(context, post, iPostListener, true);
            } else if(holder instanceof SharePostViewHolder){
                ((SharePostViewHolder) holder).bind(context, post, iPostListener, true);
            }
        }


//        super.onBindViewHolder(holder, position, payloads);
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

    public void updateItemKeepMedia(Post post){
        int position = -1;
        for(int i=0; i<mPostList.size(); i++){
            if(post.getId() == mPostList.get(i).getId()){
                position = i;
                break;
            }
        }

        if(position != -1){
            mPostList.set(position, post);
            notifyItemChanged(position, post);
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

    public class PostViewHolder extends RecyclerView.ViewHolder{
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

        public void bind(Context context, Post post, IPostListener listener, boolean isKeepMedia){
            tvName.setText(post.getUser().getName());
            tvCaption.setText(post.getCaption());
            tvTime.setText((post.getCreatedAt().equals(post.getUpdatedAt()) ? "" : "Đã chỉnh sửa ") + DateFormatter.getTimeDifference(post.getUpdatedAt()));

            if(appViewModel.getUser().getId() == post.getUser().getId()){
                btnAction.setVisibility(View.VISIBLE);
            } else {
                btnAction.setVisibility(View.GONE);
            }

            if(!isKeepMedia && post.getUser().getAvatarUrl() != null && !post.getUser().getAvatarUrl().isEmpty()){
                FileSupporter.loadImage(ivAvatar, post.getUser().getAvatarUrl());
            }

            if(!isKeepMedia){
                loadMediaFile(context, post);
            }

            // Setup Reaction button UI
            showReactionButton(context, post.getSelfReaction());

            btnAction.setOnClickListener(v -> listener.onActionClick(this, post));
            btnReact.setOnClickListener(v -> listener.onReactClick(this, post));
            btnReact.setOnLongClickListener(v -> listener.onReactLongClick(this, post));
            btnComment.setOnClickListener(v -> listener.onCommentClick(this, post));
            btnShare.setOnClickListener(v -> listener.onShareClick(this, post));
        }

        public void loadMediaFile(Context context, Post post){
            if(FileSupporter.isImageUrl(post.getImageUrl())){
                FileSupporter.loadImage(ivImage, post.getImageUrl());
                ivImage.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.GONE);
            } else if(FileSupporter.isVideoUrl(post.getImageUrl())) {
                FileSupporter.loadVideo(context, playerView, post.getImageUrl());
                playerView.setVisibility(View.VISIBLE);
                ivImage.setVisibility(View.GONE);
            }
        }

        public void showReactionButton(Context context, String type){
            Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_like);
            String text = "Thích";
            int color = ContextCompat.getColor(context, R.color.darkGray);
            ColorStateList iconTint = ColorStateList.valueOf(color);
            PorterDuff.Mode iconTintMode = null;
            if(type != null){
                iconTint = null;
                iconTintMode = PorterDuff.Mode.MULTIPLY;

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
            }
            btnReact.setText(text);
            btnReact.setTextColor(color);
            btnReact.setIcon(icon);
            btnReact.setIconTintMode(iconTintMode);
            btnReact.setIconTint(iconTint);
        }
    }

    public class SharePostViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivAvatar, ivChildAvatar, ivImage;
        private TextView tvName, tvTime, tvCaption, tvChildName, tvChildTime, tvChildCaption;
        private PlayerView playerView;
        private MaterialButton btnReact, btnComment;
        private ImageButton btnAction;
        private String currentFilePath;
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

        public void bind(Context context, Post post, IPostListener listener, boolean isKeepMedia){
            Post sharedPost = post.getSharedPost();
            // Parent header
            if(!isKeepMedia && post.getUser().getAvatarUrl() != null && !post.getUser().getAvatarUrl().isEmpty()){
                FileSupporter.loadImage(ivAvatar, post.getUser().getAvatarUrl());
            }
            tvName.setText(post.getUser().getName());
            tvTime.setText(post.getCreatedAt().equals(post.getUpdatedAt()) ? "" : "Đã chỉnh sửa " + DateFormatter.getTimeDifference(post.getUpdatedAt()));
            if(appViewModel.getUser().getId() == post.getUser().getId()){
                btnAction.setVisibility(View.VISIBLE);
            } else {
                btnAction.setVisibility(View.GONE);
            }
            tvCaption.setText(post.getCaption());
            // Child
            if(!isKeepMedia && sharedPost.getUser().getAvatarUrl() != null && !sharedPost.getUser().getAvatarUrl().isEmpty()){
                FileSupporter.loadImage(ivChildAvatar, sharedPost.getUser().getAvatarUrl());
            }
            tvChildName.setText(sharedPost.getUser().getName());
            tvChildTime.setText(sharedPost.getCreatedAt().equals(sharedPost.getUpdatedAt()) ? "" : "Đã chỉnh sửa " + DateFormatter.getTimeDifference(sharedPost.getUpdatedAt()));
            tvChildCaption.setText(sharedPost.getCaption());


            if(!isKeepMedia) {
                loadMediaFile(context, sharedPost);
            }

            // Setup reaction button UI
            showReactionButton(context, post.getSelfReaction());

            btnAction.setOnClickListener(v -> listener.onActionClick(this, post));
            btnReact.setOnClickListener(v -> listener.onReactClick(this, post));
            btnReact.setOnLongClickListener(v -> listener.onReactLongClick(this, post));
            btnComment.setOnClickListener(v -> listener.onCommentClick(this, post));
        }

        public void loadMediaFile(Context context, Post post){
            if(FileSupporter.isImageUrl(post.getImageUrl())){
                FileSupporter.loadImage(ivImage, post.getImageUrl());
                ivImage.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.GONE);
            } else if(FileSupporter.isVideoUrl(post.getImageUrl())) {
                FileSupporter.loadVideo(context, playerView, post.getImageUrl());
                playerView.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.GONE);
            }
        }

        public void showReactionButton(Context context, String type){
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

                btnReact.setTextColor(color);
                btnReact.setIconTintMode(PorterDuff.Mode.MULTIPLY);
                btnReact.setIcon(icon);
                btnReact.setIconTint(null);
                btnReact.setText(text);
            }
        }
    }
}
