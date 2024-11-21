package com.example.socinetandroid.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.socinetandroid.databinding.BottomSheetConversationActionBinding;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Conversation;
import com.example.socinetandroid.model.User;
import com.example.socinetandroid.repository.ConversationRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.utils.SocketManager;
import com.example.socinetandroid.viewmodel.ConversationViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationActionBottomSheet extends BottomSheetDialogFragment {
    private Conversation currentConversation;
    private BottomSheetConversationActionBinding bd;
    private ConversationViewModel conversationViewModel;
    private ConversationRepository conversationRepository;
    private int confirmCount = 0;
    private SocketManager socketManager;
    public ConversationActionBottomSheet(Conversation conversation){
        this.currentConversation = conversation;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bd = BottomSheetConversationActionBinding.inflate(getLayoutInflater());
        return bd.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
        setEvent();
    }

    private void initialize(){
        conversationViewModel = new ViewModelProvider(requireActivity()).get(ConversationViewModel.class);
        conversationRepository = RetrofitClient.createInstance(getContext()).create(ConversationRepository.class);
        socketManager = SocketManager.getInstance(requireActivity().getApplication());
    }

    private void setEvent(){
        bd.tvUpdate.setOnClickListener(v -> {
            conversationViewModel.updatingConversation(currentConversation);
            dismiss();
        });
        bd.tvRemove.setOnClickListener(v -> {
            if(confirmCount == 0){
                Toast.makeText(getContext(), "Nhấn thêm lần nữa để thu hồi", Toast.LENGTH_SHORT).show();
                confirmCount++;
            } else if(confirmCount == 1){
                conversationRepository.removeChat(currentConversation.getId()).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                            @Override
                            public void onSuccess(ApiResponse result) {
                                Toast.makeText(getContext(), "Thu hồi thành công", Toast.LENGTH_SHORT).show();
                                Conversation conversation = Helper.convertDataToType(result.getData(), Helper.getType(Conversation.class));
                                conversationViewModel.updatedConversation(conversation);
                                socketManager.updateConversationMessage(conversation);
                            }

                            @Override
                            public void onFail(ApiResponse result) {
                                Toast.makeText(getContext(), "Thu hồi thất bại\n" + result.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        dismiss();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                        Log.e("API", throwable.getMessage());
                    }
                });
            }
        });
    }
}
