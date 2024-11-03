package com.example.socinetandroid.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socinetandroid.activity.LoginActivity;
import com.example.socinetandroid.activity.ProfileActivity;
import com.example.socinetandroid.databinding.FragmentAccountBinding;
import com.example.socinetandroid.utils.TokenManager;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class AccountFragment extends Fragment {
    public static final String TAG = "ACCOUNT";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentAccountBinding bd;
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bd = FragmentAccountBinding.inflate(inflater, container, false);
        setEvent();
        return bd.getRoot();
    }

    private void setEvent(){
        bd.btnLogout.setOnClickListener(v -> {
            new TokenManager(getContext()).clearSharedPref();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        bd.layoutBtnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ProfileActivity.class);
            startActivity(intent);
        });
    }
}