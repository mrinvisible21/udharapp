package com.example.udhar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.shadowfax.proswipebutton.ProSwipeButton;

public class Login extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private ProSwipeButton login;
    TextView newuser;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initView();
        setListener();

        sessionManager = new SessionManager(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void initView() {
        etUsername = findViewById(R.id.user_email);
        etPassword = findViewById(R.id.user_password);
        login = findViewById(R.id.login_btn);
        newuser=(TextView)findViewById(R.id.newuser);
        newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,AddUser.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void setListener() {
        login.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UserLogin();
                    }
                }, 2000);
            }
        });

    }

    private void UserLogin() {
        if (etUsername.getText().toString().equalsIgnoreCase("")) {
            login.showResultIcon(false);
            login.setText("enter username");
        } else if (etPassword.getText().toString().equalsIgnoreCase("")) {
            login.showResultIcon(false);
            login.setText("enter password");

        } else {
            if (BaseURL.isOnline(getApplicationContext())) {
                loginRequest();
            } else {
                login.setText("Internet Connection not found");
                login.showResultIcon(false);
                MDToast mdToast = MDToast.makeText(Login.this, "Turn on Data Connection", 2, MDToast.TYPE_WARNING);
                mdToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                mdToast.show();
            }
        }
    }

    private void loginRequest() {

        RequestQueue queue = Volley.newRequestQueue(Login.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, BaseURL.USER_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.equalsIgnoreCase("Invalid user")) {
                            login.setText("Invalid User");
                            login.showResultIcon(false);
                        }
                        else if (response.equalsIgnoreCase("user not found")) {
                            login.setText("SOMETHING WENT WRONG");
                            login.showResultIcon(false);
                        }
                        else {
                            SetUserData(response);
                        }
                        //Toast.makeText(Login.this,response,Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(Login.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @SuppressLint("HardwareIds")
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("u_mobile", etUsername.getText().toString());
                params.put("u_password", etPassword.getText().toString());
                params.put("AndroidID", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                params.put("AndroidModel", Build.MODEL);
                params.put("token", BaseURL.MY_KEY);

                return params;
            }

        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void SetUserData(String response) {

        try {

            JSONArray jsonarray = new JSONArray(response);
            JSONObject jsonobject = jsonarray.getJSONObject(0);

            String u_id = jsonobject.getString("u_id");
            String u_name = jsonobject.getString("u_name");
            String u_mobile = jsonobject.getString("u_mobile");
            String u_address = jsonobject.getString("u_address");
            String error = jsonobject.getString("error");

            sessionManager.userdata(u_id, u_name, u_mobile, u_address);

            if (error.equals("false")) {
                sessionManager.createLoginSession(u_mobile);
                login.showResultIcon(true);
                StartDashboardActivity();
            }
            else {
                login.showResultIcon(false);
                login.setText("Invalid user");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void StartDashboardActivity() {
        Intent intent = new Intent(Login.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
