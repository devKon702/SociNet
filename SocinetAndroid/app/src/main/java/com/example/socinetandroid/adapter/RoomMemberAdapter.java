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
import com.example.socinetandroid.model.RoomMember;
import com.example.socinetandroid.utils.FileSupporter;

import java.util.List;

public class RoomMemberAdapter extends RecyclerView.Adapter<RoomMemberAdapter.RoomMemberViewHolder> {
    private List<RoomMember> mMemberList;

    public RoomMemberAdapter(List<RoomMember> mMemberList) {
        this.mMemberList = mMemberList;
    }

    @NonNull
    @Override
    public RoomMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_room_member_item, parent, false);
        return new RoomMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomMemberViewHolder holder, int position) {
        RoomMember member = mMemberList.get(position);
        if(member == null) return;
        holder.bind(member);
    }

    @Override
    public int getItemCount() {
        if(mMemberList == null) return 0;
        return mMemberList.size();
    }

    public class RoomMemberViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivAvatar;
        private TextView tvName, tvRole;
        public RoomMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvRole = itemView.findViewById(R.id.tvRole);
        }

        public void bind(RoomMember member){
            FileSupporter.loadImage(ivAvatar, member.getUser().getAvatarUrl());
            tvName.setText(member.getUser().getName());
            tvRole.setText(member.isAdmin() ? "Quản trị viên" : "Thành viên");
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<RoomMember> list){
        this.mMemberList = list;
        notifyDataSetChanged();
    }
    public int findPositionOf(RoomMember member){
        for(int i=0; i< mMemberList.size(); i++){
            if(member.getId() == mMemberList.get(i).getId())
                return i;
        }
        return -1;
    }

    public void addItem(RoomMember member){
        this.mMemberList.add(member);
        notifyItemInserted(mMemberList.size()-1);
    }

    public void removeItem(RoomMember member){
        int position = findPositionOf(member);
        if(position != -1){
            mMemberList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
