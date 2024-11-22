package com.example.socinetandroid.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.socinetandroid.R;
import com.example.socinetandroid.databinding.ActivitySignupBinding;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.repository.AuthRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.request.SignUpRequest;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.utils.Validator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding bd;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bd = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());
        init();
        setEvent();
    }
    private void init(){
        authRepository = RetrofitClient.createInstance(this).create(AuthRepository.class);
    }
    private void setEvent(){
        bd.btnBack.setOnClickListener(v -> finish());
        bd.tvGetOtp.setOnClickListener(v -> {
            bd.tvGetOtp.setClickable(false);
            String email = bd.edtEmail.getText().toString().trim();
            if(email.isEmpty() || !Validator.isValidEmail(email)){
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                bd.tvGetOtp.setClickable(true);
                return;
            }
            bd.tvGetOtp.setVisibility(View.GONE);
            bd.tvOtp.setText("Đang gửi...");
            authRepository.sendOtp(email).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            bd.tvOtp.setText("Chưa nhận được OTP?");
                            bd.tvGetOtp.setText("Gửi lại");
                            bd.tvGetOtp.setTextColor(ContextCompat.getColor(SignupActivity.this, R.color.orange));
                            bd.tvGetOtp.setVisibility(View.VISIBLE);
                            bd.tvGetOtp.setClickable(true);
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            bd.tvGetOtp.setVisibility(View.VISIBLE);
                            bd.tvGetOtp.setClickable(true);
                            bd.tvOtp.setText("");
                            Toast.makeText(SignupActivity.this, "Gửi OTP thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                    Log.e("API", throwable.getMessage());
                    bd.tvGetOtp.setVisibility(View.VISIBLE);
                    bd.tvGetOtp.setClickable(true);
                    bd.tvOtp.setText("");
                }
            });
        });
        
        bd.btnSignup.setOnClickListener(v -> {
            bd.btnSignup.setClickable(false);
            bd.btnSignup.setText("Đang xử lí");

            String username = bd.edtUsername.getText().toString().trim();
            String password = bd.edtPassword.getText().toString().trim();
            String confirm = bd.edtConfirm.getText().toString().trim();
            String name = bd.edtName.getText().toString().trim();
            String email = bd.edtEmail.getText().toString().trim();
            String otp = bd.edtOtp.getText().toString().trim();
            boolean isValid = true;
            if(username.isEmpty() || password.isEmpty() || confirm.isEmpty() || email.isEmpty() || otp.isEmpty()){
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            else if(!password.equals(confirm)){
                Toast.makeText(this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            else if (!Validator.isValidEmail(email)){
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            if(isValid){
                SignUpRequest request = new SignUpRequest(username, password, name, email, otp);
                authRepository.signUp(request).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                            @Override
                            public void onSuccess(ApiResponse result) {
                                Toast.makeText(SignupActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onFail(ApiResponse result) {
                                String message = "";
                                if(result.getMessage() != null)
                                    switch(result.getMessage()){
                                        case "INVALID EMAIL":
                                            bd.edtEmail.requestFocus();
                                            bd.scrollView.post(()->bd.scrollView.smoothScrollTo(0, bd.edtEmail.getTop()));
                                            message = "Email không hợp lệ";
                                            break;
                                        case "USED USERNAME":
                                            bd.edtUsername.requestFocus();
                                            bd.scrollView.post(()->bd.scrollView.smoothScrollTo(0, bd.edtUsername.getTop()));
                                            message = "Tên đăng nhập đã được sử dụng";
                                            break;
                                        case "INVALID PASSWORD":
                                            bd.edtPassword.requestFocus();
                                            bd.scrollView.post(()->bd.scrollView.smoothScrollTo(0, bd.edtPassword.getTop()));
                                            message = "Mật khẩu ít nhất 6 kí tự";
                                            break;
                                        case "USED EMAIL":
                                            bd.edtEmail.requestFocus();
                                            bd.scrollView.post(()->bd.scrollView.smoothScrollTo(0, bd.edtEmail.getTop()));
                                            message = "Email đã được sử dụng";
                                            break;
                                        case "INVALID OTP":
                                            bd.edtOtp.requestFocus();
                                            bd.scrollView.post(()->bd.scrollView.smoothScrollTo(0, bd.edtOtp.getTop()));
                                            message = "OTP không hợp lệ";
                                            break;
                                        default:
                                            message = result.getMessage();
                                    }
                                Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
                                bd.btnSignup.setText("Đăng ký");
                                bd.btnSignup.setClickable(true);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                        Toast.makeText(SignupActivity.this, "", Toast.LENGTH_SHORT).show();
                        bd.btnSignup.setText("Đăng ký");
                        bd.btnSignup.setClickable(true);
                    }
                });
            } else {
                bd.btnSignup.setClickable(true);
                bd.btnSignup.setText("Đăng ký");
            }
        });
    }

}