package com.example.socinetandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.socinetandroid.databinding.BottomSheetCreatePostBinding;
import com.example.socinetandroid.databinding.BottomSheetCreateReactionBinding;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Post;
import com.example.socinetandroid.model.Reaction;
import com.example.socinetandroid.repository.PostRepository;
import com.example.socinetandroid.repository.ReactionRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.request.ReactionRequest;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.viewmodel.PostViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateReactionBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetCreateReactionBinding bd;
    private PostViewModel postViewModel;
    private ReactionRepository reactionRepository;
    private Post currentPost;

    public CreateReactionBottomSheet(Post post){
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
        bd = BottomSheetCreateReactionBinding.inflate(getLayoutInflater());
        postViewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);
        reactionRepository = RetrofitClient.createInstance(getContext()).create(ReactionRepository.class);
    }

    private void setEvent(){
        bd.ivLike.setOnClickListener(v -> handleReact(Reaction.LIKE_TYPE) );
        bd.ivLove.setOnClickListener(v -> handleReact(Reaction.LOVE_TYPE));
        bd.ivHaha.setOnClickListener(v -> handleReact(Reaction.HAHA_TYPE));
        bd.ivSad.setOnClickListener(v -> handleReact(Reaction.SAD_TYPE));
    }

    private void handleReact(String type){
        if(type.equals(currentPost.getSelfReaction())){
            dismiss();
        } else{
            Context context = getContext();
            reactionRepository.createReaction(new ReactionRequest(currentPost.getId(), type)).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            Toast.makeText(context, "Tương tác thành công", Toast.LENGTH_SHORT).show();
                            Reaction reaction = Helper.convertDataToType(result.getData(), Helper.getType(Reaction.class));
                            postViewModel.reactPost(reaction.getPost());
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            Toast.makeText(context, "Tương tác thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                    Log.e("API", throwable.getMessage());
                }
            });
            dismiss();
        }
    }
}
