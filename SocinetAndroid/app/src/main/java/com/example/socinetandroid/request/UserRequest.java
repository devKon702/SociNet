package com.example.socinetandroid.request;

import android.app.Activity;
import android.net.Uri;

import com.example.socinetandroid.utils.FileSupporter;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UserRequest {
    private RequestBody name;
    private RequestBody phone;
    private RequestBody school;
    private RequestBody address;
    private RequestBody isMale;
    private MultipartBody.Part avatarFile;
    private UserRequest(Builder builder) {
        this.name = builder.name;
        this.phone = builder.phone;
        this.school = builder.school;
        this.address = builder.address;
        this.isMale = builder.isMale;
        this.avatarFile = builder.avatarFile;
    }

    public RequestBody getName() {
        return name;
    }

    public RequestBody getPhone() {
        return phone;
    }

    public RequestBody getSchool() {
        return school;
    }

    public RequestBody getAddress() {
        return address;
    }

    public RequestBody getIsMale() {
        return isMale;
    }

    public MultipartBody.Part getAvatarFile() {
        return avatarFile;
    }

    public static class Builder {
        private RequestBody name;
        private RequestBody phone;
        private RequestBody school;
        private RequestBody address;
        private RequestBody isMale;
        private MultipartBody.Part avatarFile;

        public Builder name(String name) {
           this.name = RequestBody.create(MediaType.parse("multipart/form-data"), name);
            return this;
        }

        public Builder phone(String phone) {
            this.phone = RequestBody.create(MediaType.parse("multipart/form-data"), phone);
            return this;
        }

        public Builder school(String school) {
            this.school = RequestBody.create(MediaType.parse("multipart/form-data"), school);
            return this;
        }

        public Builder address(String address) {
            this.address = RequestBody.create(MediaType.parse("multipart/form-data"), address);
            return this;
        }

        public Builder isMale(boolean isMale) {
            this.isMale = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(isMale));;
            return this;
        }

        public Builder avatarFile(Activity activity, Uri avatarUri) {
            if(avatarUri != null){
                File file = FileSupporter.createFileFromUri(activity, avatarUri);
                String mimeType = activity.getContentResolver().getType(avatarUri);
                assert mimeType != null;
                RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
                this.avatarFile = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            }
            return this;
        }

        public UserRequest build() {
            return new UserRequest(this);
        }
    }
}
