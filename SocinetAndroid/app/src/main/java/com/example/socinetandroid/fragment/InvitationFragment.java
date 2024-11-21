package com.example.socinetandroid.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.adapter.InvitationAdapter;
import com.example.socinetandroid.databinding.FragmentInvitationBinding;
import com.example.socinetandroid.interfaces.IInvitationListener;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Invitation;
import com.example.socinetandroid.repository.FriendRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.utils.SocketManager;
import com.example.socinetandroid.viewmodel.AppViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvitationFragment extends Fragment implements IInvitationListener{
    public static final String TAG = "INVITATION";
    private FriendRepository friendRepository;
    private InvitationAdapter adapter;
    private AppViewModel appViewModel;
    private SocketManager socketManager;
    private FragmentInvitationBinding bd;

    public InvitationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        init();
        setEvent();
        return bd.getRoot();
    }

    private void init() {
        appViewModel = ((MyApplication)requireActivity().getApplication()).getAppViewModel();
        socketManager = SocketManager.getInstance(requireActivity().getApplication());
        bd = FragmentInvitationBinding.inflate(getLayoutInflater());
        bd.rcvInvitation.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new InvitationAdapter(new ArrayList<>(), this);
        bd.rcvInvitation.setAdapter(adapter);
        friendRepository = RetrofitClient.createInstance(getContext()).create(FriendRepository.class);
    }

    private void setEvent(){
        friendRepository.getFriendInvitation().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        List<Invitation> invitationList = Helper.convertDataToType(result.getData(), Helper.getListType(Invitation.class));
                        appViewModel.getLiveRealtimeInvitationList().setValue(invitationList);
                        if(invitationList.isEmpty()){
                            bd.tvPlaceHolder.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFail(ApiResponse result) {
                        Toast.makeText(getContext(), "Lấy danh sách kết bạn thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.e("API", throwable.getMessage());
            }
        });
        appViewModel.getLiveRealtimeInvitationList().observe(getViewLifecycleOwner(), value -> {
            bd.tvPlaceHolder.setVisibility(value.isEmpty() ? View.VISIBLE : View.GONE);
            adapter.updateList(new ArrayList<>(value));
        });
    }

    @Override
    public void onAccept(Invitation invitation) {
        friendRepository.responseFriendInvitation(invitation.getId(), true).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        Toast.makeText(getContext(), "Chấp nhận kết bạn thành công", Toast.LENGTH_SHORT).show();
                        Invitation invitation = Helper.convertDataToType(result.getData(), Helper.getType(Invitation.class));
                        adapter.updateItem(invitation);
                        appViewModel.newFriend(invitation.getSender());
                        socketManager.responseInvitation(invitation.getSender().getId(), true);
                    }

                    @Override
                    public void onFail(ApiResponse result) {
                        Toast.makeText(getContext(), "Chấp nhận thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.e("API", throwable.getMessage());
            }
        });
    }

    @Override
    public void onReject(Invitation invitation) {
        friendRepository.responseFriendInvitation(invitation.getId(), false).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        Toast.makeText(getContext(), "Từ chối kết bạn thành công", Toast.LENGTH_SHORT).show();
                        socketManager.responseInvitation(invitation.getSender().getId(), false);
                        appViewModel.removeFriendInvitation(invitation);
                    }

                    @Override
                    public void onFail(ApiResponse result) {
                        Toast.makeText(getContext(), "Từ chối thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.e("API", throwable.getMessage());
            }
        });
    }
}