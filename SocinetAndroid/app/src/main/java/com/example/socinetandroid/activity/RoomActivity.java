package com.example.socinetandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.adapter.RoomActivityAdapter;
import com.example.socinetandroid.databinding.ActivityRoomBinding;
import com.example.socinetandroid.dialog.ActivityActionBottomSheet;
import com.example.socinetandroid.dialog.RoomMenuBottomSheet;
import com.example.socinetandroid.interfaces.IRefreshTokenHandler;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.interfaces.IRoomActivityListener;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.RealtimeRoom;
import com.example.socinetandroid.model.Room;
import com.example.socinetandroid.model.RoomMember;
import com.example.socinetandroid.model.RoomsActivity;
import com.example.socinetandroid.model.User;
import com.example.socinetandroid.repository.RoomRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.request.MessageRequest;
import com.example.socinetandroid.utils.FileSupporter;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.utils.SocketManager;
import com.example.socinetandroid.utils.TokenManager;
import com.example.socinetandroid.viewmodel.AppViewModel;
import com.example.socinetandroid.viewmodel.ConversationViewModel;
import com.example.socinetandroid.viewmodel.RoomViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomActivity extends AppCompatActivity implements IRoomActivityListener{
    public static final String EXTRA_KEY = "ROOM_ID";
    private ActivityRoomBinding bd;
    private long roomId;
//    private Room currentRoom;
    private RoomRepository roomRepository;
    private RoomActivityAdapter roomActivityAdapter;
    private RoomViewModel roomViewModel;
    private AppViewModel appViewModel;
    private SocketManager socketManager;
    private Uri fileUri;
    private final ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    assert result.getData() != null;
                    Uri uri = result.getData().getData();
                    if(uri != null) {
                        String mimeType = getContentResolver().getType(uri);
                        if(mimeType != null){
                            if(mimeType.startsWith("image/")){
                                showImage(uri);
                            }
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bd = ActivityRoomBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());
        roomId = getIntent().getLongExtra(EXTRA_KEY, -1);
        if(roomId == -1) {
            Toast.makeText(this, "Thiếu UserId", Toast.LENGTH_SHORT).show();
            finish();
        }
        new TokenManager(this).callRefreshToken(new IRefreshTokenHandler() {
            @Override
            public void handleSuccess() {
                initialize();
                setEvent();
            }

            @Override
            public void handleFail() {
                Log.e("API", "Call Refresh Token fail");
            }
        });

    }

    private void initialize(){
        appViewModel = ((MyApplication) getApplication()).getAppViewModel();
        socketManager = SocketManager.getInstance(getApplication());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);
        roomRepository = RetrofitClient.createInstance(this).create(RoomRepository.class);
        roomActivityAdapter = new RoomActivityAdapter(this, new ArrayList<>(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        bd.rcvActivity.setLayoutManager(linearLayoutManager);
        bd.rcvActivity.setAdapter(roomActivityAdapter);
        roomRepository.getRoomById(roomId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        Room room = Helper.convertDataToType(result.getData(), Helper.getType(Room.class));
                        roomViewModel.setRoom(room);
                    }
                    @Override
                    public void onFail(ApiResponse result) {
                        Toast.makeText(RoomActivity.this, "Thấy thông tin nhóm thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.e("API", throwable.getMessage());
            }
        });

        roomRepository.getActivitiesOfRoom(roomId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        List<RoomsActivity> list = Helper.convertDataToType(result.getData(), Helper.getListType(RoomsActivity.class));
                        roomActivityAdapter.setList(list);
                    }
                    @Override
                    public void onFail(ApiResponse result) {
                        Toast.makeText(RoomActivity.this, "Lấy tin nhắn thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
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
        socketManager.readRoomMessage(roomId);
        appViewModel.readRoomActivities(roomId);
        bd.ivBack.setOnClickListener(v -> finish());
        bd.ivMenu.setOnClickListener(v -> {
            RoomMenuBottomSheet dialog = new RoomMenuBottomSheet();
            dialog.show(getSupportFragmentManager(), dialog.getTag());
        });
        appViewModel.getLiveNewestRoomsActivity().observe(this, roomsActivity -> {
            if(roomsActivity.getRoomId() != roomId) return;
            else {
                socketManager.readRoomMessage(roomId);
                appViewModel.readRoomActivities(roomId);
            }
            int position = ((LinearLayoutManager)bd.rcvActivity.getLayoutManager()).findFirstVisibleItemPosition();
            switch (roomsActivity.getType()){
                case RoomsActivity.CHAT:
                    roomActivityAdapter.updateListWithItem(roomsActivity);
                    if(position == 0){
                        bd.rcvActivity.scrollToPosition(0);
                    }
                    break;
                case RoomsActivity.INVITE:
                case RoomsActivity.QUIT:
                    roomActivityAdapter.addItem(roomsActivity);
                    if(position == 0){
                        bd.rcvActivity.scrollToPosition(0);
                    }
                    refreshRoom();
                    break;
                case RoomsActivity.JOIN:
                    break;
                case RoomsActivity.KICK:
                    break;
            }
        });
        appViewModel.getLiveRealtimeRoomList().observe(this, value -> {
            for(RealtimeRoom realtimeRoom : value){
                if(realtimeRoom.getRoom().getId() == roomId) return;
            }
            finish();
        });

        roomViewModel.getLiveState().observe(this, value -> {
            switch (value){
                case RoomViewModel.NORMAL:
                    bd.edtContent.setText("");
                    bd.layoutState.setVisibility(View.GONE);
                    bd.tvState.setText("");
                    bd.tvCurrentContent.setText("");
                    bd.ivImage.setVisibility(View.GONE);
                    break;
                case RoomViewModel.NEW_ACTIVITY:
                    roomActivityAdapter.addItem(roomViewModel.getNewActivity());
                    roomViewModel.reset();
                    bd.rcvActivity.scrollToPosition(0);
                    break;
                case RoomViewModel.NEW_MEMBER_ADDED:
                    refreshRoom();

                    for(RoomsActivity activity : roomViewModel.getActivityList()){
                        roomActivityAdapter.addItem(activity);
                    }
                    roomViewModel.reset();
                    bd.rcvActivity.scrollToPosition(0);
                    break;
                case RoomViewModel.UPDATING:
                    bd.tvCurrentContent.setText(roomViewModel.getCurrentActivity().getContent());
                    bd.tvState.setText("Đang chỉnh sửa");
                    bd.layoutState.setVisibility(View.VISIBLE);
                    bd.edtContent.setText(roomViewModel.getCurrentActivity().getContent());
                    break;
                case RoomViewModel.CHANGED_ACTIVITY:
                    roomActivityAdapter.updateItem(roomViewModel.getNewActivity());
                    roomViewModel.reset();
                    break;
            }
        });

        roomViewModel.getLiveRoom().observe(this, room -> {
            if(room != null) {
                if(room.isActive()){
                    FileSupporter.loadImage(bd.ivAvatar, room.getAvatarUrl());
                    bd.tvName.setText(room.getName());
                } else {
                    bd.layoutInactive.setVisibility(View.VISIBLE);
                    finish();
                }

            }
        });

        bd.ivChooseFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            mActivityResultLauncher.launch(Intent.createChooser(intent, "Chọn ảnh"));
        });

        bd.cvClose.setOnClickListener(v -> {
            if(roomViewModel.getLiveState().getValue() == ConversationViewModel.NORMAL){
                fileUri = null;
                bd.ivImage.setImageURI(null);
                bd.layoutState.setVisibility(View.GONE);
            } else {
                roomViewModel.reset();
            }
        });
        bd.ivSend.setOnClickListener(v -> {
            switch (roomViewModel.getLiveState().getValue()){
                case RoomViewModel.NORMAL:
                    String content = bd.edtContent.getText().toString().trim();
                    if(content.isEmpty() && fileUri == null) {
                        return;
                    }
                    MessageRequest messageRequest = new MessageRequest(this, content, fileUri);
                    bd.ivSend.setClickable(false);
                    roomRepository.createChat(
                            roomId,
                            messageRequest.getContentPart(),
                            messageRequest.getFilePart()).enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                                @Override
                                public void onSuccess(ApiResponse result) {
                                    RoomsActivity activity = Helper.convertDataToType(result.getData(), Helper.getType(RoomsActivity.class));
                                    roomViewModel.createdActivity(activity);
                                    socketManager.newRoomMessage(roomId, activity);
                                }

                                @Override
                                public void onFail(ApiResponse result) {
                                    Toast.makeText(RoomActivity.this, "Gửi tin nhắn thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            bd.ivSend.setClickable(true);
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                            Log.e("API", throwable.getMessage());
                            bd.ivSend.setClickable(true);
                        }
                    });
                    break;
                case ConversationViewModel.UPDATING:
                    String newContent = bd.edtContent.getText().toString().trim();
                    if(newContent.isEmpty()) return;
                    bd.ivSend.setClickable(false);
                    roomRepository.updateChat(roomViewModel.getCurrentActivity().getId(), newContent).enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                                @Override
                                public void onSuccess(ApiResponse result) {
                                    RoomsActivity activity = Helper.convertDataToType(result.getData(), Helper.getType(RoomsActivity.class));
                                    roomViewModel.updatedActivity(activity);
                                    socketManager.updateRoomMessage(roomId, activity);
                                    bd.ivSend.setClickable(true);
                                }

                                @Override
                                public void onFail(ApiResponse result) {
                                    Toast.makeText(RoomActivity.this, "Cập nhật tin nhắn thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                                    bd.ivSend.setClickable(true);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                            Log.e("API", throwable.getMessage());
                            bd.ivSend.setClickable(true);
                        }
                    });
                    break;
            }
        });
    }

    @Override
    public boolean onLongClick(RoomsActivity roomsActivity) {
        ActivityActionBottomSheet dialog = new ActivityActionBottomSheet(roomsActivity);
        dialog.show(getSupportFragmentManager(), dialog.getTag());
        return true;
    }

    @Override
    public void onClick(RoomsActivity roomsActivity) {

    }

    private void showImage(Uri uri){
        this.fileUri = uri;
        bd.ivImage.setImageURI(uri);
        bd.ivImage.setVisibility(View.VISIBLE);
        bd.layoutState.setVisibility(View.VISIBLE);
    }
    private void refreshRoom(){
        roomRepository.getRoomById(roomId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        Room newRoom = Helper.convertDataToType(result.getData(), Helper.getType(Room.class));
                        roomViewModel.setRoom(newRoom);
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
}