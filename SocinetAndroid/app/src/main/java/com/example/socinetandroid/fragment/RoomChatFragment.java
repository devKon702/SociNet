package com.example.socinetandroid.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.activity.RoomActivity;
import com.example.socinetandroid.adapter.RealtimeChatAdapter;
import com.example.socinetandroid.databinding.FragmentRoomChatBinding;
import com.example.socinetandroid.dialog.CreateRoomBottomSheet;
import com.example.socinetandroid.interfaces.IRealtimeChatListener;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.RealtimeChat;
import com.example.socinetandroid.model.RealtimeRoom;
import com.example.socinetandroid.model.Room;
import com.example.socinetandroid.repository.RoomRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.viewmodel.AppViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RoomChatFragment extends Fragment {
    public static final String TAG = "ROOM_CHAT";
    private FragmentRoomChatBinding bd;
    private RealtimeChatAdapter realtimeChatAdapter;
    private AppViewModel appViewModel;
    public RoomChatFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bd = FragmentRoomChatBinding.inflate(getLayoutInflater());
        initialize();
        setEvent();
        return bd.getRoot();
    }

    private void initialize() {
        appViewModel = ((MyApplication) requireActivity().getApplication()).getAppViewModel();
//        roomRepository = RetrofitClient.createInstance(getContext()).create(RoomRepository.class);
        realtimeChatAdapter = new RealtimeChatAdapter(new ArrayList<>(), new IRealtimeChatListener() {
            @Override
            public void onItemClick(RealtimeChat realtimeChat) {
                Intent intent = new Intent(getActivity(), com.example.socinetandroid.activity.RoomActivity.class);
                intent.putExtra(RoomActivity.EXTRA_KEY, realtimeChat.getId());
                startActivity(intent);
            }
        });
        bd.rcvRoomList.setLayoutManager(new LinearLayoutManager(getContext()));
        bd.rcvRoomList.setAdapter(realtimeChatAdapter);
        realtimeChatAdapter.setList(appViewModel.getLiveRealtimeRoomList().getValue()
                .stream()
                .map(item -> new RealtimeChat(item.getRoom(), item.getNewMessage(), item.isHasUnread()))
                .collect(Collectors.toList()));
    }

    private void setEvent() {
        appViewModel.getLiveRealtimeRoomList().observe(getViewLifecycleOwner(), value -> {
            realtimeChatAdapter.updateList(value
                    .stream()
                    .map(item -> new RealtimeChat(item.getRoom(), item.getNewMessage(), item.isHasUnread()))
                     .collect(Collectors.toList()));
        });
        bd.btnCreate.setOnClickListener(v -> {
            CreateRoomBottomSheet dialog = new CreateRoomBottomSheet();
            dialog.show(getParentFragmentManager(), dialog.getTag());
        });
    }
}