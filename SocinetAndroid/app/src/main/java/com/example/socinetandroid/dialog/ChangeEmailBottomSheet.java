package com.example.socinetandroid.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.R;
import com.example.socinetandroid.databinding.BottomSheetChangeEmailBinding;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.Account;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.repository.AccountRepository;
import com.example.socinetandroid.repository.AuthRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.utils.Validator;
import com.example.socinetandroid.viewmodel.AppViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeEmailBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetChangeEmailBinding bd;
    private AccountRepository accountRepository;
    private AuthRepository authRepository;
    private AppViewModel appViewModel;
    public ChangeEmailBottomSheet(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bd = BottomSheetChangeEmailBinding.inflate(getLayoutInflater());
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
        authRepository = RetrofitClient.getInstance().create(AuthRepository.class);
        appViewModel = ((MyApplication) requireActivity().getApplication()).getAppViewModel();
        bd.edtEmail.setText(appViewModel.getAccount().getEmail());
    }
    private void setEvent(){
        bd.tvGetOtp.setOnClickListener(v -> {
            String email = bd.edtEmail.getText().toString().trim();
            if(email.isEmpty() || !Validator.isValidEmail(email)){
                Toast.makeText(getContext(), "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                bd.edtEmail.requestFocus();
                return;
            }
            bd.tvOtp.setText("Đang gửi mã OTP...");
            bd.tvGetOtp.setVisibility(View.GONE);
            authRepository.sendOtp(email).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            Toast.makeText(getContext(), "Gửi OTP thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                    bd.tvOtp.setText("Chưa nhận được OTP?");
                    bd.tvGetOtp.setText("Gửi lại");
                    bd.tvGetOtp.setTextColor(ContextCompat.getColor(requireActivity(), R.color.orange));
                    bd.tvGetOtp.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                    Log.e("API", throwable.getMessage());
                }
            });

        });

        bd.btnChange.setOnClickListener(v -> {
            String email = bd.edtEmail.getText().toString().trim();
            String otp = bd.edtOtp.getText().toString();
            if(email.isEmpty() || Validator.isValidEmail(email)){
                Toast.makeText(getContext(), "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                bd.edtEmail.requestFocus();
                return;
            }
            if(otp.isEmpty()){
                Toast.makeText(getContext(), "Hãy nhập mã OTP", Toast.LENGTH_SHORT).show();
                bd.edtOtp.requestFocus();
                return;
            }
            if(email.equals(appViewModel.getAccount().getEmail())){
                Toast.makeText(getContext(), "Hiện đang sử dụng email này", Toast.LENGTH_SHORT).show();
                return;
            }
            accountRepository.changeEmail(email, otp).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            Toast.makeText(getContext(), "Đổi email thành công", Toast.LENGTH_SHORT).show();
                            Account account = appViewModel.getAccount();
                            account.setEmail(email);
                            appViewModel.getLiveAccount().setValue(Helper.clone(account, Helper.getType(Account.class)));
                            dismiss();
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            String message = "";
                            switch(result.getMessage()){
                                case "USED EMAIL":
                                    message = "Email đã được sử dụng";
                                    bd.edtEmail.requestFocus();
                                    break;
                                case "INVALID OTP":
                                    message = "OTP không hợp lệ";
                                    bd.edtOtp.requestFocus();
                                    break;
                                default:
                                    message = "Thay đổi email thất bại";
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
