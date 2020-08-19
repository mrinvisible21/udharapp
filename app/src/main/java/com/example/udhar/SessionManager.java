package com.example.udhar;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class SessionManager {
    private SharedPreferences pref;
    private Editor editor;
    private Context context;
    private static final String PREF_NAME = "pref";
    private static final String IS_LOGIN = "IsLoggedIn";
    static final String SESSION = "u_mobile";
    static final String KEY_U_ID = "u_id";
    static final String KEY_U_NAME = "u_name";
    static final String KEY_U_MOBILE = "u_mobile";
    static final String KEY_U_ADDRESS = "u_address";

    @SuppressLint("CommitPrefEdits")
    SessionManager(Context context){
        this.context = context;
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    void createLoginSession(String email){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(SESSION, email);
        editor.commit();
    }

    void checkLogin(){
        if(!this.isLoggedIn()){
            Intent i = new Intent(context, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

    }


    void userdata(String u_id,String u_name,String u_mobile,String u_address){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_U_ID, u_id);
        editor.putString(KEY_U_NAME, u_name);
        editor.putString(KEY_U_MOBILE, u_mobile);
        editor.putString(KEY_U_ADDRESS, u_address);
        editor.commit();
    }

    HashMap<String, String> getUserDetails(){

        HashMap<String, String> user = new HashMap<String, String>();
        user.put(SESSION, pref.getString(SESSION, null));
        user.put(KEY_U_ID, pref.getString(KEY_U_ID, null));
        user.put(KEY_U_NAME, pref.getString(KEY_U_NAME, null));
        user.put(KEY_U_MOBILE, pref.getString(KEY_U_MOBILE, null));
        user.put(KEY_U_ADDRESS, pref.getString(KEY_U_ADDRESS, null));

        return user;
    }

    void logoutUser(){
        Intent i = new Intent(context, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        editor.clear();
        editor.commit();

    }

    boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

}
