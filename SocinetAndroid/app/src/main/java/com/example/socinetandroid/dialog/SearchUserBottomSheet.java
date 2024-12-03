package com.example.socinetandroid.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.socinetandroid.R;
import com.example.socinetandroid.activity.ProfileActivity;
import com.example.socinetandroid.adapter.UserAdapter;
import com.example.socinetandroid.databinding.BottomSheetSearchUserBinding;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.interfaces.IUserListener;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.User;
import com.example.socinetandroid.repository.UserRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.EditTextDebounce;
import com.example.socinetandroid.utils.Helper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchUserBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetSearchUserBinding bd;
    private UserAdapter userAdapter;
    private UserRepository userRepository;
    private EditTextDebounce debounce;
    public SearchUserBottomSheet(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bd = BottomSheetSearchUserBinding.inflate(getLayoutInflater());
        return bd.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        Helper.setupBottomSheetDialog(dialog, bd.getRoot(), true, R.id.layoutSearchUser);
        init();
        setEvent();
    }
    private void init(){
        userRepository = RetrofitClient.createInstance(getContext()).create(UserRepository.class);
        debounce = new EditTextDebounce();
        userAdapter = new UserAdapter(new ArrayList<>(), UserAdapter.HORIZONTAL_ITEM, new IUserListener() {
            @Override
            public void onItemClick(User user) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra(ProfileActivity.USER_ID, user.getId());
                startActivity(intent);
                dismiss();
            }
        });
        bd.rcvUser.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        bd.rcvUser.setAdapter(userAdapter);
    }
    private void setEvent(){
        debounce.setDebounce(bd.edtSearch, 1000, text -> {
            userRepository.getUserList(text).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            List<User> userList = Helper.convertDataToType(result.getData(), Helper.getListType(User.class));
                            userAdapter.setList(userList);
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            Toast.makeText(getContext(), "Lấy danh sách thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
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
}
