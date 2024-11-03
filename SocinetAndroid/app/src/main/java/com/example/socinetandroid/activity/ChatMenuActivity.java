package com.example.socinetandroid.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.socinetandroid.R;
import com.example.socinetandroid.databinding.ActivityChatMenuBinding;
import com.example.socinetandroid.fragment.RoomChatFragment;
import com.example.socinetandroid.fragment.SingleChatFragment;

public class ChatMenuActivity extends AppCompatActivity {

    private ActivityChatMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initial();
        setContentView(binding.getRoot());
        setEvent();
    }

    private void initial(){
        binding = ActivityChatMenuBinding.inflate(getLayoutInflater());
        replaceFragment(new SingleChatFragment());
    }

    private void setEvent(){
        binding.bottomNavigationChat.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.navigation_single){
                binding.tvTitle.setText("Trò chuyện riêng");
                replaceFragment(new SingleChatFragment());
            }
            else if(item.getItemId() == R.id.navigation_room){
                binding.tvTitle.setText("Trò chuyện nhóm");
                replaceFragment(new RoomChatFragment());
            }
            return true;
        });

        binding.ivHome.setOnClickListener(v -> {
            finish();
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}