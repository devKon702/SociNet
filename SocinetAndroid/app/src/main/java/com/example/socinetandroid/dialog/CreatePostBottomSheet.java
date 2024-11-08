package com.example.socinetandroid.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.socinetandroid.R;
import com.example.socinetandroid.databinding.BottomSheetCreatePostBinding;
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
import com.google.android.material.button.MaterialButton;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostBottomSheet extends BottomSheetDialogFragment {
    private PostViewModel postViewModel;
    private PostRepository postRepository;
    private BottomSheetCreatePostBinding bd;
    private Uri fileUri;

    private final ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    assert result.getData() != null;
                    Uri uri = result.getData().getData();
                    if(uri != null) {
                        String mimeType = requireActivity().getContentResolver().getType(uri);
                        if(mimeType != null){
                            if(mimeType.startsWith("image/")){
                                showImage(uri);
                            } else if(mimeType.startsWith("video/")){
                                showVideo(uri);
                            }
                        }
                    }
                }
            }
    );


    public CreatePostBottomSheet(){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        init();
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_create_post, null);
        dialog.setContentView(bd.getRoot());
        Helper.setupBottomSheetDialog(dialog, bd.getRoot(), true, R.id.layoutCreatePostBottomSheet);

        setEvent();
        return dialog;
    }

    private void init(){
        postViewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);
        postRepository = RetrofitClient.createInstance(getContext()).create(PostRepository.class);
        bd = BottomSheetCreatePostBinding.inflate(getLayoutInflater());
    }

    private void setEvent(){
        bd.btnCreate.setOnClickListener(v -> {
            String caption = bd.edtCaption.getText().toString();
            if(fileUri == null && caption.isEmpty()){
                Toast.makeText(getContext(), "Không thể tạo bài viết trống", Toast.LENGTH_SHORT).show();
                return;
            }
            PostRequest postRequest = new PostRequest(requireActivity(), fileUri, caption, -1);
            postRepository.createPost(postRequest.getCaptionPart(), postRequest.getFilePart(), postRequest.getSharedPostIdPart()).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            Toast.makeText(getContext(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                            Post post = Helper.convertDataToType(result.getData(), Helper.getType(Post.class));
                            postViewModel.addPost(post);
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            Toast.makeText(getContext(), "Tạo bài viết thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    dismiss();
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                    Log.e("API", throwable.getMessage());
                }
            });
        });
    }

    private void showImage(Uri uri){
        fileUri = uri;
        bd.ivImage.setImageURI(uri);
        bd.ivImage.setVisibility(View.VISIBLE);
        bd.cvRemoveFile.setVisibility(View.VISIBLE);
    }

    private void showVideo(Uri uri){
        fileUri = uri;
        ExoPlayer player = new ExoPlayer.Builder(requireActivity()).build();
        bd.playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(uri);
        player.setMediaItem(mediaItem);
        player.prepare();
        bd.playerView.setVisibility(View.VISIBLE);
        bd.cvRemoveFile.setVisibility(View.VISIBLE);
    }
}
