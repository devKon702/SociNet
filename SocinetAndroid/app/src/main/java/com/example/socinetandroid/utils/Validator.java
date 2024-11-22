package com.example.socinetandroid.utils;

import android.util.Patterns;

public class Validator {
    public static boolean isValidEmail(String email){
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
