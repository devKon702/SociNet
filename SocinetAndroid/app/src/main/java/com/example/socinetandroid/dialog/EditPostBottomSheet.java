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
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.socinetandroid.R;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Post;
import com.example.socinetandroid.repository.PostRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.FileSupporter;
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

public class EditPostBottomSheet extends BottomSheetDialogFragment {
    private PostViewModel postViewModel;
    private EditText edtCaption;
    private ImageView ivImage;
    private PlayerView playerView;
    private TextView tvChooseFile;
    private MaterialButton btnEdit;
    private CardView cvRemoveFile;
    private Uri mUri = null;

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

    public EditPostBottomSheet(){}

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_edit_post, null);
        dialog.setContentView(dialogView);
        Helper.setupBottomSheetDialog(dialog, dialogView, true, R.id.layoutEditPostBottomSheet);
        init(dialogView);
        setEvent();
        return dialog;
    }

    private void init(View view){
        postViewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);
        Post mPost = postViewModel.getCurrentPost();
        edtCaption = view.findViewById(R.id.edtCaption);
        ivImage = view.findViewById(R.id.ivImage);
        playerView = view.findViewById(R.id.playerView);
        tvChooseFile = view.findViewById(R.id.tvChooseFile);
        btnEdit = view.findViewById(R.id.btnEdit);
        cvRemoveFile = view.findViewById(R.id.cvRemoveFile);

        edtCaption.setText(mPost.getCaption());
        if(FileSupporter.isImageUrl(mPost.getImageUrl())) {
            FileSupporter.loadImage(ivImage, mPost.getImageUrl());
            ivImage.setVisibility(View.VISIBLE);
        }
        else if(FileSupporter.isVideoUrl(mPost.getImageUrl())){
            FileSupporter.loadVideo(getContext(), playerView, mPost.getImageUrl());
            playerView.setVisibility(View.VISIBLE);
        }
    }

    private void setEvent(){
        cvRemoveFile.setOnClickListener(v -> {
            ivImage.setVisibility(View.GONE);
            playerView.setVisibility(View.GONE);
            cvRemoveFile.setVisibility(View.GONE);
            this.mUri = null;
        });

        tvChooseFile.setOnClickListener(v -> {
//            if(!FileSupporter.isReadExternalStorageAllowed(requireActivity())){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/* video/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
                mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Image"));
//            } else{
//                FileSupporter.requestStoragePermission(requireActivity());
//            }
        });

        btnEdit.setOnClickListener(v -> {
            if (mUri != null && FileSupporter.getFileSizeFromUri(requireActivity(), mUri) > 5L * 1024 * 1024){
                Toast.makeText(requireActivity(), "File quá lớn", Toast.LENGTH_SHORT).show();
                return;
            }
            MultipartBody.Part fileRequest = null;
            if(mUri != null){
                File file = FileSupporter.createFileFromUri(getContext(), mUri); // Convert Uri to File
                String mimeType = requireActivity().getContentResolver().getType(mUri);
                assert mimeType != null;
                RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
                fileRequest = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            }
            RequestBody captionRequest = RequestBody.create(MediaType.parse("multipart/form-data"), edtCaption.getText().toString());
            RetrofitClient.createInstance(requireActivity()).create(PostRepository.class).updatePost(postViewModel.getCurrentPost().getId(), fileRequest, captionRequest).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            Post post = Helper.convertDataToType(result.getData(), Helper.getType(Post.class));
                            postViewModel.editPost(post);
                            Toast.makeText(getContext(), "Edit Post Success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            Toast.makeText(getContext(), "Edit Post Fail:\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
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

    private void showImage(Uri uri){
        this.mUri = uri;
        ivImage.setImageURI(uri);
        ivImage.setVisibility(View.VISIBLE);
        cvRemoveFile.setVisibility(View.VISIBLE);
    }

    private void showVideo(Uri uri){
        this.mUri = uri;
        ExoPlayer player = new ExoPlayer.Builder(requireActivity()).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(uri);
        player.setMediaItem(mediaItem);
        player.prepare();
        playerView.setVisibility(View.VISIBLE);
        cvRemoveFile.setVisibility(View.VISIBLE);
    }
}
