package com.example.socinetandroid.dialog;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socinetandroid.R;
import com.example.socinetandroid.utils.Helper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CommentBottomSheetDialog extends BottomSheetDialogFragment {
    public static CommentBottomSheetDialog newInstance(){
        CommentBottomSheetDialog dialog = new CommentBottomSheetDialog();
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
//        View viewDialog = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_comment, null);
//
//        bottomSheetDialog.setContentView(viewDialog);
//
//        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) viewDialog.getParent());
//        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        behavior.setSkipCollapsed(true);
//
//        LinearLayout layout = bottomSheetDialog.findViewById(R.id.layoutCommentBottomSheet);
//        assert  layout != null;
//        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
        bottomSheetDialog = Helper.setupBottomSheetDialog(bottomSheetDialog, getContext(), R.layout.bottom_sheet_comment, true, R.id.layoutCommentBottomSheet);

        return bottomSheetDialog;
    }
}
