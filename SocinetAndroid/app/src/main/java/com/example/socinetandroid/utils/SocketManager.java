package com.example.socinetandroid.utils;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.enums.SocketEvent;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Conversation;
import com.example.socinetandroid.model.Invitation;
import com.example.socinetandroid.model.Room;
import com.example.socinetandroid.model.RoomsActivity;
import com.example.socinetandroid.model.User;
import com.example.socinetandroid.repository.RoomRepository;
import com.example.socinetandroid.repository.UserRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.viewmodel.AppViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.socket.client.IO;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocketManager {
    private static SocketManager instance;
    private Socket socket;
    private Gson gson;
    private AppViewModel appViewModel;
    private Context context;
    static class CustomDateDeserializer implements JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String[] DATE_FORMATS = {
                    "EEE MMM dd HH:mm:ss zzzz yyyy",  // Ví dụ: Wed Nov 20 01:25:59 GMT+07:00 2024
                    "yyyy-MM-dd'T'HH:mm:ss.SSSZ",    // Ví dụ: 2024-11-20T01:25:59.000+0700
                    "MM/dd/yyyy",                    // Ví dụ: 11/20/2024
                    "yyyy-MM-dd",                    // Ví dụ: 2024-11-20
                    "dd/MM/yyyy"                     // Ví dụ: 20/11/2024
            };
            String dateString = json.getAsString();
            for (String format : DATE_FORMATS) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
                    return sdf.parse(dateString);  // Nếu parse thành công, trả về Date
                } catch (ParseException e) {
                    // Nếu parse không thành công, tiếp tục với định dạng tiếp theo
                }
            }
            return null;
        }
    }
    private SocketManager(Application application) {
        try {
            IO.Options options = IO.Options.builder()
                    .setTimeout(10_000)
                    .setReconnection(true)
                    .setReconnectionDelay(2_000).build();
            socket = IO.socket(Constant.SOCKET_URL, options);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        context = application.getApplicationContext();
        gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new CustomDateDeserializer())
                .create();
        appViewModel = ((MyApplication) application).getAppViewModel();
        setupSocketListener();
    }
    public static SocketManager getInstance(Application application){
        if(instance == null) {
            instance = new SocketManager(application);
        }
        return instance;
    }
    public void connect(){
        if(socket != null && !socket.connected()){
            socket.off();
            socket.on(Socket.EVENT_CONNECT, args -> {
                Log.i("SOCKET", "Socket connected");
                emitOnline(appViewModel.getUser());
                setupSocketListener();
            });
            socket.connect();
        }
    }
    public void disconnect(){
        if (socket != null && socket.connected()) {
            socket.disconnect();
            socket.close();
        }
    }
    private void setupSocketListener() {
        socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
            Log.e("SOCKET", "Socket connect fail" + gson.toJson(args));
        });

        socket.on(Socket.EVENT_DISCONNECT, args -> {
            Log.i("SOCKET", "Socket disconnected" + gson.toJson(args[0]));
            socket.off();
        });

        socket.on(SocketEvent.NEW_ONLINE_USER.getString(), args -> {
            User user = gson.fromJson(args[0].toString(), Helper.getType(User.class));
            appViewModel.newOnlineUser(user);
        });

        socket.on(SocketEvent.NEW_OFFLINE_USER.getString(), args -> {
            User user = gson.fromJson(args[0].toString(), Helper.getType(User.class));
            appViewModel.newOfflineUser(user);
        });
        socket.on(SocketEvent.FILTER_FRIEND_STATUS.getString(), args -> {
            List<Long> onlineIdList = gson.fromJson(args[0].toString(), Helper.getListType(Long.class));
            List<Long> hasUnreadUserIdList = gson.fromJson(args[1].toString(), Helper.getListType(Long.class));
            appViewModel.filterFriendStatus(onlineIdList, hasUnreadUserIdList);
        });

        socket.on(SocketEvent.GET_ROOM_STATUS.getString(), args -> {
            List<Long> unreadRoomIdList = gson.fromJson(args[0].toString(), Helper.getListType(Long.class));
            appViewModel.filterRoomStatus(unreadRoomIdList);
        });

        socket.on(SocketEvent.NEW_MESSAGE.getString(), args -> {
            Conversation conversation = gson.fromJson(args[0].toString(), Helper.getType(Conversation.class));
            appViewModel.newConversation(conversation);
        });

        socket.on(SocketEvent.UPDATE_MESSAGE.getString(), args -> {
            Conversation conversation = gson.fromJson(args[0].toString(), Helper.getType(Conversation.class));
            appViewModel.newUpdateConversation(conversation);
        });

        socket.on(SocketEvent.NEW_ROOM_MESSAGE.getString(), args -> {
            RoomsActivity message = gson.fromJson(args[1].toString(), Helper.getType(RoomsActivity.class));
            appViewModel.newRoomMessage(message);
        });

        socket.on(SocketEvent.UPDATE_ROOM_MESSAGE.getString(), args -> {
            RoomsActivity message = gson.fromJson(args[1].toString(), Helper.getType(RoomsActivity.class));
            appViewModel.newUpdateRoomMessage(message);
        });

        socket.on(SocketEvent.INVITE_TO_ROOM.getString(), args -> {
            long roomId = gson.fromJson(args[0].toString(), Helper.getType(Long.class));
            RoomRepository roomRepository = RetrofitClient.getInstance().create(RoomRepository.class);
            roomRepository.getRoomById(roomId).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                        @Override
                        public void onSuccess(ApiResponse result) {
                            Room room = Helper.convertDataToType(result.getData(), Helper.getType(Room.class));
                            appViewModel.inviteToRoom(room);
                            joinRoom(roomId);
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
        });

        socket.on(SocketEvent.NEW_MEMBER.getString(), args -> {
            RoomsActivity activity = gson.fromJson(args[1].toString(), Helper.getType(RoomsActivity.class));
            appViewModel.newMember(activity);
        });

        socket.on(SocketEvent.MEMBER_QUIT.getString(), args -> {
            RoomsActivity activity = gson.fromJson(args[1].toString(), Helper.getType(RoomsActivity.class));
            appViewModel.newMemberQuit(activity);
        });

        socket.on(SocketEvent.DISABLE_ROOM.getString(), args -> {
            long roomId = gson.fromJson(args[0].toString(), Helper.getType(Long.class));
            appViewModel.disableRoom(roomId);
        });


        socket.on(SocketEvent.NEW_INVITATION.getString(), args -> {
            Invitation invitation = gson.fromJson(args[0].toString(), Helper.getType(Invitation.class));
            appViewModel.newFriendInvitation(invitation);
        });

        socket.on(SocketEvent.RESPONSE_INVITATION.getString(), args -> {
            long receiverId = gson.fromJson(args[0].toString(), Helper.getType(Long.class));
            boolean isAccept = gson.fromJson(args[1].toString(), Helper.getType(Boolean.class));
            if(isAccept){
                UserRepository userRepository = RetrofitClient.getInstance().create(UserRepository.class);
                userRepository.getUserById(receiverId).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                            @Override
                            public void onSuccess(ApiResponse result) {
                                User user = Helper.convertDataToType(result.getData(), Helper.getType(User.class));
                                appViewModel.newAcceptedFriendInvitation(user);
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
        });


        socket.on(SocketEvent.FORCE_LOGOUT.getString(), args -> {
            new TokenManager(context).clearSharedPref();
        });
    }
    public void emitOnline(@NonNull User user){
        socket.emit(SocketEvent.NOTIFY_ONLINE.getString(), Helper.toJsonObject(user));
    }
    public void emitFilterFriendStatus(@NonNull List<Long> usersId){
        socket.emit(SocketEvent.FILTER_FRIEND_STATUS.getString(), Helper.toJsonArray(usersId));
    }
    public void emitFilterRoomStatus(){
        socket.emit(SocketEvent.GET_ROOM_STATUS.getString());
    }
    public void joinMultiRoom(@NonNull List<Long> roomIdList){
        socket.emit(SocketEvent.JOIN_MULTI_ROOM.getString(), Helper.toJsonArray(roomIdList));
    }

    public void newConversationMessage(@NonNull Conversation conversation){
        socket.emit(SocketEvent.NEW_MESSAGE.getString(), Helper.toJsonObject(conversation));
    }

    public void updateConversationMessage(@NonNull Conversation conversation){
        socket.emit(SocketEvent.UPDATE_MESSAGE.getString(), Helper.toJsonObject(conversation));
    }

    public void readConversationWithUserId(@NonNull long userId){
        socket.emit(SocketEvent.READ_CONVERSATION.getString(), userId);
    }

    public void newInvitation(Invitation invitation){
        socket.emit(SocketEvent.NEW_INVITATION.getString(), Helper.toJsonObject(invitation), Helper.toJsonObject(invitation.getSender()), invitation.getReceiver().getId());
    }

    public void responseInvitation(long senderId, boolean isAccept){
        socket.emit(SocketEvent.RESPONSE_INVITATION.getString(), senderId, isAccept);
    }

    public void newRoom(long roomId){
        socket.emit(SocketEvent.NEW_ROOM.getString(), roomId);
    }
    public void newRoomMessage(long roomId, RoomsActivity activity){
        socket.emit(SocketEvent.NEW_ROOM_MESSAGE.getString(), roomId, Helper.toJsonObject(activity));
    }
    public void updateRoomMessage(long roomId, RoomsActivity activity){
        socket.emit(SocketEvent.UPDATE_ROOM_MESSAGE.getString(), roomId, Helper.toJsonObject(activity));
    }
    public void readRoomMessage(long roomId){
        socket.emit(SocketEvent.READ_ROOM_MESSAGE.getString(), roomId);
    }
    public void inviteToRoom(long roomId, RoomsActivity activity){
        socket.emit(SocketEvent.INVITE_TO_ROOM.getString(), roomId, Helper.toJsonObject(activity));
    }
    public void joinRoom(long roomId){
        socket.emit(SocketEvent.JOIN_ROOM.getString(), roomId);
    }
    public void quitRoom(long roomId, RoomsActivity activity){
        socket.emit(SocketEvent.QUIT_ROOM.getString(), roomId, Helper.toJsonObject(activity));
    }
    public void disableRoom(long roomId){
        socket.emit(SocketEvent.DISABLE_ROOM.getString(), roomId);
    }
}
