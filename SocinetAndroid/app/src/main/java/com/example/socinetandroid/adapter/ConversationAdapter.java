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
import com.example.socinetandroid.interfaces.IConversationListener;
import com.example.socinetandroid.model.Conversation;
import com.example.socinetandroid.utils.DateFormatter;
import com.example.socinetandroid.utils.FileSupporter;
import com.example.socinetandroid.viewmodel.AppViewModel;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int SELF_VIEW_TYPE = 1;
    public static final int OTHER_VIEW_TYPE = 2;
    private List<Conversation> mConversationList;
    private IConversationListener listener;
    private AppViewModel appViewModel;

    public ConversationAdapter(Context context, List<Conversation> mConversationList, IConversationListener listener) {
        this.mConversationList = mConversationList;
        this.listener = listener;
        this.appViewModel = ((MyApplication) context.getApplicationContext()).getAppViewModel();
    }

    @Override
    public int getItemViewType(int position) {
        Conversation conversation = mConversationList.get(position);
        if(conversation.getSenderId() == appViewModel.getUser().getId()) return SELF_VIEW_TYPE;
        else return OTHER_VIEW_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SELF_VIEW_TYPE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_self_message_item, parent, false);
            return new SelfConversationViewHolder(view);
        } else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_other_message_item, parent, false);
            return new OtherConversationViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()){
            Conversation conversation = mConversationList.get(position);
            if(conversation == null) return;
            if(holder instanceof SelfConversationViewHolder){
                ((SelfConversationViewHolder) holder).bind(conversation, false);
            } else if(holder instanceof OtherConversationViewHolder){
                ((OtherConversationViewHolder) holder).bind(conversation, false);
            }
        } else{
            Conversation conversation = (Conversation) payloads.get(0);
            if(conversation == null) return;
            if(holder instanceof SelfConversationViewHolder){
                ((SelfConversationViewHolder) holder).bind(conversation, true);
            } else if(holder instanceof OtherConversationViewHolder){
                ((OtherConversationViewHolder) holder).bind(conversation, true);
            }
        }
    }

    @Override
    public int getItemCount() {
        if(mConversationList == null) return 0;
        return mConversationList.size();
    }

    public class SelfConversationViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivImage;
        private TextView tvContent, tvTime;
        private CardView cvImage;
        public SelfConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            cvImage = itemView.findViewById(R.id.cvImage);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        public void bind(Conversation conversation, boolean isKeepMedia){
            if(conversation.isActive()){
                if(conversation.getFileUrl() != null){
                    cvImage.setVisibility(View.VISIBLE);
                    if(!isKeepMedia){
                        FileSupporter.loadImage(ivImage, conversation.getFileUrl());
                    }
                }
                else cvImage.setVisibility(View.GONE);

                if(conversation.getContent().isEmpty()){
                    tvContent.setVisibility(View.GONE);
                    cvImage.setOnLongClickListener(v -> listener.onLongClick(conversation));
                    cvImage.setOnClickListener(v -> tvTime.setVisibility(tvTime.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
                } else{
                    tvContent.setText(conversation.getContent());
                    tvContent.setVisibility(View.VISIBLE);
                    tvContent.setOnLongClickListener(v -> listener.onLongClick(conversation));
                    tvContent.setOnClickListener(v -> tvTime.setVisibility(tvTime.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
                }
                tvTime.setText((conversation.getCreatedAt().equals(conversation.getUpdatedAt()) ? "" : "Đã chỉnh sửa ") + DateFormatter.getDetailTime(conversation.getUpdatedAt()));
            } else {
                cvImage.setVisibility(View.GONE);
                tvContent.setText("Tin nhắn đã thu hồi");
                tvTime.setText("Thu hồi vào " + DateFormatter.getDetailTime(conversation.getUpdatedAt()));
                tvContent.setOnClickListener(v -> tvTime.setVisibility(tvTime.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
            }
        }
    }

    public class OtherConversationViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivImage;
        private CardView cvImage;
        private TextView tvContent, tvTime;
        public OtherConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTime = itemView.findViewById(R.id.tvTime);
            cvImage = itemView.findViewById(R.id.cvImage);
        }

        public void bind(Conversation conversation, boolean isKeepMedia){
            if(conversation.isActive()){
                if(conversation.getFileUrl() != null){
                    cvImage.setVisibility(View.VISIBLE);
                    if(!isKeepMedia){
                        FileSupporter.loadImage(ivImage, conversation.getFileUrl());
                    }
                }
                else cvImage.setVisibility(View.GONE);

                if(conversation.getContent().isEmpty()){
                    tvContent.setVisibility(View.GONE);
                    ivImage.setOnClickListener(v -> tvTime.setVisibility(tvTime.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
                } else{
                    tvContent.setText(conversation.getContent());
                    tvContent.setVisibility(View.VISIBLE);
                    tvContent.setOnClickListener(v -> tvTime.setVisibility(tvTime.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
                }
                tvTime.setText((conversation.getCreatedAt().equals(conversation.getUpdatedAt()) ? "" : "Đã chỉnh sửa ") + DateFormatter.getDetailTime(conversation.getUpdatedAt()));
            } else {
                cvImage.setVisibility(View.GONE);
                tvContent.setText("Tin nhắn đã thu hồi");
                tvTime.setText("Thu hồi vào " + DateFormatter.getDetailTime(conversation.getUpdatedAt()));
                tvContent.setOnClickListener(v -> tvTime.setVisibility(tvTime.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
            }
        }
    }
    private int findIndexOf(Conversation conversation){
        int position = -1;
        for(int i=0; i<mConversationList.size(); i++){
            Conversation item = mConversationList.get(i);
            if(item.getId() == conversation.getId()){
                position = i;
                break;
            }
        }
        return position;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<Conversation> list){
        mConversationList = list;
        notifyDataSetChanged();
    }
    public void updateItem(Conversation conversation){
        int position = findIndexOf(conversation);
        if(position == -1) return;
        mConversationList.set(position, conversation);
        notifyItemChanged(position, conversation);
    }

    public void addItem(Conversation conversation){
        mConversationList.add(0, conversation);
        notifyItemInserted(0);
    }
    public void updateListWithItem(Conversation conversation){
        int position = findIndexOf(conversation);
        if(position == -1){
            mConversationList.add(0, conversation);
            notifyItemInserted(0);
        } else {
            mConversationList.set(position, conversation);
            notifyItemChanged(position);
        }
    }
}
