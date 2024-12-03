package com.example.socinetandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;


import com.example.socinetandroid.interfaces.IGetIpInfoHandler;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.IPResponse;
import com.example.socinetandroid.repository.IpRepository;
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
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    public static void getIpInfo(String ip, IGetIpInfoHandler handler){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ip-api.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IpRepository service = retrofit.create(IpRepository.class);
        Call<IPResponse> call;

        if (ip != null && !ip.isEmpty()) {
            call = service.getIpInfo(ip);
        } else {
            call = service.getCurrentIpInfo("61439");
        }

        call.enqueue(new Callback<IPResponse>() {
            @Override
            public void onResponse(Call<IPResponse> call, Response<IPResponse> response) {
                if (response.isSuccessful()) {
                    IPResponse result = response.body();
                    handler.onSuccess(result);
                } else {
                    handler.onFail();
                }
            }

            @Override
            public void onFailure(Call<IPResponse> call, Throwable t) {
                Log.e("IP", t.getMessage());
            }
        });
    }
    public static String getDeviceType(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown";
        }

        // Kiểm tra Tablet
        String tabletRegex = ".*(tablet|ipad|playbook|silk)|(android(?!.*mobi)).*";
        if (Pattern.compile(tabletRegex, Pattern.CASE_INSENSITIVE).matcher(userAgent).matches()) {
            return "Tablet";
        }

        // Kiểm tra Mobile
        String mobileRegex = ".*(Mobile|iP(hone|od)|Android|okhttp|BlackBerry|IEMobile|Kindle|Silk-Accelerated|(hpw|web)OS|Opera M(obi|ini)).*";
        if (Pattern.compile(mobileRegex, Pattern.CASE_INSENSITIVE).matcher(userAgent).matches()) {
            return "Mobile";
        }

        // Mặc định là PC
        return "PC";
    }
}
