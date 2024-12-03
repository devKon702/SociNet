package com.example.socinetandroid.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socinetandroid.databinding.BottomSheetChangePasswordBinding;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.repository.AccountRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.Helper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetChangePasswordBinding bd;
    private AccountRepository accountRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bd = BottomSheetChangePasswordBinding.inflate(getLayoutInflater());
        return bd.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        Helper.setupBottomSheetDialog(dialog, bd.getRoot(), false, -1);
        init();
        setEvent();
    }
    private void init(){
        accountRepository = RetrofitClient.createInstance(getContext()).create(AccountRepository.class);
    }
    private void setEvent(){
        bd.btnChange.setOnClickListener(v -> {
            String newPass = bd.edtNewPassword.getText().toString();
            String oldPass = bd.edtOldPassword.getText().toString();
            String confirm = bd.edtConfirm.getText().toString();
            if(oldPass.isEmpty()){
                notify("Điền mật khẩu hiện tại");
                bd.edtOldPassword.requestFocus();
            }
            else if(newPass.isEmpty()){
                notify("Điền mật khẩu mới");
                bd.edtNewPassword.requestFocus();
            }
            else if(newPass.length() < 6){
                notify("Mật khẩu tối thiểu 6 kí tự");
                bd.edtNewPassword.requestFocus();
            }
            else if(!confirm.equals(newPass)){
                notify("Xác nhận không trùng khớp");
                bd.edtConfirm.requestFocus();
            } else {
                accountRepository.changePassword(oldPass, newPass).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                            @Override
                            public void onSuccess(ApiResponse result) {
                                ChangePasswordBottomSheet.this.notify("Thay đổi mật khẩu thành công");
                                dismiss();
                            }

                            @Override
                            public void onFail(ApiResponse result) {
                                switch(result.getMessage()){
                                    case "Password is incorrect":
                                        ChangePasswordBottomSheet.this.notify("Mật khẩu hiện tại không đúng");
                                        bd.edtOldPassword.requestFocus();
                                        break;
                                    default:
                                        ChangePasswordBottomSheet.this.notify("Thay đổi mật khẩu thất bại");
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                        Log.e("API", throwable.getMessage());
                    }
                });
            }
        });
    }
    private void notify(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
