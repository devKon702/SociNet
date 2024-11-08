package com.example.socinetandroid.request;

import android.app.Activity;
import android.net.Uri;

import com.example.socinetandroid.utils.FileSupporter;
import com.example.socinetandroid.utils.Helper;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PostRequest {
    private MultipartBody.Part filePart = null;
    private RequestBody captionPart = null;
    private RequestBody sharedPostIdPart = null;

    public PostRequest(Activity activity, Uri uri, String caption, long sharedPostId){
        if(uri != null){
            File file = FileSupporter.createFileFromUri(activity, uri);
            String mimeType = activity.getContentResolver().getType(uri);
            assert mimeType != null;
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            this.filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        }
        this.captionPart = RequestBody.create(MediaType.parse("multipart/form-data"), caption);
        if(sharedPostId != -1){
            this.sharedPostIdPart = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(sharedPostId));
        }
    }

    public MultipartBody.Part getFilePart() {
        return filePart;
    }

    public RequestBody getCaptionPart() {
        return captionPart;
    }

    public RequestBody getSharedPostIdPart() {
        return sharedPostIdPart;
    }
}
