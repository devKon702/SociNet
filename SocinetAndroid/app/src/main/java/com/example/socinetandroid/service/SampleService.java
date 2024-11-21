package com.example.socinetandroid.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.R;
import com.example.socinetandroid.activity.MainActivity;
import com.example.socinetandroid.utils.Constant;
import com.example.socinetandroid.viewmodel.AppViewModel;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SampleService extends Service {
    private Socket mSocket;
    private AppViewModel appViewModel;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendNotification();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void sendNotification(){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setContentTitle("Socinet Notification")
                .setContentText("Sample Service started")
                .setSmallIcon(R.drawable.ic_chat_solid)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }

    private void setupSocket(){
        try {
            mSocket = IO.socket(Constant.SOCKET_URL);
            setupSocketEventListening();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupSocketEventListening(){
    }
}
