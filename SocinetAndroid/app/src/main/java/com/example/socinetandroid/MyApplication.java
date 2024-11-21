package com.example.socinetandroid;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.lifecycle.ViewModelProvider;

import com.example.socinetandroid.utils.SocketManager;
import com.example.socinetandroid.viewmodel.AppViewModel;

public class MyApplication extends Application {
    private AppViewModel appViewModel;
    public static final String CHANNEL_ID = "channel_service";
    private SocketManager socketManager;
    @Override
    public void onCreate() {
        super.onCreate();
        appViewModel = new ViewModelProvider.AndroidViewModelFactory(this).create(AppViewModel.class);
        socketManager = SocketManager.getInstance(this);
        createNotificationChannel();
    }

    public AppViewModel getAppViewModel(){
        return appViewModel;
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel Service", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if(manager != null){
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if(socketManager != null){
            socketManager.disconnect();
        }
    }
}
