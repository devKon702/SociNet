package com.example.socinetandroid.adapter;

import android.annotation.SuppressLint;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socinetandroid.R;
import com.example.socinetandroid.interfaces.IChooseUserListener;
import com.example.socinetandroid.interfaces.IUserListener;
import com.example.socinetandroid.model.User;
import com.example.socinetandroid.utils.FileSupporter;

import java.util.List;

public class ChooseMemberAdapter extends RecyclerView.Adapter<ChooseMemberAdapter.ChooseMemberViewHolder> {
    private List<User> mUserList;
    private IChooseUserListener listener;

    public ChooseMemberAdapter(List<User> mUserList, IChooseUserListener listener) {
        this.mUserList = mUserList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChooseMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friend2choose_item, parent, false);
        return new ChooseMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseMemberViewHolder holder, int position) {
        User user = mUserList.get(position);
        if(user == null) return;
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        if(mUserList == null) return 0;
        return mUserList.size();
    }

    public class ChooseMemberViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout layoutItem;
        private ImageView ivAvatar, ivChecked, ivUnchecked;
        private TextView tvName;
        private boolean isChecked = false;
        public ChooseMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layoutItem);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            ivChecked = itemView.findViewById(R.id.ivChecked);
            ivUnchecked = itemView.findViewById(R.id.ivUnchecked);
            tvName = itemView.findViewById(R.id.tvName);
        }

        public void bind(User user){
            FileSupporter.loadImage(ivAvatar, user.getAvatarUrl());
            tvName.setText(user.getName());
            layoutItem.setOnClickListener(v -> {
                isChecked = !isChecked;
                ivChecked.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                ivUnchecked.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                listener.onClick(user, isChecked);
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<User> list){
        mUserList = list;
        notifyDataSetChanged();
    }
}
