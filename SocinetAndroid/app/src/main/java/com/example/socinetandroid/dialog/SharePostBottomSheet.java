package com.example.socinetandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.socinetandroid.R;
import com.example.socinetandroid.databinding.BottomSheetSharePostBinding;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Post;
import com.example.socinetandroid.repository.PostRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.request.PostRequest;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.viewmodel.PostViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SharePostBottomSheet extends BottomSheetDialogFragment {
    private PostViewModel postViewModel;
    private PostRepository postRepository;
    private BottomSheetSharePostBinding bd;
    private Post currentPost;

    public SharePostBottomSheet(Post post){
        this.currentPost = post;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        init();
        dialog.setContentView(bd.getRoot());
        Helper.setupBottomSheetDialog(dialog, bd.getRoot(), false, -1);
        setEvent();
        return dialog;
    }

    private void init(){
        this.postViewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);
        this.postRepository = RetrofitClient.createInstance(getContext()).create(PostRepository.class);
        this.bd = BottomSheetSharePostBinding.inflate(getLayoutInflater());
    }

    private void setEvent(){
        bd.btnShare.setOnClickListener(v -> {
            Context context = getContext();
            PostRequest postRequest = new PostRequest(requireActivity(), null, bd.edtCaption.getText().toString(), currentPost.getId());
            postRepository.createPost(postRequest.getCaptionPart(), null, postRequest.getSharedPostIdPart()).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            Toast.makeText(context, "Chia sẻ thành công", Toast.LENGTH_SHORT).show();
                            Post newPost = Helper.convertDataToType(result.getData(), Helper.getType(Post.class));
                            postViewModel.addPost(newPost);
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            Toast.makeText(context, "Chia sẻ thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();                        }
                    });
                    postViewModel.setCurrentPost(null);
                    dismiss();
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                    Log.e("API", throwable.getMessage());
                }
            });
        });
    }


}
