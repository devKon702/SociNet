package com.example.socinetandroid.adapter;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socinetandroid.R;
import com.example.socinetandroid.enums.RealtimeStatus;
import com.example.socinetandroid.interfaces.IRealtimeChatListener;
import com.example.socinetandroid.model.RealtimeChat;
import com.example.socinetandroid.utils.FileSupporter;

import java.util.List;

public class RealtimeChatAdapter extends RecyclerView.Adapter<RealtimeChatAdapter.RealtimeViewHolder> {
    private List<RealtimeChat> mRealtimeList;
    private IRealtimeChatListener listener;

    public RealtimeChatAdapter(List<RealtimeChat> list, IRealtimeChatListener listener){
        this.mRealtimeList = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RealtimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_item, parent, false);
        return new RealtimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RealtimeViewHolder holder, int position) {
        holder.bind(mRealtimeList.get(position), false);
    }

    @Override
    public void onBindViewHolder(@NonNull RealtimeViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()){
            RealtimeChat realtimeChat = mRealtimeList.get(position);
            if(realtimeChat == null) return;
            holder.bind(realtimeChat, false);
        }
        else{
            RealtimeChat realtimeChat = (RealtimeChat) payloads.get(0);
            if(realtimeChat == null) return;
            holder.bind(realtimeChat, true);
        }
    }

    @Override
    public int getItemCount() {
        if(mRealtimeList == null) return 0;
        return mRealtimeList.size();
    }

    public class RealtimeViewHolder extends RecyclerView.ViewHolder{
        private ConstraintLayout layoutChatItem;
        private ImageView ivAvatar;
        private TextView tvName, tvNewMessage;
        private LinearLayout circleStatus;
        public RealtimeViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutChatItem = itemView.findViewById(R.id.layoutChatItem);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvNewMessage = itemView.findViewById(R.id.tvNewMessage);
            circleStatus = itemView.findViewById(R.id.circleStatus);
        }

        public void bind(RealtimeChat realtimeChat, boolean isKeepMedia){
            if(!isKeepMedia){
                FileSupporter.loadImage(ivAvatar, realtimeChat.getAvatarUrl());
            }
            tvName.setText(realtimeChat.getName());
            tvNewMessage.setText(realtimeChat.isHasNewMessage() ? "Có tin nhắn mới" : "");
            layoutChatItem.setOnClickListener(v-> listener.onItemClick(realtimeChat));
            if(realtimeChat.getStatus().equals(RealtimeStatus.ONLINE)){
                circleStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.green)));
                circleStatus.setVisibility(View.VISIBLE);
            } else if(realtimeChat.getStatus().equals(RealtimeStatus.OFFLINE)){
                circleStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.red)));
                circleStatus.setVisibility(View.VISIBLE);
            } else {
                circleStatus.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<RealtimeChat> list){
        this.mRealtimeList = list;
        notifyDataSetChanged();
    }
    public void updateList(List<RealtimeChat> list){
        if(list.size() != mRealtimeList.size()){
            setList(list);
        } else {
            for(int i=0; i<list.size(); i++){
                if(!list.get(i).equals(mRealtimeList.get(i))){
                    mRealtimeList.set(i, list.get(i));
                    notifyItemChanged(i);
                }
            }
        }
    }

    public void updateItem(RealtimeChat realtimeChat, boolean isReset){
        int position = findIndexOf(realtimeChat);

        if(position != -1){
            mRealtimeList.set(position, realtimeChat);
            if(isReset) notifyItemChanged(position);
            else notifyItemChanged(position, realtimeChat);
        }
    }

    public void addItem(RealtimeChat realtimeChat){
        mRealtimeList.add(0, realtimeChat);
        notifyItemInserted(0);
    }

    public void removeItem(RealtimeChat realtimeChat){
        int position = findIndexOf(realtimeChat);
        if(position != -1){
            mRealtimeList.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void updateOnlineStatus(long id, RealtimeStatus status){
        int position = findIndexById(id);
        if(position != -1){
            mRealtimeList.get(position).setStatus(status);
            notifyItemChanged(position);
        }
    }

    private int findIndexOf(RealtimeChat realtimeChat){
        for(int i=0; i<mRealtimeList.size(); i++){
            RealtimeChat item = mRealtimeList.get(i);
            if(realtimeChat.getId() == item.getId() && realtimeChat.getChatType().equals(item.getChatType())){
                return i;
            }
        }
        return -1;
    }
    private int findIndexById(long id){
        for(int i=0; i<mRealtimeList.size(); i++){
            RealtimeChat item = mRealtimeList.get(i);
            if(id == item.getId()){
                return i;
            }
        }
        return -1;
    }
}
