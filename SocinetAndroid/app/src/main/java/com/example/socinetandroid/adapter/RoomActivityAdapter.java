package com.example.socinetandroid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.R;
import com.example.socinetandroid.interfaces.IRoomActivityListener;
import com.example.socinetandroid.model.RoomsActivity;
import com.example.socinetandroid.model.User;
import com.example.socinetandroid.utils.DateFormatter;
import com.example.socinetandroid.utils.FileSupporter;
import com.example.socinetandroid.viewmodel.AppViewModel;

import java.util.List;

public class RoomActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int SELF_MESSAGE = 1;
    public static final int OTHER_MESSAGE = 2;
    public static final int NOTIFY = 3;
    private List<RoomsActivity> mListRoomsActivities;
    private IRoomActivityListener listener;
    private AppViewModel appViewModel;

    public RoomActivityAdapter(Context context, List<RoomsActivity> mListRoomsActivities, IRoomActivityListener listener) {
        this.mListRoomsActivities = mListRoomsActivities;
        this.listener = listener;
        appViewModel = ((MyApplication) context.getApplicationContext()).getAppViewModel();
    }

    @Override
    public int getItemViewType(int position) {
        RoomsActivity activity = mListRoomsActivities.get(position);
        if(activity == null) return NOTIFY;
        if(activity.getType().equals(RoomsActivity.CHAT)){
            if(activity.getSender().getId() == appViewModel.getUser().getId()) return SELF_MESSAGE;
            else return OTHER_MESSAGE;
        } else return NOTIFY;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch(viewType){
            case SELF_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_room_self_message, parent, false);
                return new SelfMessageViewHolder(view);
            case OTHER_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_room_other_message, parent, false);
                return new OtherMessageViewHolder(view);
            case NOTIFY:
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_room_notification, parent, false);
                return new NotifyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()){
            RoomsActivity activity = mListRoomsActivities.get(position);
            if(activity == null) return;
            if(holder instanceof SelfMessageViewHolder){
                ((SelfMessageViewHolder) holder).bind(activity);
            }
            else if(holder instanceof OtherMessageViewHolder){
                boolean checkContinuous = false;
                if(position < mListRoomsActivities.size() - 1){
                    RoomsActivity aboveActivity = mListRoomsActivities.get(position+1);
                    if(aboveActivity != null){
                        checkContinuous = aboveActivity.getSender().getId() == activity.getSender().getId() && aboveActivity.getType().equals(RoomsActivity.CHAT);
                    }
                }
                ((OtherMessageViewHolder) holder).bind(activity, checkContinuous);
            }
            else{
                ((NotifyViewHolder) holder).bind(activity);
            }
        } else {
            RoomsActivity activity = (RoomsActivity) payloads.get(0);
            if(activity == null) return;
            if(holder instanceof SelfMessageViewHolder){
                ((SelfMessageViewHolder) holder).updateContent(activity);
            }
            else if(holder instanceof OtherMessageViewHolder){
                ((OtherMessageViewHolder) holder).updateContent(activity);
            }
            else{
                ((NotifyViewHolder) holder).bind(activity);
            }
        }
    }

    @Override
    public int getItemCount() {
        if(mListRoomsActivities == null) return 0;
        return mListRoomsActivities.size();
    }

    public class SelfMessageViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivImage;
        private TextView tvContent, tvTime;
        private CardView cvImage;
        public SelfMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTime = itemView.findViewById(R.id.tvTime);
            cvImage = itemView.findViewById(R.id.cvImage);
        }
        public void bind(RoomsActivity activity){
            if(activity.isActive()){
                if(activity.getFileUrl() == null) cvImage.setVisibility(View.GONE);
                else {
                    FileSupporter.loadImage(ivImage, activity.getFileUrl());
                    cvImage.setVisibility(View.VISIBLE);
                }
                if(activity.getContent() != null){
                    tvContent.setText(activity.getContent());
                    tvContent.setOnLongClickListener(v -> listener.onLongClick(activity));
                    tvContent.setOnClickListener(v -> tvTime.setVisibility(tvTime.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
                } else{
                    tvContent.setVisibility(View.GONE);
                    ivImage.setOnLongClickListener(v -> listener.onLongClick(activity));
                    ivImage.setOnClickListener(v -> tvTime.setVisibility(tvTime.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
                }
                tvTime.setText((activity.getCreatedAt().equals(activity.getUpdatedAt()) ? "" : "Đã chỉnh sửa ") + DateFormatter.getDetailTime(activity.getUpdatedAt()));
            } else {
                cvImage.setVisibility(View.GONE);
                tvContent.setText("Tin nhắn đã thu hồi");
                tvTime.setText("Đã thu hồi lúc " + DateFormatter.getDetailTime(activity.getUpdatedAt()));
                tvContent.setOnLongClickListener(null);
                tvContent.setOnClickListener(v -> tvTime.setVisibility(tvTime.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
            }
        }
        public void updateContent(RoomsActivity activity){
            if(activity.isActive()){
                if(activity.getContent() != null || !activity.getContent().isEmpty()){
                    tvContent.setVisibility(View.VISIBLE);
                    tvContent.setText(activity.getContent());
                    tvContent.setOnLongClickListener(v -> listener.onLongClick(activity));
                    tvContent.setOnClickListener(v -> tvTime.setVisibility(tvTime.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
                } else{
                    tvContent.setVisibility(View.GONE);
                    ivImage.setOnLongClickListener(v -> listener.onLongClick(activity));
                    ivImage.setOnClickListener(v -> tvTime.setVisibility(tvTime.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
                }
                tvTime.setText((activity.getCreatedAt().equals(activity.getUpdatedAt()) ? "" : "Đã chỉnh sửa ") + DateFormatter.getDetailTime(activity.getUpdatedAt()));
            } else {
                cvImage.setVisibility(View.GONE);
                tvContent.setText("Tin nhắn đã thu hồi");
                tvTime.setText("Đã thu hồi lúc " + DateFormatter.getDetailTime(activity.getUpdatedAt()));
                tvContent.setOnLongClickListener(null);
                tvContent.setOnClickListener(v -> tvTime.setVisibility(tvTime.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
            }
        }
    }

    public class OtherMessageViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivImage, ivAvatar;
        private TextView tvName, tvContent, tvTime;
        private CardView cvAvatar, cvImage;
        public OtherMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTime = itemView.findViewById(R.id.tvTime);
            cvImage = itemView.findViewById(R.id.cvImage);
            cvAvatar = itemView.findViewById(R.id.cvAvatar);
        }
        public void bind(RoomsActivity activity, boolean isContinuous){
            if(isContinuous){
                cvAvatar.setVisibility(View.INVISIBLE);
                tvName.setVisibility(View.GONE);
            }
            else {
                FileSupporter.loadImage(ivAvatar, activity.getSender().getAvatarUrl());
                tvName.setText(activity.getSender().getName());
            }
            if(activity.isActive()){
                if(activity.getFileUrl() == null) cvImage.setVisibility(View.GONE);
                else {
                    FileSupporter.loadImage(ivImage, activity.getFileUrl());
                    cvImage.setVisibility(View.VISIBLE);
                }
                if(activity.getContent() != null){
                    tvContent.setText(activity.getContent());
                    tvContent.setOnClickListener(v -> tvTime.setVisibility(tvTime.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
                } else{
                    tvContent.setVisibility(View.GONE);
                    ivImage.setOnClickListener(v -> tvTime.setVisibility(tvTime.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
                }
                tvTime.setText((activity.getCreatedAt().equals(activity.getUpdatedAt()) ? "" : "Đã chỉnh sửa ") + DateFormatter.getDetailTime(activity.getUpdatedAt()));
            } else {
                ivImage.setVisibility(View.GONE);
                tvContent.setText("Tin nhắn đã thu hồi");
                tvTime.setText("Đã thu hồi lúc " + DateFormatter.getDetailTime(activity.getUpdatedAt()));
                tvContent.setOnClickListener(v -> tvTime.setVisibility(tvTime.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
            }
        }
        public void updateContent(RoomsActivity activity){
            if(activity.isActive()){
                if(activity.getContent() != null || !activity.getContent().isEmpty()){
                    tvContent.setText(activity.getContent());
                    tvContent.setVisibility(View.VISIBLE);
                } else{
                    tvContent.setVisibility(View.GONE);
                }
                tvTime.setText((activity.getCreatedAt().equals(activity.getUpdatedAt()) ? "" : "Đã chỉnh sửa ") + DateFormatter.getDetailTime(activity.getUpdatedAt()));
            } else {
                ivImage.setVisibility(View.GONE);
                tvContent.setText("Tin nhắn đã thu hồi");
                tvTime.setText("Đã thu hồi lúc " + DateFormatter.getDetailTime(activity.getUpdatedAt()));
                tvContent.setOnClickListener(v -> tvTime.setVisibility(tvTime.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
            }
        }
    }

    public class NotifyViewHolder extends RecyclerView.ViewHolder{
        private TextView tvContent;
        public NotifyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(com.example.socinetandroid.R.id.tvContent);
        }
        public void bind(RoomsActivity activity){
            String content = "";
            User currentUser = appViewModel.getUser();
            if(activity.getType().equals(RoomsActivity.JOIN)){
                content += activity.getSender().getName() + " đã tham gia nhóm";
            }
            if(activity.getType().equals(RoomsActivity.INVITE)){
                content += (currentUser.getId() == activity.getSender().getId()) ? "Bạn" : activity.getSender().getName();
                content += " đã thêm " + activity.getReceiver().getName() + " vào nhóm";
            }
            if(activity.getType().equals(RoomsActivity.KICK)){
                content += (currentUser.getId() == activity.getSender().getId()) ? "Bạn" : activity.getSender().getName();
                content += " đã xóa " + activity.getReceiver().getName() + " khỏi nhóm";
            }
            if(activity.getType().equals(RoomsActivity.QUIT)){
                content += (currentUser.getId() == activity.getSender().getId()) ? "Bạn" : activity.getSender().getName();
                content += " đã rời nhóm";
            }
            if(activity.getType().equals(RoomsActivity.NOTIFY)){
                content += (currentUser.getId() == activity.getSender().getId()) ? "Bạn" : activity.getSender().getName();
                content += " " + activity.getContent();
            }
            tvContent.setText(content);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<RoomsActivity> list){
        this.mListRoomsActivities = list;
        notifyDataSetChanged();
    }
    public void updateListWithItem(RoomsActivity activity){
        int position = findPositionOf(activity);
        if(position == -1){
            mListRoomsActivities.add(0, activity);
            notifyItemInserted(0);
        } else {
            mListRoomsActivities.set(position, activity);
            notifyItemChanged(position);
        }
    }

    public int findPositionOf(RoomsActivity activity){
        if(mListRoomsActivities == null) return -1;
        for(int i=0; i< mListRoomsActivities.size(); i++){
            if(mListRoomsActivities.get(i).getId() == activity.getId())
                return i;
        }
        return -1;
    }

    public void addItem(RoomsActivity activity){
        mListRoomsActivities.add(0, activity);
        notifyItemInserted(0);
    }

    public void updateItem(RoomsActivity activity){
        int position = findPositionOf(activity);
        if(position != -1){
            mListRoomsActivities.set(position, activity);
            notifyItemChanged(position, activity);
        }
    }
}
