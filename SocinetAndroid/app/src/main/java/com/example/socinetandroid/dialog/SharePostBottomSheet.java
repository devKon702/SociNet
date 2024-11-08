package com.example.socinetandroid.dialog;

import android.app.Dialog;
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
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Post;
import com.example.socinetandroid.repository.PostRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
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

    public SharePostBottomSheet(){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_share_post, null);
        dialog.setContentView(view);
        Helper.setupBottomSheetDialog(dialog, view, true, R.id.layoutSharePostBottomSheet);
        init();
        setEvent(view);
        return dialog;
    }

    private void init(){
        this.postViewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);
        this.postRepository = RetrofitClient.createInstance(getContext()).create(PostRepository.class);

    }

    private void setEvent(View view){
        view.findViewById(R.id.btnShare).setOnClickListener(v -> {
            Post post = postViewModel.getCurrentPost();
            RequestBody captionRequest = RequestBody.create(MediaType.parse("multipart/form-data"), ((EditText) view.findViewById(R.id.edtCaption)).getText().toString());
            RequestBody sharedPostIdRequest = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(post.getId()));
            postRepository.createPost(captionRequest, null, sharedPostIdRequest).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            Toast.makeText(getContext(), "Chia sẻ thành công", Toast.LENGTH_SHORT).show();
                            Post newPost = Helper.convertDataToType(result.getData(), Helper.getType(Post.class));
                            postViewModel.addPost(newPost);
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            Toast.makeText(getContext(), "Chia sẻ thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();                        }
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
