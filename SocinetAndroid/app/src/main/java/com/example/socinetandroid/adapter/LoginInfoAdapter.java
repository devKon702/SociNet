package com.example.socinetandroid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socinetandroid.R;
import com.example.socinetandroid.interfaces.IGetIpInfoHandler;
import com.example.socinetandroid.interfaces.ILoginInfoListener;
import com.example.socinetandroid.model.IPResponse;
import com.example.socinetandroid.model.LoginInfo;
import com.example.socinetandroid.utils.DateFormatter;
import com.example.socinetandroid.utils.Helper;

import java.util.List;

public class LoginInfoAdapter extends RecyclerView.Adapter<LoginInfoAdapter.LoginInfoViewHolder> {
    private List<LoginInfo> mLoginInfoList;
    private ILoginInfoListener listener;
    private Context context;
    private String currentIp;

    public LoginInfoAdapter(List<LoginInfo> mLoginInfoList, ILoginInfoListener listener, Context context, String currentIp) {
        this.mLoginInfoList = mLoginInfoList;
        this.listener = listener;
        this.context = context;
        this.currentIp = currentIp;
    }

    @NonNull
    @Override
    public LoginInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_login_info_item, parent, false);
        return new LoginInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoginInfoViewHolder holder, int position) {
        LoginInfo loginInfo = mLoginInfoList.get(position);
        if(loginInfo == null) return;
        holder.bind(loginInfo);
    }

    @Override
    public int getItemCount() {
        if(mLoginInfoList == null) return 0;
        return mLoginInfoList.size();
    }

    public class LoginInfoViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivDevice, btnRemove;
        private TextView tvName, tvTime, tvCurrent, tvLocation;

        public LoginInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDevice = itemView.findViewById(R.id.ivDevice);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCurrent = itemView.findViewById(R.id.tvCurrent);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }
        public void bind(LoginInfo loginInfo){
            switch(Helper.getDeviceType(loginInfo.getUserAgent())){
                case "Mobile":
                    ivDevice.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_mobile));
                    tvName.setText("Di động");
                    break;
                case "PC":
                    ivDevice.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_laptop));
                    tvName.setText("Máy tính");
                    break;
                case "Tablet":
                    ivDevice.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_devices));
                    tvName.setText("Tablet");
                    break;
                default:
                    ivDevice.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_devices));
                    tvName.setText("Không xác định");
            }
            Helper.getIpInfo(loginInfo.getIp(), new IGetIpInfoHandler() {
                @Override
                public void onSuccess(IPResponse response) {
                    String location = response.getRegionName();
                    tvLocation.setText(location);
                    tvTime.setText(DateFormatter.getTimeDifference(loginInfo.getUpdatedAt()));
                }

                @Override
                public void onFail() {
                    Log.e("API", "Lấy thông tin ip thất bại");
                }
            });
            if(loginInfo.getIp().equals(currentIp)){
                tvCurrent.setVisibility(View.VISIBLE);
                tvTime.setVisibility(View.GONE);
            } else {
                tvCurrent.setVisibility(View.GONE);
                tvTime.setVisibility(View.VISIBLE);
                btnRemove.setVisibility(View.VISIBLE);
                btnRemove.setOnClickListener(v -> listener.onRemove(loginInfo));
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<LoginInfo> list){
        this.mLoginInfoList = list;
        notifyDataSetChanged();
    }
    public void removeItem(LoginInfo loginInfo){
        int position = findPositionById(loginInfo.getId());
        if(position != -1){
            mLoginInfoList.set(position, loginInfo);
            notifyItemRemoved(position);
        }
    }
    private int findPositionById(long id){
        for(int i=0; i<mLoginInfoList.size(); i++){
            if(mLoginInfoList.get(i).getId() == id) return i;
        }
        return -1;
    }
}
