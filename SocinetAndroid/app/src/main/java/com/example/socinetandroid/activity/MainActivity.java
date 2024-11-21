package com.example.socinetandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.R;
import com.example.socinetandroid.databinding.ActivityMainBinding;
import com.example.socinetandroid.enums.RealtimeStatus;
import com.example.socinetandroid.fragment.AccountFragment;
import com.example.socinetandroid.fragment.HomeFragment;
import com.example.socinetandroid.fragment.InvitationFragment;
import com.example.socinetandroid.interfaces.IRefreshTokenHandler;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.RealtimeChat;
import com.example.socinetandroid.model.RealtimeRoom;
import com.example.socinetandroid.model.RealtimeUser;
import com.example.socinetandroid.model.Room;
import com.example.socinetandroid.model.User;
import com.example.socinetandroid.repository.FriendRepository;
import com.example.socinetandroid.repository.RoomRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.utils.SocketManager;
import com.example.socinetandroid.utils.TokenManager;
import com.example.socinetandroid.viewmodel.AppViewModel;
import com.example.socinetandroid.viewmodel.CommentViewModel;
import com.example.socinetandroid.viewmodel.PostViewModel;
import com.google.android.material.badge.ExperimentalBadgeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SocketManager socketManager;
    private AppViewModel appViewModel;
    private RoomRepository roomRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        new TokenManager(this).callRefreshToken(new IRefreshTokenHandler() {
            @Override
            public void handleSuccess() {
                initial();
                setEvent();
            }

            @Override
            public void handleFail() {

            }
        });
        setContentView(binding.getRoot());
    }

    @OptIn(markerClass = ExperimentalBadgeUtils.class)
    private void initial(){
        socketManager = SocketManager.getInstance(getApplication());
        appViewModel = ((MyApplication) getApplication()).getAppViewModel();
        User user = appViewModel.getUser();
        if(user != null){
            binding.tvTest.setText(user.getName());
            socketManager.connect();
        } else {
            finish();
        }

        PostViewModel postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        CommentViewModel commentViewModel = new ViewModelProvider(this).get(CommentViewModel.class);
        binding.bottomNavigation.setBackground(null);
        binding.bottomNavigation.getMenu().getItem(3).setEnabled(false);
        switchFragment(new HomeFragment(), HomeFragment.TAG);

        FriendRepository friendRepository = RetrofitClient.createInstance(this).create(FriendRepository.class);
        friendRepository.getFriendListByUser(user.getId()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        List<User> list = Helper.convertDataToType(result.getData(), Helper.getListType(User.class));
                        List<RealtimeUser> realtimeUsers = new ArrayList<>();
                        for(User user : list){
//                            GlobalData.friendIdList.add(user.getId());
                            appViewModel.getLiveFriendByUserIdList().getValue().add(user.getId());
                            realtimeUsers.add(new RealtimeUser(user, RealtimeStatus.OFFLINE, "", false));
                        }
                        appViewModel.getLiveRealtimeUserList().setValue(realtimeUsers);
                        socketManager.emitFilterFriendStatus(appViewModel.getLiveFriendByUserIdList().getValue());
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

        roomRepository = RetrofitClient.getInstance().create(RoomRepository.class);
        roomRepository.getRoomOfMe().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        List<Room> roomList = Helper.convertDataToType(result.getData(), Helper.getListType(Room.class));
                        appViewModel.getLiveRealtimeRoomList().setValue(roomList.stream().map(room -> new RealtimeRoom(room, "", false)).collect(Collectors.toList()));
                        socketManager.emitFilterRoomStatus();
                        socketManager.joinMultiRoom(roomList.stream().map(Room::getId).collect(Collectors.toList()));
                    }

                    @Override
                    public void onFail(ApiResponse result) {
                        Toast.makeText(MainActivity.this, "Lấy danh sách nhóm thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
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
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.navigation_home){
                switchFragment(new HomeFragment(), HomeFragment.TAG);
            }
            else if(item.getItemId() == R.id.navigation_invitation){
                switchFragment(new InvitationFragment(), InvitationFragment.TAG);
            }
            else if(item.getItemId() == R.id.navigation_account){
                switchFragment(new AccountFragment(), AccountFragment.TAG);
            }
            return true;
        });

        binding.fab.setOnClickListener((v -> {
            Intent intent = new Intent(MainActivity.this, ChatMenuActivity.class);
            startActivity(intent);
        }));

//        binding.headerLogo.setOnClickListener(v -> startService(new Intent(this, SampleService.class)));
    }


    private void switchFragment(Fragment fragment, String tag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Hide all fragments
        fragmentManager.getFragments().forEach(fragmentTransaction::hide);

        Fragment existingFragment = fragmentManager.findFragmentByTag(tag);
        if(existingFragment != null){
            // active if exist
            fragmentTransaction.show(existingFragment);
        } else{
            // add new
            fragmentTransaction.add(R.id.frameLayout, fragment, tag);
        }
        fragmentTransaction.commit();
    }

    private void setBadgeNumber(int count) {
        if (count > 0) {
            binding.tvBadge.setVisibility(View.VISIBLE);
            binding.tvBadge.setText(count > 99 ? "99+" : String.valueOf(count));
        } else {
            binding.tvBadge.setVisibility(View.GONE);
        }
    }
}