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

import com.example.socinetandroid.databinding.ActivityConversationBinding;
import com.example.socinetandroid.databinding.BottomSheetActivityActionBinding;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.RoomsActivity;
import com.example.socinetandroid.repository.RoomRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.utils.SocketManager;
import com.example.socinetandroid.viewmodel.RoomViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityActionBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetActivityActionBinding bd;
    private RoomsActivity activity;
    private RoomViewModel roomViewModel;
    private RoomRepository roomRepository;
    private SocketManager socketManager;
    private int confirmCount = 0;

    public ActivityActionBottomSheet(RoomsActivity activity){
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bd = BottomSheetActivityActionBinding.inflate(getLayoutInflater());
        return bd.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
        setEvent();
    }

    public void initialize(){
        socketManager = SocketManager.getInstance(requireActivity().getApplication());
        roomViewModel = new ViewModelProvider(requireActivity()).get(RoomViewModel.class);
        roomRepository = RetrofitClient.createInstance(getContext()).create(RoomRepository.class);
    }

    private void setEvent(){
        bd.tvUpdate.setOnClickListener(v -> {
            roomViewModel.updatingActivity(activity);
            dismiss();
        });

        bd.tvRemove.setOnClickListener(v -> {
            if(confirmCount == 0){
                Toast.makeText(getContext(), "Nhấn thêm lần nữa để thu hồi", Toast.LENGTH_SHORT).show();
            } else if(confirmCount == 1){
                roomRepository.removeChat(activity.getId()).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                            @Override
                            public void onSuccess(ApiResponse result) {
                                RoomsActivity removedActivity = Helper.convertDataToType(result.getData(), Helper.getType(RoomsActivity.class));
                                roomViewModel.updatedActivity(removedActivity);
                                socketManager.updateRoomMessage(removedActivity.getRoomId(), removedActivity);
                            }

                            @Override
                            public void onFail(ApiResponse result) {
                                Toast.makeText(getContext(), "Thu hồi thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        dismiss();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                        Log.e("API", throwable.getMessage());
                    }
                });
            }
            confirmCount++;
        });
    }
}
