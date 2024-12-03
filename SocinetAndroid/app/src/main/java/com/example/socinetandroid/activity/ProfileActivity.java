package com.example.socinetandroid.activity;

import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.R;
import com.example.socinetandroid.adapter.UserAdapter;
import com.example.socinetandroid.adapter.PostAdapter;
import com.example.socinetandroid.databinding.ActivityProfileBinding;
import com.example.socinetandroid.dialog.CommentBottomSheet;
import com.example.socinetandroid.dialog.CreateReactionBottomSheet;
import com.example.socinetandroid.dialog.FriendListBottomSheet;
import com.example.socinetandroid.dialog.PostActionBottomSheet;
import com.example.socinetandroid.dialog.SharePostBottomSheet;
import com.example.socinetandroid.dialog.UpdateProfileBottomSheet;
import com.example.socinetandroid.interfaces.IPostListener;
import com.example.socinetandroid.interfaces.IRefreshTokenHandler;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.interfaces.IUserListener;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Invitation;
import com.example.socinetandroid.model.Post;
import com.example.socinetandroid.model.Reaction;
import com.example.socinetandroid.model.User;
import com.example.socinetandroid.repository.FriendRepository;
import com.example.socinetandroid.repository.PostRepository;
import com.example.socinetandroid.repository.ReactionRepository;
import com.example.socinetandroid.repository.UserRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.request.ReactionRequest;
import com.example.socinetandroid.utils.FileSupporter;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.utils.SocketManager;
import com.example.socinetandroid.utils.TokenManager;
import com.example.socinetandroid.viewmodel.AppViewModel;
import com.example.socinetandroid.viewmodel.PostViewModel;
import com.example.socinetandroid.viewmodel.ProfileViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements IPostListener  {
    public static final String USER_ID = "UserId";

    private ActivityProfileBinding bd;
    private FriendRepository friendRepository;
    private PostRepository postRepository;
    private UserRepository userRepository;
    private long currentUserId;
    private String friendStatus;
    private List<User> friendList;
    private PostAdapter postAdapter;
    private UserAdapter userAdapter;
    private AppViewModel appViewModel;
    private SocketManager socketManager;
    private PostViewModel postViewModel;
    private ReactionRepository reactionRepository;
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        new TokenManager(this).callRefreshToken(new IRefreshTokenHandler() {
            @Override
            public void handleSuccess() {
                bindData();
                setEvent();
            }

            @Override
            public void handleFail() {
                Log.e("API", "Call Refresh token fail");
            }
        });
        setContentView(bd.getRoot());
    }

    private void init(){
        this.currentUserId = getIntent().getLongExtra(USER_ID,-1);
        if(currentUserId == -1){
            Log.e("ERROR", "Empty UserId Intent");
            finish();
        }
        bd = ActivityProfileBinding.inflate(getLayoutInflater());

        userRepository = RetrofitClient.createInstance(this).create(UserRepository.class);
        friendRepository = RetrofitClient.getInstance().create(FriendRepository.class);
        postRepository = RetrofitClient.getInstance().create(PostRepository.class);
        reactionRepository = RetrofitClient.getInstance().create(ReactionRepository.class);

        postAdapter = new PostAdapter(this, new ArrayList<>(), this);
        bd.rcvPost.setAdapter(postAdapter);
        bd.rcvPost.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new UserAdapter(new ArrayList<>(), UserAdapter.VERTICAL_ITEM, new IUserListener() {
            @Override
            public void onItemClick(User user) {
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(ProfileActivity.USER_ID, user.getId());
                startActivity(intent);
            }
        });

        bd.rcvFriend.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        bd.rcvFriend.setAdapter(userAdapter);

        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        appViewModel = ((MyApplication) getApplication()).getAppViewModel();
        socketManager = SocketManager.getInstance(getApplication());
        profileViewModel.getLiveUser().setValue(appViewModel.getUser());
    }

    private void bindData(){
        // Lấy thông tin người dùng
        userRepository.getUserById(currentUserId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        User user = Helper.convertDataToType(result.getData(), Helper.getType(User.class));
                        FileSupporter.loadImage(bd.ivAvatar, user.getAvatarUrl());
                        bd.tvName.setText(user.getName());
                        bd.tvFrom.setText("Dến từ " + user.getAddress());
                        bd.tvSchool.setText("Học tại " + user.getSchool());
                        bd.tvGenre.setText("Giới tính " + (user.isMale() ? "Nam" : "Nữ"));
                        bd.tvPhone.setText(user.getPhone());
                    }

                    @Override
                    public void onFail(ApiResponse result) {
                        Toast.makeText(ProfileActivity.this, "Lấy thông tin thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.e("API", throwable.getMessage());
            }
        });
        // Lấy ds bạn bè
        friendRepository.getFriendListByUser(currentUserId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        List<User> userList = Helper.convertDataToType(result.getData(), Helper.getListType(User.class));
                        friendList = userList;
                        bd.tvFriendNumber.setText(userList.size() + " bạn bè");
                        bd.tvFriendBottomNumber.setText(String.valueOf(userList.size()));
                        userAdapter.setList(userList.size() >= 4 ? userList.subList(0, 4) : userList);
                    }

                    @Override
                    public void onFail(ApiResponse result) {
                        Toast.makeText(ProfileActivity.this, "Lấy danh sách bạn bè thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.e("API", throwable.getMessage());
            }
        });
        // Nếu ko phải me, kiểm tra user hiện tại có phải bạn bè không
        if(currentUserId != appViewModel.getUser().getId()){
            friendRepository.checkIsFriend(currentUserId).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            friendStatus = Helper.convertDataToType(result.getData(), Helper.getType(String.class));
                            setFriendStatusEvent();
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            Toast.makeText(ProfileActivity.this, "Kiểm tra tình trạng bạn bè thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                            bd.btnFriend.setVisibility(GONE);
                        }
                    });
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                    Log.e("API", throwable.getMessage());
                }
            });
        } else setFriendStatusEvent();
        // Lấy ds bài viết
        postRepository.getPostOfUser(currentUserId).enqueue(new Callback<ApiResponse>() {
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
                        Toast.makeText(ProfileActivity.this, "Lấy danh sách bài viết thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.e("API", throwable.getMessage());
            }
        });
    }

    private void setEvent(){
        postViewModel.getState().observe(this, value -> {
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

        if(currentUserId == appViewModel.getUser().getId()){
            profileViewModel.getLiveUser().observe(this, value -> {
                bd.tvName.setText(value.getName());
                bd.tvSchool.setText(value.getSchool());
                bd.tvFrom.setText(value.getAddress());
                bd.tvPhone.setText(value.getPhone());
                bd.tvGenre.setText(value.isMale() ? "Nam" : "Nữ");
                FileSupporter.loadImage(bd.ivAvatar, value.getAvatarUrl());
            });
        }

        bd.ivBack.setOnClickListener(v -> {
//            Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
//            intent.putExtra(USER_ID, currentUserId);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
            finish();
        });

        bd.btnShowAllFriend.setOnClickListener(v -> {
            FriendListBottomSheet dialog = new FriendListBottomSheet(friendList);
            dialog.show(getSupportFragmentManager(), dialog.getTag());
        });

        bd.btnUpdateProfile.setOnClickListener(v -> {
            UpdateProfileBottomSheet dialog = new UpdateProfileBottomSheet();
            dialog.show(getSupportFragmentManager(), dialog.getTag());
        });
    }

    private void setFriendStatusEvent(){
        if(currentUserId == appViewModel.getUser().getId()){
            bd.btnUpdateProfile.setVisibility(View.VISIBLE);
            bd.btnFriend.setVisibility(View.GONE);
        } else {
            bd.btnUpdateProfile.setVisibility(View.GONE);
            bd.btnFriend.setVisibility(View.VISIBLE);
            if(friendStatus.equals(Invitation.STATUS_FRIEND)){
                bd.btnFriend.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue));
                bd.btnFriend.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_check_friend));
                bd.btnFriend.setText("Bạn bè");
            }
            else if(friendStatus.equals(Invitation.STATUS_INVITED)){
                bd.btnFriend.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.darkGray));
                bd.btnFriend.setIcon(null);
                bd.btnFriend.setText("Đã gửi lời mời");
            }
            else if(friendStatus.equals(Invitation.STATUS_STRANGE)){
                bd.btnFriend.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue));
                bd.btnFriend.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_add_friend));
                bd.btnFriend.setText("Gửi lời mời kết bạn");
                bd.btnFriend.setOnClickListener(v -> {
                    bd.btnFriend.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.darkGray));
                    bd.btnFriend.setIcon(null);
                    bd.btnFriend.setText("Đã gửi lời mời");
                    friendRepository.createFriendInvitation(currentUserId).enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                                @Override
                                public void onSuccess(ApiResponse result) {
                                    Invitation invitation = Helper.convertDataToType(result.getData(), Helper.getType(Invitation.class));
                                    socketManager.newInvitation(invitation);
                                    Toast.makeText(ProfileActivity.this, "Đã gửi lời mời thành công", Toast.LENGTH_SHORT).show();
                                    bd.btnFriend.setOnClickListener(null);
                                }

                                @Override
                                public void onFail(ApiResponse result) {
                                    if(result.getMessage().equals("INVITATION EXISTED")) {
                                        bd.btnFriend.setOnClickListener(null);
                                        return;
                                    };
                                    Toast.makeText(ProfileActivity.this, "Gửi lời mời thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                                    bd.btnFriend.setBackgroundTintList(ContextCompat.getColorStateList(ProfileActivity.this, R.color.blue));
                                    bd.btnFriend.setIcon(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.ic_add_friend));
                                    bd.btnFriend.setText("Gửi lời mời kết bạn");
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                            Log.e("API", throwable.getMessage());
                        }
                    });
                });
            }
        }
    }

    @Override
    public void onActionClick(RecyclerView.ViewHolder holder, Post post) {
        postViewModel.setCurrentPost(post);
        PostActionBottomSheet dialog = new PostActionBottomSheet();
        dialog.show(getSupportFragmentManager(), dialog.getTag());
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
                            Toast.makeText(ProfileActivity.this, "Tương tác bài viết thành công", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            Toast.makeText(ProfileActivity.this, "Tương tác thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                            post.setSelfReaction(null);
                            postAdapter.updateItemKeepMedia(post);
                        }
                    });
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                    Log.e("API", throwable.getMessage());
                    Toast.makeText(ProfileActivity.this, "Reaction Error", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ProfileActivity.this, "Gỡ tương tác thành công", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail(ApiResponse result) {
                            Toast.makeText(ProfileActivity.this, "Gỡ tương tác thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                            post.setSelfReaction(lastReaction);
                            postAdapter.updateItemKeepMedia(post);
                        }
                    });
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                    Log.e("API", throwable.getMessage());
                    Toast.makeText(ProfileActivity.this, "Reaction Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    @Override
    public boolean onReactLongClick(RecyclerView.ViewHolder holder, Post post) {
        CreateReactionBottomSheet dialog = new CreateReactionBottomSheet(post);
        dialog.show(getSupportFragmentManager(), dialog.getTag());
        return true;
    }
    @Override
    public void onCommentClick(RecyclerView.ViewHolder holder, Post post) {
        CommentBottomSheet dialog = new CommentBottomSheet(post.getId());
        dialog.show(getSupportFragmentManager(), dialog.getTag());
    }
    @Override
    public void onShareClick(RecyclerView.ViewHolder holder, Post post) {
        SharePostBottomSheet dialog = new SharePostBottomSheet(post);
        dialog.show(getSupportFragmentManager(), dialog.getTag());
    }

    @Override
    public void onUserClick(Post post) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(USER_ID, post.getUser().getId());
        startActivity(intent);
    }
}