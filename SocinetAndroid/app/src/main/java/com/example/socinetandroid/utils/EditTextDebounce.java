package com.example.socinetandroid.utils;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class EditTextDebounce {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable workRunnable;

    /**
     * Debounce text changes in EditText and call the provided API function
     *
     * @param editText EditText to watch
     * @param delayMillis Delay time in milliseconds
     * @param callback Function to call when text stabilizes
     */
    public void setDebounce(EditText editText, long delayMillis, DebounceHandler callback) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Cancel the previous debounce call
                if (workRunnable != null) {
                    handler.removeCallbacks(workRunnable);
                }

                // Schedule a new debounce call
                workRunnable = () -> callback.handleText(s.toString());
                handler.postDelayed(workRunnable, delayMillis);
            }
        });
    }

    // Functional interface for API calls
    public interface DebounceHandler{
        void handleText(String text);
    }
}
