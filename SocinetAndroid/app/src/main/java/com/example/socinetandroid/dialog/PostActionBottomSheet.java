package com.example.socinetandroid.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.socinetandroid.R;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.repository.PostRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.viewmodel.PostViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActionBottomSheet extends BottomSheetDialogFragment {
    private int confirmCount = 0;
    private PostViewModel postViewModel;

    public PostActionBottomSheet(){
//        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_post_action, null);
        dialog.setContentView(dialogView);
        Helper.setupBottomSheetDialog(dialog, dialogView, false, -1);
        init();
        setEvent(dialogView);
        return dialog;
    }

    private void init(){
        postViewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);
    }

    private void setEvent(View view){
        view.findViewById(R.id.tvEdit).setOnClickListener(v -> {
            EditPostBottomSheet dialog = new EditPostBottomSheet();
            dialog.show(getParentFragmentManager(), dialog.getTag());
            dismiss();
        });
        view.findViewById(R.id.tvDelete).setOnClickListener(v -> {
            if(confirmCount == 0){
                Toast.makeText(getContext(), "Nhấn thêm lần nữa để xóa", Toast.LENGTH_SHORT).show();
            } else if(confirmCount == 1){
                PostRepository postRepository = RetrofitClient.createInstance(getContext()).create(PostRepository.class);
                postRepository.deletePost(postViewModel.getCurrentPost().getId()).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                            @Override
                            public void onSuccess(ApiResponse result) {
                                Toast.makeText(getContext(), "Gỡ bài viết thành công", Toast.LENGTH_SHORT).show();
                                postViewModel.deletePost(postViewModel.getCurrentPost());
                                dismiss();
                            }

                            @Override
                            public void onFail(ApiResponse result) {
                                Toast.makeText(getContext(), "Gỡ bài viết thành công\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                        Log.e("API", throwable.getMessage());
                    }
                });
            }
            confirmCount++;
        });
    }

}
