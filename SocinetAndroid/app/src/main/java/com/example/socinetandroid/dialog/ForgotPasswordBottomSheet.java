package com.example.socinetandroid.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.socinetandroid.R;
import com.example.socinetandroid.databinding.BottomSheetForgotPasswordBinding;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.Account;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.repository.AuthRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.EditTextDebounce;
import com.example.socinetandroid.utils.FileSupporter;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.utils.Validator;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetForgotPasswordBinding bd;
    private AuthRepository authRepository;
    private EditTextDebounce debounce;
    private final MutableLiveData<Account> liveAccount = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> liveSendingOtp = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> livePhase = new MutableLiveData<>(1);
    public ForgotPasswordBottomSheet(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bd = BottomSheetForgotPasswordBinding.inflate(getLayoutInflater());
        return bd.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if(dialog != null) Helper.setupBottomSheetDialog(dialog, bd.getRoot(), true, R.id.layoutForgotPassword);
        init();
        setEvent();
    }

    private void init(){
        authRepository = RetrofitClient.createInstance(getContext()).create(AuthRepository.class);
        debounce = new EditTextDebounce();
    }
    private void setEvent(){
        liveAccount.observe(getViewLifecycleOwner(), account -> {
            if(account == null){
                bd.tvNotFound.setVisibility(View.VISIBLE);
                bd.tvNotFound.setText("Không tìm thấy");
                bd.layoutUserItem.setVisibility(View.GONE);
                bd.btnNext.setVisibility(View.GONE);
            } else{
                bd.tvNotFound.setVisibility(View.GONE);
                bd.tvName.setText(account.getUser().getName());
                FileSupporter.loadImage(bd.ivAvatar, account.getUser().getAvatarUrl());
                bd.layoutUserItem.setVisibility(View.VISIBLE);
                bd.btnNext.setVisibility(View.VISIBLE);
            }
        });

        livePhase.observe(getViewLifecycleOwner(), phase -> {
            if(phase == 1){
                bd.layoutPhase1.setVisibility(View.VISIBLE);
                bd.layoutPhase2.setVisibility(View.GONE);
                bd.layoutActionPhase2.setVisibility(View.GONE);
                if(liveAccount.getValue() != null)
                    bd.btnNext.setVisibility(View.VISIBLE);
            } else if (phase == 2) {
                bd.layoutPhase1.setVisibility(View.GONE);
                bd.layoutPhase2.setVisibility(View.VISIBLE);
                bd.layoutActionPhase2.setVisibility(View.VISIBLE);
                bd.btnNext.setVisibility(View.GONE);
            }
        });
        liveSendingOtp.observe(getViewLifecycleOwner(), isSending -> {
            if(isSending){
                bd.tvOtp.setText("Đang gửi OTP...");
                bd.tvGetOtp.setVisibility(View.GONE);
            } else {
                bd.tvOtp.setText("Chưa nhận được OTP?");
                bd.tvGetOtp.setVisibility(View.VISIBLE);
            }
        });



        debounce.setDebounce(bd.edtEmail, 1000, text -> {
            if(!Validator.isValidEmail(text)) return;
            bd.tvNotFound.setText("Đang tìm...");
            authRepository.searchByEmail(text).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            if(result.getData() == null){
                                liveAccount.setValue(null);
                            } else {
                                Account account = Helper.convertDataToType(result.getData(), Helper.getType(Account.class));
                                liveAccount.setValue(account);
                            }
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            Toast.makeText(getContext(), "Tìm kiếm thông tin tài khoản thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                    Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        bd.btnNext.setOnClickListener(v -> {
            livePhase.setValue(2);
            liveSendingOtp.setValue(true);
            authRepository.sendOtp(liveAccount.getValue().getEmail()).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            liveSendingOtp.setValue(false);
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            Toast.makeText(getContext(), "Gửi OTP thất bại", Toast.LENGTH_SHORT).show();
                            liveSendingOtp.setValue(true);
                        }
                    });
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                    Log.e("API", throwable.getMessage());
                }
            });
        });
        bd.btnBack.setOnClickListener(v -> {
            livePhase.setValue(1);
        });
        bd.btnUpdate.setOnClickListener(v -> {
            String password = bd.edtNewPassword.getText().toString();
            String confirm = bd.edtConfirm.getText().toString();
            String otp = bd.edtOtp.getText().toString();
            if(password.length() < 6) {
                Toast.makeText(getContext(), "Mật khẩu ít nhât 6 kí tự", Toast.LENGTH_SHORT).show();
                bd.edtNewPassword.requestFocus();
                return;
            }
            if(!password.equals(confirm)){
                Toast.makeText(getContext(), "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
                bd.edtConfirm.requestFocus();
                return;
            }
            if(otp.isEmpty()){
                Toast.makeText(getContext(), "Hãy điền mã OTP", Toast.LENGTH_SHORT).show();
                bd.edtOtp.requestFocus();
                return;
            }
            assert liveAccount.getValue() != null;
            authRepository.forgotPassword(liveAccount.getValue().getEmail(), password, otp).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            String message = "";
                            switch(result.getMessage()){
                                case "INVALID OTP":
                                    message = "Mã OTP không hợp lệ";
                                    bd.edtOtp.requestFocus();
                                    break;
                                case "TOO SHORT PASSWORD":
                                    message = "Mật khẩu tối thiểu 6 kí tự";
                                    break;
                                case "ACCOUNT NOT FOUND":
                                    message = "Không tìm thấy tài khoản";
                                    break;
                            }
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
