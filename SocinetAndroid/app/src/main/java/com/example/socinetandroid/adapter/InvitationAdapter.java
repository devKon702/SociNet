package com.example.socinetandroid.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socinetandroid.R;
import com.example.socinetandroid.interfaces.IInvitationListener;
import com.example.socinetandroid.model.Invitation;
import com.example.socinetandroid.utils.DateFormatter;
import com.example.socinetandroid.utils.FileSupporter;

import java.util.List;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.InvitationViewHolder> {
    private List<Invitation> mInvitationList;
    private IInvitationListener listener;

    public InvitationAdapter(List<Invitation> list, IInvitationListener listener){
        this.mInvitationList = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InvitationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_invitation_item, parent, false);
        return new InvitationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitationViewHolder holder, int position) {
        return;
    }

    @Override
    public void onBindViewHolder(@NonNull InvitationViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()){
            Invitation invitation = mInvitationList.get(position);
            if(invitation == null) return;
            holder.bind(invitation, false);
        } else {
            Invitation invitation = (Invitation) payloads.get(0);
            holder.bind(invitation, true);
        }
    }

    @Override
    public int getItemCount() {
        if(mInvitationList == null) return 0;
        return mInvitationList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<Invitation> list){
        this.mInvitationList = list;
        notifyDataSetChanged();
    }

    public void updateList(List<Invitation> list){
        if(list.size() != mInvitationList.size()) setList(list);
        else{
            for(int i=0; i< mInvitationList.size(); i++){
                if(mInvitationList.get(i).getId() != list.get(i).getId()){
                    mInvitationList.set(i, list.get(i));
                    notifyItemChanged(i, list.get(i));
                }
            }
        }

    }

    public void updateItem(Invitation invitation){
        int position = -1;
        for(int i=0; i<mInvitationList.size(); i++){
            if(invitation.getId() == mInvitationList.get(i).getId()){
                position = i;
                break;
            }
        }
        if(position != -1){
            mInvitationList.set(position, invitation);
            notifyItemChanged(position, invitation);
        }
    }

    public void addItem(Invitation invitation){
        mInvitationList.add(0, invitation);
        notifyItemInserted(0);
    }

    public void removeItem(Invitation invitation){
        int position = -1;
        for(int i=0; i<mInvitationList.size(); i++){
            if(invitation.getId() == mInvitationList.get(i).getId()){
                position = i;
                break;
            }
        }
        if(position != -1){
            mInvitationList.remove(position);
            notifyItemRemoved(position);
        }
    }


    public class InvitationViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivAvatar;
        private TextView tvName, tvTime, tvStatus;
        private Button btnAccept, btnReject;
        private LinearLayout layoutAction;

        public InvitationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            layoutAction = itemView.findViewById(R.id.layoutAction);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        public void bind(Invitation invitation, boolean isKeepMedia){
            if(!isKeepMedia && invitation.getSender().getAvatarUrl() != null && !invitation.getSender().getAvatarUrl().isEmpty()){
                FileSupporter.loadImage(ivAvatar, invitation.getSender().getAvatarUrl());
            }
            tvName.setText(invitation.getSender().getName());
            tvTime.setText(DateFormatter.getDetailTime(invitation.getCreatedAt()));
            if(invitation.isAccepted()){
                layoutAction.setVisibility(View.GONE);
                tvStatus.setText("Đã chấp nhận lời mời");
                tvStatus.setVisibility(View.VISIBLE);
            } else {
                layoutAction.setVisibility(View.VISIBLE);
                tvStatus.setVisibility(View.GONE);
            }
            btnAccept.setOnClickListener(v -> listener.onAccept(invitation));
            btnReject.setOnClickListener(v -> listener.onReject(invitation));
        }
    }

}
