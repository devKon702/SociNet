package com.example.socinetandroid.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socinetandroid.R;
import com.example.socinetandroid.interfaces.IUserListener;
import com.example.socinetandroid.model.User;
import com.example.socinetandroid.utils.FileSupporter;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    public static final int VERTICAL_ITEM = 1;
    public static final int HORIZONTAL_ITEM = 2;
    private List<User> mUserList;
    private List<User> mUserListFull;
    private int itemType;
    private IUserListener listener;


    public UserAdapter(List<User> userList, int itemType, IUserListener listener) {
        this.mUserList = userList;
        if(itemType != VERTICAL_ITEM && itemType != HORIZONTAL_ITEM) {
            throw new RuntimeException("Invalid UserAdapter viewType");
        }
        this.itemType = itemType;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(this.itemType == VERTICAL_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friend_item_vertical, null);
            return new VerticalViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friend_item_horizontal, null);
            return new HorizontalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User user = mUserList.get(position);
        if(user == null) return;
        if(holder instanceof VerticalViewHolder){
            ((VerticalViewHolder) holder).bind(user);
        } else if(holder instanceof HorizontalViewHolder){
            ((HorizontalViewHolder) holder).bind(user);
        }
    }

    @Override
    public int getItemCount() {
        if(mUserList == null) return 0;
        return mUserList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<User> list){
        this.mUserList = list;
        notifyDataSetChanged();
    }

    public List<User> getCurrentList(){
        return this.mUserList;
    }
    public class VerticalViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout layoutFriend;
        private ImageView ivAvatar;
        private TextView tvName;

        public VerticalViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutFriend = itemView.findViewById(R.id.layoutFriend);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
        }

        public void bind(User user){
            FileSupporter.loadImage(ivAvatar, user.getAvatarUrl());
            tvName.setText(user.getName());
            layoutFriend.setOnClickListener(v -> listener.onItemClick(user));
        }
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout layoutFriend;
        private ImageView ivAvatar;
        private TextView tvName;
        public HorizontalViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutFriend = itemView.findViewById(R.id.layoutFriend);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
        }

        public void bind(User user){
            FileSupporter.loadImage(ivAvatar, user.getAvatarUrl());
            tvName.setText(user.getName());
            layoutFriend.setOnClickListener(v -> listener.onItemClick(user));
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchValue = constraint.toString();
                if(searchValue.isEmpty()){
                    mUserList = mUserListFull;
                } else {
                    List<User> list = new ArrayList<>();
                    mUserListFull.forEach(user -> {
                        if(user.getName().toLowerCase().contains(searchValue.toLowerCase())){
                            list.add(user);
                        }
                    });
                    mUserList = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mUserList;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mUserList = (List<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
