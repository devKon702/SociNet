package com.example.socinetandroid.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.activity.ProfileActivity;
import com.example.socinetandroid.adapter.PostAdapter;
import com.example.socinetandroid.databinding.FragmentHomeBinding;
import com.example.socinetandroid.dialog.CommentBottomSheet;
import com.example.socinetandroid.dialog.CreatePostBottomSheet;
import com.example.socinetandroid.dialog.CreateReactionBottomSheet;
import com.example.socinetandroid.dialog.PostActionBottomSheet;
import com.example.socinetandroid.dialog.SharePostBottomSheet;
import com.example.socinetandroid.interfaces.IPostListener;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Post;
import com.example.socinetandroid.model.Reaction;
import com.example.socinetandroid.repository.PostRepository;
import com.example.socinetandroid.repository.ReactionRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.request.ReactionRequest;
import com.example.socinetandroid.utils.FileSupporter;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.viewmodel.AppViewModel;
import com.example.socinetandroid.viewmodel.PostViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements IPostListener{
    public static final String TAG = "HOME";
    private FragmentHomeBinding bd;
    private PostViewModel postViewModel;
    private AppViewModel appViewModel;
    private PostRepository postRepository;
    private ReactionRepository reactionRepository;
    private PostAdapter postAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        init();
        setEvent();
        return bd.getRoot();
    }


    private void init(){
        bd = FragmentHomeBinding.inflate(getLayoutInflater());
        bd.rcvPost.setLayoutManager(new LinearLayoutManager(getContext()));
        postViewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);
        appViewModel = ((MyApplication) requireActivity().getApplication()).getAppViewModel();
        postAdapter = new PostAdapter(getContext(), new ArrayList<>(), this);
        bd.rcvPost.setAdapter(postAdapter);
        postRepository = RetrofitClient.createInstance(getContext()).create(PostRepository.class);
        reactionRepository = RetrofitClient.createInstance(getContext()).create(ReactionRepository.class);
        FileSupporter.loadImage(bd.ivAvatarCreatePost, appViewModel.getUser().getAvatarUrl());
    }

    private void setEvent(){
        postViewModel.getState().observe(getViewLifecycleOwner(), value -> {
            switch(value){
                case PostViewModel.NORMAL: break;
                case PostViewModel.EDITED:
                    postAdapter.updateItem(postViewModel.getNewPost());
                    postViewModel.setNewPost(null);
                    postViewModel.setState(PostViewModel.NORMAL);
                    break;
                case PostViewModel.DELETED:
                    postAdapter.deleteItem(postViewModel.getNewPost());
                    postViewModel.setNewPost(null);
                    postViewModel.setState(PostViewModel.NORMAL);
                    break;
                case PostViewModel.CREATED:
                    postAdapter.addItem(0, postViewModel.getNewPost());
                    postViewModel.setNewPost(null);
                    postViewModel.setState(PostViewModel.NORMAL);
                    break;
                case PostViewModel.REACTED:
                    postAdapter.updateItemKeepMedia(postViewModel.getNewPost());
                    postViewModel.setNewPost(null);
                    postViewModel.setState(PostViewModel.NORMAL);
            }
        });
        postRepository.getPosts().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        List<Post> postList = Helper.convertDataToType(result.getData(), Helper.getListType(Post.class));
                        postAdapter.setList(postList);
                    }

                    @Override
                    public void onFail(ApiResponse result) {
                        Toast.makeText(getContext(), "Lấy danh sách bài viết thất bại\n"+result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.e("API", throwable.getMessage());
            }
        });
        bd.layoutCreatePost.setOnClickListener(v -> {
            CreatePostBottomSheet dialog = new CreatePostBottomSheet();
            dialog.show(getParentFragmentManager(), dialog.getTag());
        });
    }

    @Override
    public void onActionClick(RecyclerView.ViewHolder holder, Post post) {
        postViewModel.setCurrentPost(post);
        PostActionBottomSheet dialog = new PostActionBottomSheet();
        dialog.show(getParentFragmentManager(), dialog.getTag());
    }

    @Override
    public void onReactClick(RecyclerView.ViewHolder holder, Post post) {
        if(post.getSelfReaction() == null){
            post.setSelfReaction(Reaction.LIKE_TYPE);
            postAdapter.updateItemKeepMedia(post);
            reactionRepository.createReaction(new ReactionRequest(post.getId(), Reaction.LIKE_TYPE)).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            Toast.makeText(getContext(), "Tương tác bài viết thành công", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            Toast.makeText(getContext(), "Tương tác thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                            post.setSelfReaction(null);
                            postAdapter.updateItemKeepMedia(post);
                        }
                    });
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                    Log.e("API", throwable.getMessage());
                    Toast.makeText(getContext(), "Reaction Error", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            String lastReaction = post.getSelfReaction();
            post.setSelfReaction(null);
            postAdapter.updateItemKeepMedia(post);
            reactionRepository.deleteReaction(post.getId()).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            Toast.makeText(getContext(), "Gỡ tương tác thành công", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            Toast.makeText(getContext(), "Gỡ tương tác thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                            post.setSelfReaction(lastReaction);
                            postAdapter.updateItemKeepMedia(post);
                        }
                    });
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                    Log.e("API", throwable.getMessage());
                    Toast.makeText(getContext(), "Reaction Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onReactLongClick(RecyclerView.ViewHolder holder, Post post) {
        CreateReactionBottomSheet dialog = new CreateReactionBottomSheet(post);
        dialog.show(getParentFragmentManager(), dialog.getTag());
        return true;
    }

    @Override
    public void onCommentClick(RecyclerView.ViewHolder holder, Post post) {
        CommentBottomSheet dialog = new CommentBottomSheet(post.getId());
        dialog.show(requireActivity().getSupportFragmentManager(), dialog.getTag());
    }

    @Override
    public void onShareClick(RecyclerView.ViewHolder holder, Post post) {
        SharePostBottomSheet dialog = new SharePostBottomSheet(post);
        dialog.show(requireActivity().getSupportFragmentManager(), dialog.getTag());
    }

    @Override
    public void onUserClick(Post post) {
        Intent intent = new Intent(getContext(), ProfileActivity.class);
        intent.putExtra(ProfileActivity.USER_ID, post.getUser().getId());
        startActivity(intent);
    }
}