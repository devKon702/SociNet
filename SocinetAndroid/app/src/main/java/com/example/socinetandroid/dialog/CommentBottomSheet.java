package com.example.socinetandroid.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socinetandroid.R;
import com.example.socinetandroid.adapter.CommentAdapter;
import com.example.socinetandroid.interfaces.ICommentListener;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Comment;
import com.example.socinetandroid.repository.CommentRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.Helper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentBottomSheet extends BottomSheetDialogFragment {
    private static final String KEY_BUNDLE_OBJECT = "post_id";
    private CommentRepository commentRepository;
    private Long mPostId;

    private RecyclerView rcvComment;
    private EditText edtComment;
    private ImageButton btnSend;

    public static CommentBottomSheet newInstance(Long postId){
        CommentBottomSheet dialog = new CommentBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_BUNDLE_OBJECT, postId);

        dialog.setArguments(bundle);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            mPostId = (Long) bundle.get(KEY_BUNDLE_OBJECT);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View viewDialog = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_comment, null);
        bottomSheetDialog.setContentView(viewDialog);
        Helper.setupBottomSheetDialog(bottomSheetDialog, viewDialog, true, R.id.layoutCommentBottomSheet);
        initView(viewDialog);
        return bottomSheetDialog;
    }

    private void initView(View view){
        edtComment = view.findViewById(R.id.edtComment);
        rcvComment = view.findViewById(R.id.rcvComment);
        btnSend = view.findViewById(R.id.btnSend);
        rcvComment.setLayoutManager(new LinearLayoutManager(getContext()));
        commentRepository = RetrofitClient.createInstance(getContext()).create(CommentRepository.class);
        commentRepository.getCommentsByPostId(mPostId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        List<Comment> list = Helper.convertDataToType(result.getData(), Helper.getListType(Comment.class));
                        setupRecyclerView(list);
                    }

                    @Override
                    public void onFail(ApiResponse result) {

                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {

            }
        });
    }
    private void setupRecyclerView(List<Comment> list){
        CommentAdapter adapter = new CommentAdapter(list, new ICommentListener() {
            @Override
            public void onLongClick(CommentAdapter.CommentViewHolder holder, Comment comment) {
                CommentActionBottomSheet dialog = new CommentActionBottomSheet(new CommentActionBottomSheet.IActionClick() {
                    @Override
                    public void onResponse() {
                        Toast.makeText(getContext(), "Response", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onUpdate() {
                        Toast.makeText(getContext(), "Update", Toast.LENGTH_SHORT).show();
                        edtComment.setText(comment.getContent());
                    }

                    @Override
                    public void onDelete() {
                        Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show(getParentFragmentManager(), dialog.getTag());
            }

            @Override
            public void onEdit(Comment comment) {

            }

            @Override
            public void onDelete(Comment comment) {

            }
        });
        rcvComment.setAdapter(adapter);
    }
}
