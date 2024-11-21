package com.example.socinetandroid.dialog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.socinetandroid.databinding.BottomSheetUpdateRoomBinding;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Room;
import com.example.socinetandroid.repository.RoomRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.request.RoomRequest;
import com.example.socinetandroid.utils.FileSupporter;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.viewmodel.RoomViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateRoomBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetUpdateRoomBinding bd;
    private RoomViewModel roomViewModel;
    private RoomRepository roomRepository;
    private Uri fileUri;
    private final ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    assert result.getData() != null;
                    Uri uri = result.getData().getData();
                    if(uri != null) {
                        String mimeType = requireActivity().getContentResolver().getType(uri);
                        if(mimeType != null){
                            if(mimeType.startsWith("image/")){
                                showImage(uri);
                            }
                        }
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bd = BottomSheetUpdateRoomBinding.inflate(getLayoutInflater());
        return bd.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
        setEvent();
    }

    public void initialize(){
        roomViewModel = new ViewModelProvider(requireActivity()).get(RoomViewModel.class);
        roomRepository = RetrofitClient.createInstance(getContext()).create(RoomRepository.class);
        Room currentRoom = roomViewModel.getLiveRoom().getValue();
        if(currentRoom == null) dismiss();
        FileSupporter.loadImage(bd.ivImage, currentRoom.getAvatarUrl());
        bd.edtName.setText(currentRoom.getName());
    }
    public void setEvent(){
        bd.btnUpdate.setOnClickListener(v -> {
            String content = bd.edtName.getText().toString().trim();
            if(content.isEmpty() && fileUri == null) return;
            RoomRequest roomRequest = new RoomRequest(requireActivity(), content, fileUri);
            roomRepository.updateRoom(roomViewModel.getLiveRoom().getValue().getId(), roomRequest.getNamePart(), roomRequest.getAvatarPart()).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            Room room = Helper.convertDataToType(result.getData(), Helper.getType(Room.class));
                            roomViewModel.setRoom(room);
                            dismiss();
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            Toast.makeText(getContext(), "Cập nhật thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                    Log.e("API", throwable.getMessage());
                }
            });
        });

        bd.ivImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            mActivityResultLauncher.launch(Intent.createChooser(intent, "Chọn ảnh"));
        });

        bd.cvClose.setOnClickListener(v -> {
            FileSupporter.loadImage(bd.ivImage, roomViewModel.getLiveRoom().getValue().getAvatarUrl());
            bd.cvClose.setVisibility(View.GONE);
            this.fileUri = null;
        });
    }

    private void showImage(Uri uri){
        this.fileUri = uri;
        bd.ivImage.setImageURI(uri);
        bd.cvClose.setVisibility(View.VISIBLE);
    }
}
