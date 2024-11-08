package com.example.socinetandroid.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socinetandroid.R;
import com.example.socinetandroid.utils.Helper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CommentActionBottomSheet extends BottomSheetDialogFragment {
    private static final String KEY_BUNDLE_COMMENT = "comment_bundle";
    private IActionClick actionClick;
    private TextView tvResponse, tvUpdate, tvDelete;

    public interface IActionClick{
        void onResponse();
        void onUpdate();
        void onDelete();
    }
    public CommentActionBottomSheet(IActionClick iActionClick){
        this.actionClick = iActionClick;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View viewDialog = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_comment_action, null);
        bottomSheetDialog.setContentView(viewDialog);
        Helper.setupBottomSheetDialog(bottomSheetDialog, viewDialog, false, -1);
        init(viewDialog);
        setEvent();
        return bottomSheetDialog;
    }

    private void init(View view){
        tvResponse = view.findViewById(R.id.tvResponse);
        tvUpdate = view.findViewById(R.id.tvUpdate);
        tvDelete = view.findViewById(R.id.tvDelete);
    }

    private void setEvent(){
        tvResponse.setOnClickListener(v -> {
            actionClick.onResponse();
            dismiss();
        });
        tvUpdate.setOnClickListener(v -> {
            actionClick.onUpdate();
            dismiss();
        });
        tvDelete.setOnClickListener(v -> {
            actionClick.onDelete();
            dismiss();
        });
    }
}
