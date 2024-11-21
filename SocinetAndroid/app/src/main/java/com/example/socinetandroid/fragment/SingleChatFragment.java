package com.example.socinetandroid.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.activity.ConversationActivity;
import com.example.socinetandroid.adapter.RealtimeChatAdapter;
import com.example.socinetandroid.databinding.FragmentSingleChatBinding;
import com.example.socinetandroid.model.RealtimeChat;
import com.example.socinetandroid.model.RealtimeRoom;
import com.example.socinetandroid.model.RealtimeUser;
import com.example.socinetandroid.repository.FriendRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.SocketManager;
import com.example.socinetandroid.viewmodel.AppViewModel;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SingleChatFragment extends Fragment {
    public static final String TAG = "SINGLE_CHAT";
    private FragmentSingleChatBinding bd;
    private RealtimeChatAdapter realtimeChatAdapter;
    private FriendRepository friendRepository;
    private SocketManager socketManager;
    private AppViewModel appViewModel;

    public SingleChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bd = FragmentSingleChatBinding.inflate(getLayoutInflater());
        initialize();
        setEvent();
        return bd.getRoot();
    }

    private void initialize(){
        socketManager = SocketManager.getInstance(requireActivity().getApplication());
        appViewModel = ((MyApplication) requireActivity().getApplication()).getAppViewModel();
        friendRepository = RetrofitClient.createInstance(getContext()).create(FriendRepository.class);
        realtimeChatAdapter = new RealtimeChatAdapter(new ArrayList<>(), realtimeChat -> {
            Intent intent = new Intent(getActivity(), ConversationActivity.class);
            intent.putExtra(ConversationActivity.EXTRA_KEY, realtimeChat.getId());
            startActivity(intent);
        });
        bd.rcvConversationList.setLayoutManager(new LinearLayoutManager(getContext()));
        bd.rcvConversationList.setAdapter(realtimeChatAdapter);
        realtimeChatAdapter.setList(appViewModel.getLiveRealtimeUserList().getValue()
                .stream()
                .map(item -> new RealtimeChat(item.getUser(), item.getNewMessage(), item.isHasUnread(), item.getStatus()))
                .collect(Collectors.toList()));
        socketManager.emitFilterFriendStatus(appViewModel.getLiveRealtimeUserList().getValue().stream().map(item -> item.getUser().getId()).collect(Collectors.toList()));
    }

    private void setEvent(){
        appViewModel.getLiveRealtimeUserList().observe(getViewLifecycleOwner(), value -> {
            realtimeChatAdapter.updateList(value.stream().map(item -> new RealtimeChat(item.getUser(), item.getNewMessage(), item.isHasUnread(), item.getStatus())).collect(Collectors.toList()));
        });
    }
}