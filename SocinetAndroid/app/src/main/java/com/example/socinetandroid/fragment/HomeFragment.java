package com.example.socinetandroid.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socinetandroid.R;
import com.example.socinetandroid.adapter.PostAdapter;
import com.example.socinetandroid.databinding.FragmentHomeBinding;
import com.example.socinetandroid.interfaces.IPostListener;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Post;
import com.example.socinetandroid.service.AuthService;
import com.example.socinetandroid.service.PostService;
import com.example.socinetandroid.service.config.RetrofitClient;
import com.example.socinetandroid.utils.Helper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment{
    public static final String TAG = "HOME";
    private FragmentHomeBinding bd;

    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
        init();
        setEvent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false);
        return bd.getRoot();
    }


    private void init(){
        bd = FragmentHomeBinding.inflate(getLayoutInflater());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        bd.rcvPost.setLayoutManager(linearLayoutManager);
    }

    private void setEvent(){
        PostService postService = null;
        postService = RetrofitClient.createInstance(getContext()).create(PostService.class);

        postService.getPosts().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        List<Post> postList = Helper.convertDataToType(result.getData(), Helper.getListType(Post.class));
                        setAdapterData(postList);
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

    private void setAdapterData(List<Post> list){
        PostAdapter adapter = new PostAdapter(list, new IPostListener() {
            @Override
            public void onItemClick() {
                Toast.makeText(getContext(), "Item click", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReactClick() {
                Toast.makeText(getContext(), "React click", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCommentClick() {
                Toast.makeText(getContext(), "Comment click", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onShareClick() {
                Toast.makeText(getContext(), "Share click", Toast.LENGTH_SHORT).show();
            }
        });
        bd.rcvPost.setAdapter(adapter);
    }
}