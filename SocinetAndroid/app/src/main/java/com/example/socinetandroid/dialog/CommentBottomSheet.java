package com.example.socinetandroid.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.socinetandroid.R;
import com.example.socinetandroid.adapter.CommentAdapter;
import com.example.socinetandroid.databinding.BottomSheetCommentBinding;
import com.example.socinetandroid.interfaces.ICommentListener;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Comment;
import com.example.socinetandroid.repository.CommentRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.request.CommentRequest;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.viewmodel.CommentViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentBottomSheet extends BottomSheetDialogFragment implements ICommentListener{
    private BottomSheetCommentBinding bd;
    private CommentRepository commentRepository;
    private Long mPostId;
    private CommentAdapter commentAdapter;
    private CommentViewModel commentViewModel;
    public CommentBottomSheet(Long postId){
        this.mPostId = postId;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        init();
        return bd.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEvent();
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) getDialog();
        Helper.setupBottomSheetDialog(bottomSheetDialog, bd.getRoot(), true, R.id.layoutCommentBottomSheet);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//    }

    private void init(){
        bd = BottomSheetCommentBinding.inflate(getLayoutInflater());
        commentViewModel = new ViewModelProvider(requireActivity()).get(CommentViewModel.class);
        bd.rcvComment.setLayoutManager(new LinearLayoutManager(getContext()));
        commentAdapter = new CommentAdapter(getContext(), new ArrayList<>(), true, this);
        bd.rcvComment.setAdapter(commentAdapter);
        commentRepository = RetrofitClient.createInstance(getContext()).create(CommentRepository.class);
        commentRepository.getCommentsByPostId(mPostId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        List<Comment> list = Helper.convertDataToType(result.getData(), Helper.getListType(Comment.class));
                        commentAdapter.setList(list);
                    }

                    @Override
                    public void onFail(ApiResponse result) {
                        Toast.makeText(getContext(), "Lấy danh sách bình luận thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.e("API", throwable.getMessage());
            }
        });
    }
    @Override
    public boolean onLongClick(Comment comment, boolean isCanReply) {
        CommentActionBottomSheet dialog = new CommentActionBottomSheet(comment, isCanReply);
        dialog.show(getParentFragmentManager(), dialog.getTag());
        return true;
    }
    private void setEvent(){
        commentViewModel.getLiveState().observe(getViewLifecycleOwner(), value -> {
            switch(value){
                case CommentViewModel.NORMAL:
                    bd.layoutState.setVisibility(View.GONE);
                    bd.edtComment.setText("");
                    break;
                case CommentViewModel.NEW_COMMENT:
                    commentAdapter.addItem(commentViewModel.getNewComment());
                    commentViewModel.reset();
                    break;
                case CommentViewModel.UPDATING:
                    bd.tvCurrentState.setText("Đang chỉnh sửa");
                    bd.tvCurrentUserName.setText(commentViewModel.getCurrentComment().getUser().getName());
                    bd.tvCurrentContent.setText(commentViewModel.getCurrentComment().getContent());
                    bd.edtComment.setText(commentViewModel.getCurrentComment().getContent());
                    bd.layoutState.setVisibility(View.VISIBLE);
                    break;
                case CommentViewModel.CHANGED_COMMENT:
                    bd.layoutState.setVisibility(View.GONE);
                    commentAdapter.updateItem(commentViewModel.getNewComment());
                    commentViewModel.reset();
                    break;
                case CommentViewModel.REPLYING:
                    bd.tvCurrentState.setText("Đang trả lời");
                    bd.tvCurrentUserName.setText(commentViewModel.getCurrentComment().getUser().getName());
                    bd.tvCurrentContent.setText(commentViewModel.getCurrentComment().getContent());
                    bd.layoutState.setVisibility(View.VISIBLE);
                    break;
                case CommentViewModel.NEW_REPLY_COMMENT:
                    commentAdapter.replyItem(commentViewModel.getNewComment(), commentViewModel.getCurrentComment().getId());
                    commentViewModel.reset();
                    break;
                case CommentViewModel.REMOVED_COMMENT:
                    commentAdapter.removeItem(commentViewModel.getNewComment());
                    commentViewModel.reset();
                    break;
            }
        });

        bd.ivClose.setOnClickListener(v -> {
            String lastInput = bd.edtComment.getText().toString();
            commentViewModel.reset();
            bd.edtComment.setText(lastInput);
        });

        bd.btnSend.setOnClickListener(v -> {
            Integer state = commentViewModel.getLiveState().getValue();
            if(state == null) return;
            switch (state){
                case CommentViewModel.NORMAL:
                    createComment();
                    break;
                case CommentViewModel.UPDATING:
                    updateComment();
                    break;
                case CommentViewModel.REPLYING:
                    replyComment();
                    break;
            }
        });
    }
    private void createComment() {
        String content = bd.edtComment.getText().toString().trim();
        if(content.isEmpty()) return;

        CommentRequest commentRequest = new CommentRequest(mPostId, content, null);
        commentRepository.createComment(commentRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        Comment comment = Helper.convertDataToType(result.getData(), Helper.getType(Comment.class));
                        commentViewModel.createdComment(comment);
                    }

                    @Override
                    public void onFail(ApiResponse result) {
                        Toast.makeText(getContext(), "Bình luận thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.e("API", throwable.getMessage());
            }
        });
    }
    private void updateComment() {
        String content = bd.edtComment.getText().toString().trim();
        if(content.isEmpty()) return;
        long commentId = commentViewModel.getCurrentComment().getId();
        commentRepository.updateComment(commentId, content).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        Comment comment = Helper.convertDataToType(result.getData(), Helper.getType(Comment.class));
                        commentViewModel.updatedComment(comment);
                    }

                    @Override
                    public void onFail(ApiResponse result) {
                        Toast.makeText(getContext(), "Cập nhật bình luận thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.e("API", throwable.getMessage());
            }
        });
    }
    private void replyComment() {
        String content = bd.edtComment.getText().toString().trim();
        if(content.isEmpty()) return;
        long parentId = commentViewModel.getCurrentComment().getId();
        CommentRequest commentRequest = new CommentRequest(mPostId, content, parentId);
        commentRepository.createComment(commentRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        Comment comment = Helper.convertDataToType(result.getData(), Helper.getType(Comment.class));
                        commentViewModel.repliedComment(comment);
                    }

                    @Override
                    public void onFail(ApiResponse result) {
                        Toast.makeText(getContext(), "Trả lời bình luận thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.e("API", throwable.getMessage());
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        commentViewModel.reset();
    }
}
