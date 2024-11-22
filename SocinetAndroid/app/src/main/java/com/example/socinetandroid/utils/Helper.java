package com.example.socinetandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;


import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Date;
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
    public static void setupBottomSheetDialog(BottomSheetDialog bottomSheetDialog, View viewDialog, boolean isFullScreen, int topParentId){
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) viewDialog.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);

        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if(isFullScreen){
            View layout = bottomSheetDialog.findViewById(topParentId);
            assert  layout != null;
            if(layout instanceof LinearLayout){
                ((LinearLayout)layout).setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
            }
            // Constraint ko áp dụng được
//            else if(layout instanceof ConstraintLayout){
//                ((ConstraintLayout)layout).setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
//            }
        }
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

    public static void handleRetrofitResponse(Response<ApiResponse> response, IRetrofitResponseHandler handler) {
        if(response.body() != null){
            ApiResponse result = response.body();
            if(result.isSuccess()){
                handler.onSuccess(result);
            } else {
                handler.onFail(result);
            }
        }
        else if(response.errorBody() != null){
            try {
                handler.onFail(gson.fromJson(response.errorBody().string(), ApiResponse.class));
                Log.e("API ERROR", response.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> T clone(Object object, Type type){
        return gson.fromJson(gson.toJson(object), type);
    }
    public static JSONObject toJsonObject(Object object) {
        JSONObject jsonObject = new JSONObject();
        try {
            // Lấy tất cả các field (thuộc tính) của lớp
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true); // Cho phép truy cập vào các field private

                Object value = field.get(object);

                // Nếu field là một đối tượng lớp khác, đệ quy gọi toJsonObject
                if (value != null && !isPrimitiveOrString(value)) {
                    jsonObject.put(field.getName(), toJsonObject(value));
                } else {
                    jsonObject.put(field.getName(), value); // Thêm giá trị của field vào jsonObject
                }
            }
        } catch (IllegalAccessException | JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    // Kiểm tra xem một đối tượng có phải là kiểu nguyên thủy (primitive) hoặc String không
    private static boolean isPrimitiveOrString(Object value) {
        return value instanceof Date || value instanceof String || value instanceof Number || value instanceof Boolean || value.getClass().isPrimitive();
    }

    public static <T> JSONArray toJsonArray(List<T> list) {
        String jsonString = gson.toJson(list);
        try {
            // Chuyển chuỗi JSON thành JSONArray
            return new JSONArray(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Trả về null nếu có lỗi
        }
    }
}
