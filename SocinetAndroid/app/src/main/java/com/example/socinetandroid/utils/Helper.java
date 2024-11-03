package com.example.socinetandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.Api19Impl;

import com.example.socinetandroid.R;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Response;

public class Helper {
    private static final Gson gson = new Gson();

    // Hàm generic để chuyển đổi kết quả JSON thành đối tượng hoặc danh sách đối tượng
    public static <T> T convertDataToType(Object data, Type type) {
        return gson.fromJson(gson.toJson(data), type);
    }

    // Hàm tiện ích để lấy Type của một class
    public static <T> Type getType(Class<T> clazz) {
        return TypeToken.get(clazz).getType();
    }

    // Hàm tiện ích để lấy Type của một danh sách các đối tượng
    public static <T> Type getListType(Class<T> clazz) {
        return TypeToken.getParameterized(List.class, clazz).getType();
    }
    public static BottomSheetDialog setupBottomSheetDialog(BottomSheetDialog bottomSheetDialog, Context context, int bottomSheetLayoutId, boolean isFullScreen, int topParentId){
        View viewDialog = LayoutInflater.from(context).inflate(bottomSheetLayoutId, null);
        bottomSheetDialog.setContentView(viewDialog);

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) viewDialog.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);

        if(isFullScreen){
            LinearLayout layout = bottomSheetDialog.findViewById(topParentId);
            assert  layout != null;
            layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
        }

        return bottomSheetDialog;
    }

    public static void saveSharedPrefs(Context context, String key, String value){
        SharedPreferences sharedPrefs = context.getSharedPreferences("Socinet", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void clearSharedPrefs(Context context){
        context.getSharedPreferences("Socinet", Context.MODE_PRIVATE)
                .edit().clear().apply();
    }

    public static String getSharedPrefsValue(Context context, String key){
        return context.getSharedPreferences("Socinet", Context.MODE_PRIVATE)
                .getString(key, null);
    }

    public static void handleRetrofitResponse(Response<ApiResponse> response, IRetrofitResponseHandler handler){
        if(response.body() != null){
            ApiResponse result = response.body();
            if(result.isSuccess()){
                handler.onSuccess(result);
            } else {
                handler.onFail(result);
            }
        }
        else {
            handler.onFail(convertDataToType(response.errorBody(), ApiResponse.class));
            Log.e("API ERROR", response.toString());
        }
    }

    public static void refreshToken(){

    }
}
