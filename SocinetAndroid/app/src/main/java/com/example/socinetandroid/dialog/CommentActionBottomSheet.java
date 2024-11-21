package com.example.socinetandroid.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.databinding.BottomSheetCommentActionBinding;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Comment;
import com.example.socinetandroid.repository.CommentRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.viewmodel.AppViewModel;
import com.example.socinetandroid.viewmodel.CommentViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActionBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetCommentActionBinding bd;
    private final Comment mComment;
    private final boolean isCanReply;
    private CommentViewModel commentViewModel;
    private int confirmCount = 0;

    public CommentActionBottomSheet(Comment comment, boolean isCanReply){
        this.mComment = comment;
        this.isCanReply = isCanReply;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        init();
        bottomSheetDialog.setContentView(bd.getRoot());
        Helper.setupBottomSheetDialog(bottomSheetDialog, bd.getRoot(), false, -1);
        setEvent();
        return bottomSheetDialog;
    }

    private void init(){
        bd = BottomSheetCommentActionBinding.inflate(getLayoutInflater());
        commentViewModel = new ViewModelProvider(requireActivity()).get(CommentViewModel.class);
    }

    private void setEvent(){
        if(isCanReply){
            bd.tvResponse.setOnClickListener(v -> {
                commentViewModel.replyingComment(mComment);
                dismiss();
            });
        } else {
            bd.tvResponse.setVisibility(View.GONE);
        }
        AppViewModel appViewModel = ((MyApplication) requireActivity().getApplication()).getAppViewModel();
        if(appViewModel.getUser().getId() == mComment.getUser().getId()){
            bd.tvUpdate.setVisibility(View.VISIBLE);
            bd.tvDelete.setVisibility(View.VISIBLE);
            bd.tvUpdate.setOnClickListener(v -> {
                commentViewModel.updatingComment(mComment);
                dismiss();
            });

            bd.tvDelete.setOnClickListener(v -> {
                if(confirmCount == 0){
                    Toast.makeText(getContext(), "Nhấn thêm lần nữa để xóa bình luân", Toast.LENGTH_SHORT).show();
                }
                else if(confirmCount == 1){
                    CommentRepository commentRepository = RetrofitClient.createInstance(requireActivity()).create(CommentRepository.class);
                    commentRepository.deleteComment(mComment.getId()).enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                                @Override
                                public void onSuccess(ApiResponse result) {
                                    commentViewModel.deletedComment(mComment);
                                }

                                @Override
                                public void onFail(ApiResponse result) {
                                    Toast.makeText(getContext(), "Xóa bình luận thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            dismiss();
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                            Log.e("API", throwable.getMessage());
                        }
                    });
                }
                confirmCount++;
            });
        } else {
            bd.tvUpdate.setVisibility(View.GONE);
            bd.tvDelete.setVisibility(View.GONE);
        }
    }
}
