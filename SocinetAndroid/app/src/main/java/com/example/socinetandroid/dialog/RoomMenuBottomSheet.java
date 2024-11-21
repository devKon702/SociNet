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
import com.example.socinetandroid.adapter.RoomMemberAdapter;
import com.example.socinetandroid.databinding.BottomSheetRoomMenuBinding;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Room;
import com.example.socinetandroid.model.RoomMember;
import com.example.socinetandroid.model.RoomsActivity;
import com.example.socinetandroid.repository.RoomRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.FileSupporter;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.utils.SocketManager;
import com.example.socinetandroid.viewmodel.AppViewModel;
import com.example.socinetandroid.viewmodel.RoomViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomMenuBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetRoomMenuBinding bd;
    private RoomViewModel roomViewModel;
    private RoomRepository roomRepository;
    private RoomMemberAdapter roomMemberAdapter;
    private SocketManager socketManager;
    private AppViewModel appViewModel;
    private int confirmCount = 0;

    public RoomMenuBottomSheet(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bd = BottomSheetRoomMenuBinding.inflate(getLayoutInflater());
        return bd.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        Helper.setupBottomSheetDialog(dialog, bd.getRoot(), true, R.id.layoutRoomMenu);
        initialize();
        setEvent();
    }

    public void initialize(){
        appViewModel = ((MyApplication) requireActivity().getApplication()).getAppViewModel();
        socketManager = SocketManager.getInstance(requireActivity().getApplication());
        roomViewModel = new ViewModelProvider(requireActivity()).get(RoomViewModel.class);
        Room currentRoom = roomViewModel.getLiveRoom().getValue();
        if(currentRoom == null) dismiss();
        roomRepository = RetrofitClient.createInstance(getContext()).create(RoomRepository.class);
        roomMemberAdapter = new RoomMemberAdapter(currentRoom.getMembers());
        FileSupporter.loadImage(bd.ivAvatar, currentRoom.getAvatarUrl());
        bd.tvName.setText(currentRoom.getName());
        bd.rcvMember.setLayoutManager(new LinearLayoutManager(getContext()));
        bd.rcvMember.setAdapter(roomMemberAdapter);
        if(currentRoom.isAdmin()){
            bd.btnQuit.setText("Giải tán");
        } else{
            bd.btnQuit.setText("Rời nhóm");
            bd.btnAddMember.setVisibility(View.GONE);
        }
    }

    public void setEvent(){
        roomViewModel.getLiveRoom().observe(getViewLifecycleOwner(), room -> {
            FileSupporter.loadImage(bd.ivAvatar, room.getAvatarUrl());
            roomMemberAdapter.setList(room.getMembers());
            bd.tvName.setText(room.getName());
        });
        bd.btnUpdate.setOnClickListener(v -> {
            UpdateRoomBottomSheet dialog = new UpdateRoomBottomSheet();
            dialog.show(getParentFragmentManager(), dialog.getTag());
        });
        bd.btnMember.setOnClickListener(v -> {
            ViewGroup.LayoutParams params = bd.layoutMemberList.getLayoutParams();
            if (params.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                params.height = 0;
            } else {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            bd.layoutMemberList.setLayoutParams(params);
        });
        bd.btnAddMember.setOnClickListener(v -> {
            AddMemberBottomSheet dialog = new AddMemberBottomSheet();
            dialog.show(getParentFragmentManager(), dialog.getTag());
        });
        bd.btnQuit.setOnClickListener(v -> {
            Room currentRoom = roomViewModel.getLiveRoom().getValue();
            if(currentRoom != null){
                if(confirmCount == 0) {
                    confirmCount++;
                    Toast.makeText(getContext(), "Nhấn thêm lần nữa để " + (currentRoom.isAdmin() ? "giải tán" : "rời nhóm"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(confirmCount > 1) return;
                if(currentRoom.isAdmin()){
                    roomRepository.deleteRoom(currentRoom.getId()).enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                                @Override
                                public void onSuccess(ApiResponse result) {
                                    Room room = Helper.convertDataToType(result.getData(), Helper.getType(Room.class));
                                    socketManager.disableRoom(room.getId());
                                    appViewModel.disableRoom(room.getId());
                                    dismiss();
                                }

                                @Override
                                public void onFail(ApiResponse result) {
                                    Toast.makeText(getContext(), "Giải tán nhóm thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                            Log.e("API", throwable.getMessage());
                        }
                    });
                }
                else {
                    roomRepository.quitRoom(currentRoom.getId()).enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                                @Override
                                public void onSuccess(ApiResponse result) {
                                    RoomsActivity activity = Helper.convertDataToType(result.getData(), Helper.getType(RoomsActivity.class));
                                    Toast.makeText(getContext(), "Rời nhóm thành công", Toast.LENGTH_SHORT).show();
                                    socketManager.quitRoom(activity.getRoomId(), activity);
                                    appViewModel.quitRoom(activity.getRoomId());
                                    dismiss();
                                }

                                @Override
                                public void onFail(ApiResponse result) {
                                    Toast.makeText(getContext(), "Rời nhóm thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
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
        });
    }
}
