package com.example.socinetandroid.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.socinetandroid.R;
import com.example.socinetandroid.databinding.ActivityChatBinding;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initial();
        setContentView(bd.getRoot());
        setEvent();
    }

    private void initial(){
        bd = ActivityChatBinding.inflate(getLayoutInflater());
    }
    private void setEvent(){
        bd.ivBack.setOnClickListener(v -> {
            finish();
        });
    }
}