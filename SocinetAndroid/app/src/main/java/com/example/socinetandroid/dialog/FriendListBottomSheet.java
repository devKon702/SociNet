package com.example.socinetandroid.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.socinetandroid.R;
import com.example.socinetandroid.activity.ProfileActivity;
import com.example.socinetandroid.adapter.UserAdapter;
import com.example.socinetandroid.databinding.BottomSheetUserListBinding;
import com.example.socinetandroid.interfaces.IUserListener;
import com.example.socinetandroid.model.User;
import com.example.socinetandroid.utils.Helper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class FriendListBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetUserListBinding bd;
    private List<User> mUserList;
    private UserAdapter userAdapter;

    public FriendListBottomSheet(List<User> list){
        this.mUserList = list;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bd = BottomSheetUserListBinding.inflate(getLayoutInflater());
        return bd.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setEvent();
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        Helper.setupBottomSheetDialog(dialog, bd.getRoot(), true, R.id.layoutFriendList);
    }
    private void init() {
        userAdapter = new UserAdapter(mUserList, UserAdapter.HORIZONTAL_ITEM, new IUserListener() {
            @Override
            public void onItemClick(User user) {
                dismiss();
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });
        bd.rcvUser.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        bd.rcvUser.setAdapter(userAdapter);
    }

    private void setEvent() {
    }
}
