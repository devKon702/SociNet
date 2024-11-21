package com.example.socinetandroid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.R;
import com.example.socinetandroid.interfaces.ICommentListener;
import com.example.socinetandroid.model.Comment;
import com.example.socinetandroid.utils.DateFormatter;
import com.example.socinetandroid.utils.FileSupporter;
import com.example.socinetandroid.viewmodel.AppViewModel;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> mCommentList;
    private ICommentListener iCommentListener;
    private Context context;
    private boolean isCanReply;
    private AppViewModel appViewModel;

    public CommentAdapter(Context context, List<Comment> commentList, boolean isCanReply, ICommentListener listener){
        this.mCommentList = commentList;
        this.iCommentListener = listener;
        this.context = context;
        this.isCanReply = isCanReply;
        this.appViewModel = ((MyApplication) context.getApplicationContext()).getAppViewModel();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if(isCanReply){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_child_comment_item, parent, false);
        }
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()){
            Comment comment = mCommentList.get(position);
            if(comment == null) return;
            holder.bind(comment, false);
        } else {
            Comment comment = (Comment) payloads.get(0);
            if(comment == null) return;
            holder.bind(comment, true);
        }
    }

    @Override
    public int getItemCount() {
        if(mCommentList == null) return 0;
        return mCommentList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<Comment> list){
        this.mCommentList = list;
        notifyDataSetChanged();
    }

    public void addItem(Comment comment){
        mCommentList.add(0, comment);
        notifyItemInserted(0);
    }

    public void replyItem(Comment comment, long parentId){
        for(int i=0; i<mCommentList.size(); i++){
            if(mCommentList.get(i).getId() == parentId){
                mCommentList.get(i).getChildComments().add(0, comment);
                notifyItemChanged(i,mCommentList.get(i));
            }
        }
    }

    public void updateItem(Comment comment){
        for(int i=0; i<mCommentList.size(); i++){
            Comment parent = mCommentList.get(i);
            if(parent.getId() == comment.getId()){
                mCommentList.set(i, comment);
                notifyItemChanged(i, comment);
                return;
            } else{
                for(int j=0; j<parent.getChildComments().size(); j++){
                    if(parent.getChildComments().get(j).getId() == comment.getId()){
                        parent.getChildComments().set(j, comment);
                        notifyItemChanged(i, parent);
                        return;
                    }
                }
            }
        }
    }

    public void removeItem(Comment comment){
        for(int i=0; i<mCommentList.size(); i++){
            Comment parent = mCommentList.get(i);
            if(parent.getId() == comment.getId()){
                mCommentList.remove(i);
                notifyItemRemoved(i);
                return;
            } else{
                for(int j=0; j<parent.getChildComments().size(); j++){
                    if(parent.getChildComments().get(j).getId() == comment.getId()){
                        parent.getChildComments().remove(j);
                        notifyItemChanged(i, parent);
                        return;
                    }
                }
            }
        }
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivUserAvatar;
        private TextView tvUserName, tvContent, tvTime;
        private LinearLayout decoratedLine;
        private RecyclerView rcvChildComment;
        private CommentAdapter childCommentAdapter;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            this.tvUserName = itemView.findViewById(R.id.tvUserName);
            this.tvContent = itemView.findViewById(R.id.tvContent);
            this.tvTime = itemView.findViewById(R.id.tvTime);
            this.decoratedLine = itemView.findViewById(R.id.decorateLine);
            rcvChildComment = itemView.findViewById(R.id.rcvChildComment);
            childCommentAdapter = new CommentAdapter(context, new ArrayList<>(), false, iCommentListener);
        }

        public void bind(Comment comment, boolean isKeepMedia){
            if(!isKeepMedia && comment.getUser().getAvatarUrl() != null && !comment.getUser().getAvatarUrl().isEmpty()){
                FileSupporter.loadImage(ivUserAvatar, comment.getUser().getAvatarUrl());
            }

            tvUserName.setText(comment.getUser().getName());
            tvContent.setText(comment.getContent());
            tvTime.setText((comment.getCreatedAt().equals(comment.getUpdatedAt()) ? "" :"Đã chỉnh sửa ") + DateFormatter.getTimeDifference(comment.getUpdatedAt()));
            // Ko có reply comment
            if(isCanReply){
                if (comment.getChildComments().isEmpty()) {
                    rcvChildComment.setVisibility(View.GONE);
                    decoratedLine.setVisibility(View.GONE);
                }
                // Có reply comment
                else {
                    childCommentAdapter.setList(comment.getChildComments());
                    rcvChildComment.setAdapter(childCommentAdapter);
                    rcvChildComment.setLayoutManager(new LinearLayoutManager(context));
                    rcvChildComment.setVisibility(View.VISIBLE);
                    decoratedLine.setVisibility(View.VISIBLE);
                }
            }

            if(appViewModel.getUser().getId() == comment.getUser().getId() || isCanReply){
                tvContent.setOnLongClickListener(v -> iCommentListener.onLongClick(comment, isCanReply));
            }
        }
    }
}
