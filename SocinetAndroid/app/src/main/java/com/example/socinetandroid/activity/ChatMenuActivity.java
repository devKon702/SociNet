package com.example.socinetandroid.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.socinetandroid.R;
import com.example.socinetandroid.adapter.RealtimeChatAdapter;
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
        switchFragment(new SingleChatFragment(), SingleChatFragment.TAG);
    }

    private void setEvent(){


        binding.bottomNavigationChat.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.navigation_single){
                binding.tvTitle.setText("Trò chuyện riêng");
                switchFragment(new SingleChatFragment(), SingleChatFragment.TAG);
            }
            else if(item.getItemId() == R.id.navigation_room){
                binding.tvTitle.setText("Trò chuyện nhóm");
                switchFragment(new RoomChatFragment(), RoomChatFragment.TAG);
            }
            return true;
        });

        binding.ivHome.setOnClickListener(v -> {
            finish();
        });
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
}