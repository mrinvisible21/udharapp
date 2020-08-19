package com.example.udhar;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.HashMap;

public class Welcome extends AppCompatActivity {

    SessionManager sessionManager;
    HashMap<String, String> user;

    Context context;

    public int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.welcome);


        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();

        if(sessionManager.isLoggedIn()){
            new Handler().postDelayed(new Runnable() {
                                          @Override
                                          public void run() {
                                              GotoDashBoard();}
                                      },
                    SPLASH_TIME_OUT);
        }
        else {
            sessionManager.checkLogin();
        }
    }



    private void GotoDashBoard() {

        if (BaseURL.isOnline(getApplicationContext())) {
            Intent intent = new Intent(Welcome.this, Dashboard.class);
            startActivity(intent);
            finish();
        }
        else {
            Toast mdToast=  Toast.makeText(getApplicationContext(), "Please turn on data connection", Toast.LENGTH_LONG);
            mdToast.setGravity(Gravity.TOP, 0, 0);
            mdToast.show();
        }
    }

}
