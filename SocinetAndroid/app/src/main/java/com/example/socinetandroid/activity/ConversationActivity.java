package com.example.socinetandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.R;
import com.example.socinetandroid.adapter.ConversationAdapter;
import com.example.socinetandroid.databinding.ActivityConversationBinding;
import com.example.socinetandroid.dialog.ConversationActionBottomSheet;
import com.example.socinetandroid.enums.RealtimeStatus;
import com.example.socinetandroid.interfaces.IConversationListener;
import com.example.socinetandroid.interfaces.IRefreshTokenHandler;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Conversation;
import com.example.socinetandroid.model.User;
import com.example.socinetandroid.repository.ConversationRepository;
import com.example.socinetandroid.repository.UserRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.request.MessageRequest;
import com.example.socinetandroid.utils.FileSupporter;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.utils.SocketManager;
import com.example.socinetandroid.utils.TokenManager;
import com.example.socinetandroid.viewmodel.AppViewModel;
import com.example.socinetandroid.viewmodel.ConversationViewModel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationActivity extends AppCompatActivity implements IConversationListener {
    public static String EXTRA_KEY = "USER_ID";
    private ActivityConversationBinding bd;
    private ConversationRepository conversationRepository;
    private UserRepository userRepository;
    private ConversationAdapter conversationAdapter;
    private AppViewModel appViewModel;
    private SocketManager socketManager;
    private ConversationViewModel conversationViewModel;
    private long userId;
    private Uri uri;

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
        bd = ActivityConversationBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());
        userId = getIntent().getLongExtra(EXTRA_KEY, -1);
        if(userId == -1) {
            Toast.makeText(this, "Thiếu UserId", Toast.LENGTH_SHORT).show();
            finish();
        }
        new TokenManager(this).callRefreshToken(new IRefreshTokenHandler() {
            @Override
            public void handleSuccess() {
                initial();
                setEvent();
            }

            @Override
            public void handleFail() {
                Log.e("API", "Call Refresh Token fail");
            }
        });
    }

    private void initial(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        conversationViewModel = new ViewModelProvider(this).get(ConversationViewModel.class);
        appViewModel = ((MyApplication) getApplication()).getAppViewModel();
        socketManager = SocketManager.getInstance(getApplication());
        userRepository = RetrofitClient.createInstance(this).create(UserRepository.class);
        conversationRepository = RetrofitClient.getInstance().create(ConversationRepository.class);
        conversationAdapter = new ConversationAdapter(this, new ArrayList<>(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        bd.rcvConversation.setLayoutManager(linearLayoutManager);
        bd.rcvConversation.setAdapter(conversationAdapter);
//        userRepository.getUserById(userId).enqueue(new Callback<ApiResponse>() {
//            @Override
//            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
//                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
//                    @Override
//                    public void onSuccess(ApiResponse result) {
//                        User user = Helper.convertDataToType(result.getData(), Helper.getType(User.class));
//                        FileSupporter.loadImage(bd.ivAvatar, user.getAvatarUrl());
//                        bd.tvName.setText(user.getName());
//                        bd.tvStatus.setText("Unknown");
//                    }
//
//                    @Override
//                    public void onFail(ApiResponse result) {
//                        Toast.makeText(ConversationActivity.this, "Lấy thông tin user thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
//                Log.e("API", throwable.getMessage());
//            }
//        });
        appViewModel.getLiveRealtimeUserList().getValue().forEach(realtimeUser -> {
            if(realtimeUser.getUser().getId() == userId){
                User user = realtimeUser.getUser();
                setupStatusText(realtimeUser.getStatus());
                FileSupporter.loadImage(bd.ivAvatar, user.getAvatarUrl());
                bd.tvName.setText(user.getName());
            }
        });

        conversationRepository.getChatWithUser(userId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        List<Conversation> conversationList = Helper.convertDataToType(result.getData(), Helper.getListType(Conversation.class));
                        conversationAdapter.setList(conversationList);
                    }

                    @Override
                    public void onFail(ApiResponse result) {
                        Toast.makeText(ConversationActivity.this, "Lấy danh sách tin nhắn thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
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
        appViewModel.readConversation(userId);
        socketManager.readConversationWithUserId(userId);
        bd.ivBack.setOnClickListener(v -> {
            finish();
        });

        appViewModel.getLiveNewestConversation().observe(this, conversation -> {
            if(conversation.getSenderId() == userId || conversation.getReceiverId() == userId){
                conversationAdapter.updateListWithItem(conversation);
                appViewModel.readConversation(userId);
                socketManager.readConversationWithUserId(userId);
                int position = ((LinearLayoutManager)bd.rcvConversation.getLayoutManager()).findFirstVisibleItemPosition();
                if(position == 0){
                    bd.rcvConversation.scrollToPosition(0);
                }
            }
        });
        appViewModel.getLiveNewestRealtimeUser().observe(this, realtimeUser -> {
            if(realtimeUser.getUser().getId() == userId){
                setupStatusText(realtimeUser.getStatus());
            }
        });

        conversationViewModel.getLiveState().observe(this, value -> {
            switch(value){
                case ConversationViewModel.NORMAL:
                    bd.edtContent.setText("");
                    bd.layoutState.setVisibility(View.GONE);
                    bd.tvState.setText("");
                    bd.tvCurrentContent.setText("");
                    uri = null;
                    bd.ivImage.setImageURI(null);
                    break;
                case ConversationViewModel.NEW_CONVERSATION:
//                    conversationAdapter.addItem(conversationViewModel.getNewConversation());
                    conversationViewModel.reset();
//                    bd.rcvConversation.scrollToPosition(0);
                    break;
                case ConversationViewModel.UPDATING:
                    bd.tvCurrentContent.setText(conversationViewModel.getCurrentConversation().getContent());
                    bd.tvState.setText("Đang chỉnh sửa");
                    bd.layoutState.setVisibility(View.VISIBLE);
                    bd.edtContent.setText(conversationViewModel.getCurrentConversation().getContent());
                    break;
                case ConversationViewModel.CHANGED_CONVERSATION:
//                    conversationAdapter.updateItem(conversationViewModel.getNewConversation());
                    conversationViewModel.reset();
                    break;
            }
        });

        bd.cvClose.setOnClickListener(v -> {
            if(conversationViewModel.getLiveState().getValue() == ConversationViewModel.NORMAL){
                uri = null;
                bd.ivImage.setImageURI(null);
                bd.layoutState.setVisibility(View.GONE);
            } else {
                conversationViewModel.reset();
            }
        });

        bd.ivChooseFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            mActivityResultLauncher.launch(Intent.createChooser(intent, "Chọn ảnh"));
        });

        bd.ivSend.setOnClickListener(v -> {
            switch (conversationViewModel.getLiveState().getValue()){
                case ConversationViewModel.NORMAL:
                    String content = bd.edtContent.getText().toString().trim();
                    if(content.isEmpty() && uri == null) {
                        return;
                    }
                    MessageRequest messageRequest = new MessageRequest(this, content, uri);
                    bd.ivSend.setClickable(false);
                    conversationRepository.createChatWithUser(
                            RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userId)),
                            messageRequest.getContentPart(),
                            messageRequest.getFilePart()).enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                                @Override
                                public void onSuccess(ApiResponse result) {
                                    Conversation conversation = Helper.convertDataToType(result.getData(), Helper.getType(Conversation.class));
                                    conversationViewModel.createdConversation(conversation);
                                    socketManager.newConversationMessage(conversation);
                                }

                                @Override
                                public void onFail(ApiResponse result) {
                                    Toast.makeText(ConversationActivity.this, "Gửi tin nhắn thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
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
                    conversationRepository.updateChat(conversationViewModel.getCurrentConversation().getId(), newContent).enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                                @Override
                                public void onSuccess(ApiResponse result) {
                                    Conversation conversation = Helper.convertDataToType(result.getData(), Helper.getType(Conversation.class));
                                    conversationViewModel.updatedConversation(conversation);
                                    bd.ivSend.setClickable(true);
                                    socketManager.updateConversationMessage(conversation);
                                }

                                @Override
                                public void onFail(ApiResponse result) {
                                    Toast.makeText(ConversationActivity.this, "Cập nhật tin nhắn thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
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
    public boolean onLongClick(Conversation conversation) {
        ConversationActionBottomSheet dialog = new ConversationActionBottomSheet(conversation);
        dialog.show(getSupportFragmentManager(), dialog.getTag());
        return true;
    }

    @Override
    public void onClick(ConversationAdapter.SelfConversationViewHolder holder, Conversation conversation) {

    }
    private void setupStatusText(RealtimeStatus status){
        if(status.equals(RealtimeStatus.ONLINE)) {
            bd.tvStatus.setText("Online");
            bd.tvStatus.setTextColor(ContextCompat.getColor(this, R.color.green));
        }
        if(status.equals(RealtimeStatus.OFFLINE)) {
            bd.tvStatus.setText("Offline");
            bd.tvStatus.setTextColor(ContextCompat.getColor(this, R.color.red));
        }
        if(status.equals(RealtimeStatus.STRANGE)) {
            bd.tvStatus.setText("Người lạ");
            bd.tvStatus.setTextColor(ContextCompat.getColor(this, R.color.darkGray));
        }
    }

    private void showImage(Uri uri){
        this.uri = uri;
        bd.ivImage.setImageURI(uri);
        bd.ivImage.setVisibility(View.VISIBLE);
        bd.layoutState.setVisibility(View.VISIBLE);
    }
}