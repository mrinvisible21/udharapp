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

public class AddUser extends AppCompatActivity {

    ProgressDialog pd;
    EditText name,mobile,address,password,cpassword;
    Button register;

    SessionManager sessionManager;
    HashMap<String, String> user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_user);

        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();
        Mytoolbar();
        Init();
    }

    private void Init() {
        name=(EditText)findViewById(R.id.name);
        mobile=(EditText)findViewById(R.id.mobile);
        address=(EditText)findViewById(R.id.address);
        password=(EditText)findViewById(R.id.password);
        cpassword=(EditText)findViewById(R.id.cpassword);
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
            MDToast mdToast = MDToast.makeText(AddUser.this, "Enter Name",2,MDToast.TYPE_ERROR);
            mdToast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            mdToast.show();
        }

        else if (mobile.getText().toString().equalsIgnoreCase("")){
            MDToast mdToast = MDToast.makeText(AddUser.this, "Enter Mobile Number",2,MDToast.TYPE_ERROR);
            mdToast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            mdToast.show();
        }
        else if (mobile.getText().toString().length()<10){
            MDToast mdToast = MDToast.makeText(AddUser.this, "Enter 10 digits Mobile Number",2,MDToast.TYPE_ERROR);
            mdToast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            mdToast.show();
        }


        else if (address.getText().toString().equalsIgnoreCase("")){
            MDToast mdToast = MDToast.makeText(AddUser.this, "Enter Address",2,MDToast.TYPE_ERROR);
            mdToast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            mdToast.show();
        }
        else if (password.getText().toString().equalsIgnoreCase("")){
            MDToast mdToast = MDToast.makeText(AddUser.this, "Enter Password",2,MDToast.TYPE_ERROR);
            mdToast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            mdToast.show();
        }

        else if (cpassword.getText().toString().equalsIgnoreCase("")){
            MDToast mdToast = MDToast.makeText(AddUser.this, "Confirm Your Password",2,MDToast.TYPE_ERROR);
            mdToast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            mdToast.show();
        }
        else if (!cpassword.getText().toString().equalsIgnoreCase(password.getText().toString())){
            MDToast mdToast = MDToast.makeText(AddUser.this, "Confirm Password Not Matching",2,MDToast.TYPE_ERROR);
            mdToast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            mdToast.show();
        }

        else{
            RegisterUser();

        }
    }

    private void Mytoolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("User Registration");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void RegisterUser() {

        pd=new ProgressDialog(this);
        pd.setMessage("Adding Please wait.....");
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(AddUser.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST,BaseURL.ADD_USER ,
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
                params.put("u_name", name.getText().toString());
                params.put("u_mobile", mobile.getText().toString());
                params.put("u_address", address.getText().toString());
                params.put("u_password", cpassword.getText().toString());
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
                MDToast mdToast = MDToast.makeText(AddUser.this, message, 10, MDToast.TYPE_SUCCESS);
                mdToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                mdToast.show();
                Intent intent=new Intent(AddUser.this,Login.class);
                startActivity(intent);
                finish();
            }
            else if(error.equalsIgnoreCase("true")) {
                MDToast mdToast = MDToast.makeText(AddUser.this, message, 10, MDToast.TYPE_ERROR);
                mdToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                mdToast.show();
                Intent intent=new Intent(AddUser.this,Login.class);
                startActivity(intent);
                finish();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
