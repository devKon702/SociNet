package com.example.socinetandroid.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.R;
import com.example.socinetandroid.adapter.LoginInfoAdapter;
import com.example.socinetandroid.databinding.BottomSheetLoginInfoBinding;
import com.example.socinetandroid.interfaces.ILoginInfoListener;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.LoginInfo;
import com.example.socinetandroid.repository.AccountRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.viewmodel.AppViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginInfoBottomSheet extends BottomSheetDialogFragment implements ILoginInfoListener{
    private BottomSheetLoginInfoBinding bd;
    private AppViewModel appViewModel;
    private AccountRepository accountRepository;
    private LoginInfoAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bd = BottomSheetLoginInfoBinding.inflate(getLayoutInflater());
        return bd.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomSheetDialog dialog = (BottomSheetDialog)getDialog();
        Helper.setupBottomSheetDialog(dialog, bd.getRoot(), true, R.id.layoutLoginInfo);
        init();
        setEvent();
    }

    private void init(){
        appViewModel = ((MyApplication) requireActivity().getApplication()).getAppViewModel();
        accountRepository = RetrofitClient.createInstance(getContext()).create(AccountRepository.class);
        adapter = new LoginInfoAdapter(new ArrayList<>(), this, getContext(), appViewModel.getLiveIp().getValue());
        bd.rcvLoginInfo.setLayoutManager(new LinearLayoutManager(getContext()));
        bd.rcvLoginInfo.setAdapter(adapter);
        accountRepository.getLoginSession().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        List<LoginInfo> list = Helper.convertDataToType(result.getData(), Helper.getListType(LoginInfo.class));
                        adapter.setList(list);
                    }

                    @Override
                    public void onFail(ApiResponse result) {
                        Toast.makeText(getContext(), "Lấy thông tin phiên đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.e("API", throwable.getMessage());
            }
        });

    }

    private void setEvent() {

    }

    @Override
    public void onRemove(LoginInfo loginInfo) {
        accountRepository.removeLoginSession(loginInfo.getId()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        adapter.removeItem(loginInfo);
                    }

                    @Override
                    public void onFail(ApiResponse result) {
                        Toast.makeText(getContext(), "Xóa phiên đăng nhập thất bại", Toast.LENGTH_SHORT).show();
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
