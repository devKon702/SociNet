package com.example.socinetandroid.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.activity.LoginActivity;
import com.example.socinetandroid.activity.ProfileActivity;
import com.example.socinetandroid.databinding.FragmentAccountBinding;
import com.example.socinetandroid.dialog.ChangeEmailBottomSheet;
import com.example.socinetandroid.dialog.ChangePasswordBottomSheet;
import com.example.socinetandroid.dialog.LoginInfoBottomSheet;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.Account;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.repository.AccountRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.FileSupporter;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.utils.TokenManager;
import com.example.socinetandroid.viewmodel.AppViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {
    public static final String TAG = "ACCOUNT";
    private FragmentAccountBinding bd;
    private AppViewModel appViewModel;
    private AccountRepository accountRepository;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bd = FragmentAccountBinding.inflate(inflater, container, false);
        init();
        setEvent();
        return bd.getRoot();
    }

    private void init(){
        appViewModel = ((MyApplication) requireActivity().getApplication()).getAppViewModel();
        FileSupporter.loadImage(bd.ivAvatar, appViewModel.getUser().getAvatarUrl());
        bd.btnChangeEmail.setText(appViewModel.getAccount().getEmail());
    }

    private void setEvent(){
        bd.btnLogout.setOnClickListener(v -> {
            new TokenManager(getContext()).clearSharedPref();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        bd.layoutBtnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            intent.putExtra(ProfileActivity.USER_ID, appViewModel.getUser().getId());
            startActivity(intent);
        });

        bd.btnChangeEmail.setOnClickListener(v -> {
            ChangeEmailBottomSheet dialog = new ChangeEmailBottomSheet();
            dialog.show(getParentFragmentManager(), dialog.getTag());
        });

        bd.btnChangePassword.setOnClickListener(v -> {
            ChangePasswordBottomSheet dialog = new ChangePasswordBottomSheet();
            dialog.show(getParentFragmentManager(), dialog.getTag());
        });

        bd.btnLoginSession.setOnClickListener(v -> {
            LoginInfoBottomSheet dialog = new LoginInfoBottomSheet();
            dialog.show(getParentFragmentManager(), dialog.getTag());
        });
    }
}