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

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.R;
import com.example.socinetandroid.databinding.BottomSheetUpdateProfileBinding;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.User;
import com.example.socinetandroid.repository.UserRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.request.UserRequest;
import com.example.socinetandroid.utils.FileSupporter;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.viewmodel.AppViewModel;
import com.example.socinetandroid.viewmodel.ProfileViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetUpdateProfileBinding bd;
    private User user;
    private Uri avatarUri;
    private UserRepository userRepository;
    private AppViewModel appViewModel;

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

    public UpdateProfileBottomSheet() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bd = BottomSheetUpdateProfileBinding.inflate(getLayoutInflater());
        return bd.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        Helper.setupBottomSheetDialog(dialog, bd.getRoot(), true, R.id.layoutUpdateProfile);
        init();
        setEvent();
    }

    private void init(){
        appViewModel = ((MyApplication) requireActivity().getApplication()).getAppViewModel();
        this.user = appViewModel.getUser();
        userRepository = RetrofitClient.createInstance(getContext()).create(UserRepository.class);
        FileSupporter.loadImage(bd.ivAvatar, user.getAvatarUrl());
        bd.edtName.setText(user.getName());
        bd.edtSchool.setText(user.getSchool());
        bd.edtFrom.setText(user.getAddress());
        bd.edtPhone.setText(user.getPhone());
        if(user.isMale()){
            bd.rbtnMale.setChecked(true);
        } else{
            bd.rbtnFemale.setChecked(true);
        }
    }

    private void setEvent(){
        bd.ivUpdateAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            mActivityResultLauncher.launch(Intent.createChooser(intent, "Chọn ảnh"));
        });

        bd.btnRemoveAvatar.setOnClickListener(v -> {
            FileSupporter.loadImage(bd.ivAvatar, user.getAvatarUrl());
            bd.btnRemoveAvatar.setVisibility(View.GONE);
        });

        bd.btnUpdate.setOnClickListener(v -> {
            String name = bd.edtName.getText().toString().trim();
            String school = bd.edtSchool.getText().toString().trim();
            String address = bd.edtFrom.getText().toString().trim();
            String phone = bd.edtPhone.getText().toString().trim();
            boolean isMale = bd.rbtnMale.isChecked();
            UserRequest userRequest = new UserRequest.Builder()
                            .name(name)
                            .school(school)
                            .address(address)
                            .phone(phone)
                            .isMale(isMale)
                            .avatarFile(requireActivity(), avatarUri)
                            .build();
            userRepository.updateUserInfo(
                    userRequest.getName(),
                    userRequest.getPhone(),
                    userRequest.getSchool(),
                    userRequest.getAddress(),
                    userRequest.getIsMale(),
                    userRequest.getAvatarFile()).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            User data = Helper.convertDataToType(result.getData(), Helper.getType(User.class));
                            appViewModel.setUser(data);
                            ProfileViewModel profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
                            profileViewModel.setUser(data);
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            Toast.makeText(getContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                    Log.e("API", throwable.getMessage());
                }
            });
        });
    }

    private void showImage(Uri uri){
        avatarUri = uri;
        bd.ivAvatar.setImageURI(uri);
        bd.ivAvatar.setVisibility(View.VISIBLE);
        bd.btnRemoveAvatar.setVisibility(View.VISIBLE);
    }
}
