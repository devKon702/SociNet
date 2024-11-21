package com.example.socinetandroid.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.R;
import com.example.socinetandroid.adapter.ChooseMemberAdapter;
import com.example.socinetandroid.databinding.BottomSheetAddMemberBinding;
import com.example.socinetandroid.interfaces.IChooseUserListener;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Room;
import com.example.socinetandroid.model.RoomsActivity;
import com.example.socinetandroid.model.User;
import com.example.socinetandroid.repository.FriendRepository;
import com.example.socinetandroid.repository.RoomRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.utils.SocketManager;
import com.example.socinetandroid.viewmodel.AppViewModel;
import com.example.socinetandroid.viewmodel.RoomViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMemberBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetAddMemberBinding bd;
    private RoomRepository roomRepository;
    private FriendRepository friendRepository;
    private RoomViewModel roomViewModel;
    private AppViewModel appViewModel;
    private SocketManager socketManager;
    private ChooseMemberAdapter chooseMemberAdapter;
    private List<Long> mCheckedUserIdList;
    private Room currentRoom;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bd = BottomSheetAddMemberBinding.inflate(getLayoutInflater());
        return bd.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        Helper.setupBottomSheetDialog(dialog, bd.getRoot(), true, R.id.layoutAddMember);
        initialize();
        setEvent();
    }

    private void initialize(){
        roomRepository = RetrofitClient.createInstance(getContext()).create(RoomRepository.class);
        friendRepository = RetrofitClient.createInstance(getContext()).create(FriendRepository.class);
        roomViewModel = new ViewModelProvider(requireActivity()).get(RoomViewModel.class);
        appViewModel = ((MyApplication) requireActivity().getApplication()).getAppViewModel();
        socketManager = SocketManager.getInstance(requireActivity().getApplication());
        currentRoom = roomViewModel.getLiveRoom().getValue();
        if(currentRoom == null) dismiss();
        mCheckedUserIdList = new ArrayList<>();
        chooseMemberAdapter = new ChooseMemberAdapter(new ArrayList<>(), new IChooseUserListener() {
            @Override
            public void onClick(User user, boolean isChecked) {
                if(isChecked){
                    mCheckedUserIdList.add(user.getId());
                    bd.btnAdd.setVisibility(View.VISIBLE);
                } else{
                    removeUser(user);
                    if(mCheckedUserIdList.isEmpty())
                        bd.btnAdd.setVisibility(View.GONE);
                }
            }
        });
        bd.rcvFriend.setAdapter(chooseMemberAdapter);
        bd.rcvFriend.setLayoutManager(new LinearLayoutManager(getContext()));
        friendRepository.getFriendListByUser(appViewModel.getUser().getId()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        List<User> list = Helper.convertDataToType(result.getData(), Helper.getListType(User.class));
                        currentRoom.getMembers().forEach(member -> {
                            for(int i=0; i<list.size(); i++){
                                if(member.getUser().getId() == list.get(i).getId()){
                                    list.remove(i);
                                    break;
                                }
                            }
                        });
                        chooseMemberAdapter.setList(list);
                    }

                    @Override
                    public void onFail(ApiResponse result) {
                        Toast.makeText(getContext(), "Lấy danh sách bạn bè thất bại\n"+result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.e("API", throwable.getMessage());
            }
        });
    }

    private void setEvent() {
        bd.btnAdd.setOnClickListener(v -> {
            roomRepository.invite(currentRoom.getId(), mCheckedUserIdList).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            List<RoomsActivity> activityList = Helper.convertDataToType(result.getData(), Helper.getListType(RoomsActivity.class));
                            roomViewModel.addedMember(activityList);
                            activityList.forEach(activity -> {
                                socketManager.inviteToRoom(activity.getRoomId(), activity);
                            });
                            dismiss();
                        }

                        @Override
                        public void onFail(ApiResponse result) {

                        }
                    });
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {

                }
            });
        });
    }
    private void removeUser(User user){
        for(int i=0; i<mCheckedUserIdList.size(); i++){
            if(user.getId() == mCheckedUserIdList.get(i)){
                mCheckedUserIdList.remove(i);
                return;
            }
        }
    }
}
