package com.example.socinetandroid.request;

import android.app.Activity;
import android.net.Uri;

import com.example.socinetandroid.utils.FileSupporter;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MessageRequest {
    private RequestBody contentPart;
    private MultipartBody.Part filePart;

    public MessageRequest(Activity activity, String content, Uri uri){
        if(uri != null){
            File file = FileSupporter.createFileFromUri(activity, uri);
            String mimeType = activity.getContentResolver().getType(uri);
            assert mimeType != null;
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            this.filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        }
        this.contentPart = RequestBody.create(MediaType.parse("multipart/form-data"), content);
    }

    public RequestBody getContentPart() {
        return contentPart;
    }

    public MultipartBody.Part getFilePart() {
        return filePart;
    }
}
