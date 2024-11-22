package com.example.socinetandroid.dialog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.R;
import com.example.socinetandroid.databinding.BottomSheetCreateRoomBinding;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.RealtimeRoom;
import com.example.socinetandroid.model.Room;
import com.example.socinetandroid.repository.RoomRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.request.RoomRequest;
import com.example.socinetandroid.utils.FileSupporter;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.utils.SocketManager;
import com.example.socinetandroid.viewmodel.AppViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateRoomBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetCreateRoomBinding bd;
    private AppViewModel appViewModel;
    private SocketManager socketManager;
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
        bd = BottomSheetCreateRoomBinding.inflate(getLayoutInflater());
        return bd.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setEvent();
    }

    private void init(){
        appViewModel = ((MyApplication) requireActivity().getApplication()).getAppViewModel();
        socketManager = SocketManager.getInstance(requireActivity().getApplication());
        roomRepository = RetrofitClient.createInstance(getContext()).create(RoomRepository.class);
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if(dialog != null)
            Helper.setupBottomSheetDialog(dialog, bd.getRoot(), false, -1);
    }
    private void setEvent(){
        bd.ivImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            mActivityResultLauncher.launch(Intent.createChooser(intent, "Chọn ảnh"));
        });
        bd.cvClose.setOnClickListener(v -> {
            bd.cvClose.setVisibility(View.GONE);
            bd.ivImage.setImageURI(null);
            this.fileUri = null;
        });

        bd.btnCreate.setOnClickListener(v -> {
            bd.btnCreate.setClickable(false);
            String name = bd.edtName.getText().toString().trim();
            if(name.isEmpty() || fileUri == null) {
                Toast.makeText(getContext(), "Hãy điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                bd.btnCreate.setClickable(true);
                return;
            };
            RoomRequest roomRequest = new RoomRequest(requireActivity(), name, fileUri);
            roomRepository.createRoom(roomRequest.getNamePart(), roomRequest.getAvatarPart()).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            Toast.makeText(getContext(), "Tạo phòng thành công", Toast.LENGTH_SHORT).show();
                            Room room = Helper.convertDataToType(result.getData(), Helper.getType(Room.class));
                            appViewModel.getLiveRealtimeRoomList().getValue().add(0, new RealtimeRoom(room, "", false));
                            appViewModel.getLiveRealtimeRoomList().setValue(appViewModel.getLiveRealtimeRoomList().getValue());
                            socketManager.newRoom(room.getId());
                            dismiss();
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            Toast.makeText(getContext(), "Tạo phòng thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    bd.btnCreate.setClickable(true);
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                    Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    bd.btnCreate.setClickable(true);
                }
            });
        });
    }
    private void showImage(Uri uri){
        this.fileUri = uri;
        bd.ivImage.setImageURI(uri);
        bd.cvClose.setVisibility(View.VISIBLE);
    }
}
