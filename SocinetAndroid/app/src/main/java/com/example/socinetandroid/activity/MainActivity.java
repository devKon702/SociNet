package com.example.socinetandroid.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.socinetandroid.R;
import com.example.socinetandroid.databinding.ActivityMainBinding;
import com.example.socinetandroid.fragment.AccountFragment;
import com.example.socinetandroid.fragment.HomeFragment;
import com.example.socinetandroid.fragment.InvitationFragment;
import com.example.socinetandroid.service.SampleService;
import com.example.socinetandroid.utils.GlobalData;
import com.example.socinetandroid.viewmodel.PostViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initial();
        setContentView(binding.getRoot());
        setEvent();
    }

    private void initial(){
        PostViewModel postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.bottomNavigation.setBackground(null);
        binding.bottomNavigation.getMenu().getItem(3).setEnabled(false);
        if(GlobalData.user != null){
            binding.tvTest.setText(GlobalData.user.getName());
        }
        switchFragment(new HomeFragment(), HomeFragment.TAG);
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

        binding.headerLogo.setOnClickListener(v -> startService(new Intent(this, SampleService.class)));
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