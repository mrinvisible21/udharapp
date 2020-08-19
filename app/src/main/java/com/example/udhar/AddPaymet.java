package com.example.udhar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AddPaymet extends AppCompatActivity {

    ProgressDialog pd;
    EditText title,amount;
    TextView enddate;
    Button add;
    Intent intent;
    Calendar myCalendar;

    SessionManager sessionManager;
    HashMap<String, String> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_paymet);

        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();
        intent=getIntent();
        Mytoolbar();
        Init();

        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        enddate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddPaymet.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "y-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        enddate.setText(sdf.format(myCalendar.getTime()));
    }

    private void Init() {
        title=(EditText)findViewById(R.id.title);
        amount=(EditText)findViewById(R.id.amount);
        enddate=(TextView) findViewById(R.id.enddate);
        add=(Button)findViewById(R.id.addpayment);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateForm();
            }
        });


    }

    private void ValidateForm() {
        if (title.getText().toString().equalsIgnoreCase("")){
            MDToast mdToast = MDToast.makeText(AddPaymet.this, "Enter Title",2,MDToast.TYPE_ERROR);
            mdToast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            mdToast.show();
        }

        else if (amount.getText().toString().equalsIgnoreCase("")){
            MDToast mdToast = MDToast.makeText(AddPaymet.this, "Enter Amount",2,MDToast.TYPE_ERROR);
            mdToast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            mdToast.show();
        }
        else if (enddate.getText().toString().equalsIgnoreCase("")){
            MDToast mdToast = MDToast.makeText(AddPaymet.this, "Enter End Date",2,MDToast.TYPE_ERROR);
            mdToast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            mdToast.show();
        }

        else{
            AddPaymets();

        }
    }

    private void Mytoolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(intent.getStringExtra("m_name"));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void AddPaymets() {

        pd=new ProgressDialog(this);
        pd.setMessage("Adding Please wait.....");
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(AddPaymet.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST,BaseURL.ADD_AMOUNT ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        //Toast.makeText(AddPaymet.this,response,Toast.LENGTH_LONG).show();
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
                params.put("u_id", Objects.requireNonNull(user.get(SessionManager.KEY_U_ID)));
                params.put("m_id",intent.getStringExtra("m_id"));
                params.put("title", title.getText().toString());
                params.put("total_amount", amount.getText().toString());
                params.put("end_date", enddate.getText().toString());
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
                title.getText().clear();
                amount.getText().clear();

                MDToast mdToast = MDToast.makeText(AddPaymet.this, message, 5, MDToast.TYPE_SUCCESS);
                mdToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                mdToast.show();
            }
            else if(error.equalsIgnoreCase("true")) {
                title.getText().clear();
                amount.getText().clear();
                MDToast mdToast = MDToast.makeText(AddPaymet.this, message, 5, MDToast.TYPE_ERROR);
                mdToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                mdToast.show();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
