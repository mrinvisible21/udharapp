package com.example.udhar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class AddMember extends AppCompatActivity {

    ProgressDialog pd;
    EditText name,mobile,address;
    Button register;
    SessionManager sessionManager;
    HashMap<String, String> user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_member);

        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();
        Mytoolbar();
        Init();
    }

    private void Init() {
        name=(EditText)findViewById(R.id.member_name);
        mobile=(EditText)findViewById(R.id.member_mobile);
        address=(EditText)findViewById(R.id.member_address);
        register=(Button)findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateForm();
            }
        });


    }

    private void ValidateForm() {
        if (name.getText().toString().equalsIgnoreCase("")){
            MDToast mdToast = MDToast.makeText(AddMember.this, "Enter Member Name",2,MDToast.TYPE_ERROR);
            mdToast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            mdToast.show();
        }

        else if (mobile.getText().toString().equalsIgnoreCase("")){
            MDToast mdToast = MDToast.makeText(AddMember.this, "Enter Mobile Number",2,MDToast.TYPE_ERROR);
            mdToast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            mdToast.show();
        }
        else if (mobile.getText().toString().length()<10){
            MDToast mdToast = MDToast.makeText(AddMember.this, "Enter 10 digits Mobile Number",2,MDToast.TYPE_ERROR);
            mdToast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            mdToast.show();
        }


        else if (address.getText().toString().equalsIgnoreCase("")){
            MDToast mdToast = MDToast.makeText(AddMember.this, "Enter Address",2,MDToast.TYPE_ERROR);
            mdToast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            mdToast.show();
        }

        else{
            RegisterMember();

        }
    }

    private void Mytoolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Member");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void RegisterMember() {

        pd=new ProgressDialog(this);
        pd.setMessage("Adding Please wait.....");
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(AddMember.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST,BaseURL.ADD_MEMBER ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        NextActivity(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("u_id",user.get(SessionManager.KEY_U_ID));
                params.put("m_name", name.getText().toString());
                params.put("m_mobile", mobile.getText().toString());
                params.put("m_address", address.getText().toString());
                params.put("token",BaseURL.MY_KEY);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private void NextActivity(String response) {
        try {
            JSONArray jsonarray = new JSONArray(response);
            JSONObject jsonobject = jsonarray.getJSONObject(0);

            String message = jsonobject.getString("message");
            String error = jsonobject.getString("error");

            if(error.equalsIgnoreCase("false")) {
                name.getText().clear();
                mobile.getText().clear();
                address.getText().clear();

                MDToast mdToast = MDToast.makeText(AddMember.this, message, 2, MDToast.TYPE_SUCCESS);
                mdToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                mdToast.show();
            }
            else if(error.equalsIgnoreCase("true")) {
                MDToast mdToast = MDToast.makeText(AddMember.this, message, 2, MDToast.TYPE_ERROR);
                mdToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                mdToast.show();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
