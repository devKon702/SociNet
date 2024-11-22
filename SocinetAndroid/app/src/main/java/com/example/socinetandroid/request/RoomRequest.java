package com.example.socinetandroid.request;

import android.app.Activity;
import android.net.Uri;

import com.example.socinetandroid.utils.FileSupporter;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RoomRequest {
    private RequestBody namePart;
    private MultipartBody.Part avatarPart;

    public RoomRequest(Activity activity, String name, Uri avatarUri){
        if(avatarUri != null){
            File file = FileSupporter.createFileFromUri(activity, avatarUri);
            String mimeType = activity.getContentResolver().getType(avatarUri);
            assert mimeType != null;
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            this.avatarPart = MultipartBody.Part.createFormData("avatarFile", file.getName(), requestFile);
        }
        this.namePart = RequestBody.create(MediaType.parse("multipart/form-data"), name);
    }

    public RequestBody getNamePart() {
        return namePart;
    }

    public MultipartBody.Part getAvatarPart() {
        return avatarPart;
    }
}
