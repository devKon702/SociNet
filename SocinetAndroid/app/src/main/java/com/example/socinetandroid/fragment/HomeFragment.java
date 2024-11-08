package com.example.socinetandroid.fragment;

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

import com.example.socinetandroid.adapter.PostAdapter;
import com.example.socinetandroid.databinding.FragmentHomeBinding;
import com.example.socinetandroid.dialog.CommentBottomSheet;
import com.example.socinetandroid.dialog.PostActionBottomSheet;
import com.example.socinetandroid.dialog.SharePostBottomSheet;
import com.example.socinetandroid.interfaces.IPostListener;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Post;
import com.example.socinetandroid.repository.PostRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.Helper;
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
    private PostRepository postRepository;
    private PostAdapter postAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
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
        postAdapter = new PostAdapter(getContext(), new ArrayList<>(), this);
        bd.rcvPost.setAdapter(postAdapter);
        postRepository = RetrofitClient.createInstance(getContext()).create(PostRepository.class);
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
    }

    @Override
    public void onActionClick(RecyclerView.ViewHolder holder, Post post) {
        postViewModel.setCurrentPost(post);
        PostActionBottomSheet dialog = new PostActionBottomSheet();
        dialog.show(getParentFragmentManager(), dialog.getTag());
    }

    @Override
    public void onReactClick(RecyclerView.ViewHolder holder, Post post) {
        if(post.getSelfReaction() != null){

        }
    }

    @Override
    public void onReactLongClick(RecyclerView.ViewHolder holder, Post post) {

    }

    @Override
    public void onCommentClick(RecyclerView.ViewHolder holder, Post post) {
        CommentBottomSheet dialog = CommentBottomSheet.newInstance(post.getId());
        dialog.show(requireActivity().getSupportFragmentManager(), dialog.getTag());
    }

    @Override
    public void onShareClick(RecyclerView.ViewHolder holder, Post post) {
        SharePostBottomSheet dialog = new SharePostBottomSheet();
        dialog.show(requireActivity().getSupportFragmentManager(), dialog.getTag());
    }
}