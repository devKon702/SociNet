package com.example.socinetandroid.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.socinetandroid.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class FileSupporter {
    public static boolean isImageUrl(String url){
        if(url == null) return false;
        List<String> typeList = Arrays.asList(".apng", ".png", ".jpg", ".jpeg", ".jfif", ".pjpeg", ".pjp", ".svg", ".webp", ".avif", ".gif");
        return typeList.stream().anyMatch(url::contains);
    }

    public static boolean isVideoUrl(String url){
        if(url == null) return false;
        List<String> typeList = Arrays.asList(".mp4", ".webm", ".avi", ".mov", ".wmv");
        return typeList.stream().anyMatch(url::contains);
    }

    public static long getFileSizeFromUri(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        long size = 0;

        if (cursor != null && cursor.moveToFirst()) {
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            if (!cursor.isNull(sizeIndex)) {
                size = cursor.getLong(sizeIndex);
            }
            cursor.close();
        }
        return size; // Kích thước file tính bằng bytes
    }

    public static File createFileFromUri(Context context, Uri uri) {
        // Tạo thư mục tạm trong bộ nhớ cache của ứng dụng
        File cacheDir = new File(context.getCacheDir(), "tempFiles");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        // Tạo một file mới
        String fileName = getFileName(context, uri);
        if(fileName != null){
            String mimeType = context.getContentResolver().getType(uri);
            assert mimeType != null;
            fileName = "file." + mimeType.substring(mimeType.indexOf("/") + 1);
        }
        File file = new File(cacheDir, fileName);

        // Đọc dữ liệu từ Uri và ghi vào File
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while (true) {
                assert inputStream != null;
                if (!((length = inputStream.read(buffer)) > 0)) break;
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FILE ERROR", e.getMessage());
        }

        return file;
    }

    @SuppressLint("Range")
    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    public static void loadImage(ImageView view, String url){
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.unknown_avatar)
                .into(view);
    }

    public static void loadVideo(Context context, PlayerView view, String url){
        ExoPlayer exoPlayer = new ExoPlayer.Builder(context).build();
        view.setPlayer(exoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(url);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
    }

    public static boolean isReadExternalStorageAllowed(Activity activity){
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestStoragePermission(Activity activity){
        String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(activity, permission, 2);
    }

//    public interface IFilePicker{
//        void onGranted();
//    }

//    public static void requestPermissionAndOpenGallery(Activity activity, boolean isIncludeVideo, IFilePicker iFilePicker){
//
//        PermissionListener permissionlistener = new PermissionListener() {
//            @Override
//            public void onPermissionGranted() {
//                Toast.makeText(activity, "Permission Granted", Toast.LENGTH_SHORT).show();
//                iFilePicker.onGranted();
//            }
//
//            @Override
//            public void onPermissionDenied(List<String> deniedPermissions) {
//                Toast.makeText(activity, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
//            }
//        };
//        TedPermission.create()
//                .setPermissionListener(permissionlistener)
//                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
//                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES)
//                .check();
//    }
}
