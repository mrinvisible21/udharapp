package com.example.udhar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.example.udhar.AllPendingPayments.PendingPaymentAdapter;
import com.example.udhar.AllPendingPayments.PendingPayments;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ViewDetails extends AppCompatActivity {

    SwipeRefreshLayout swipeLayout;
    ProgressDialog pd;
    SessionManager sessionManager;
    HashMap<String, String> user;
    Intent intent;
    Button update;
    private Context context=this;
    TextView member_name,member_mobile,member_address,title,total_amount,
    paid_amount,remaining_amount,start_date,end_date,paid_date,status;
    String ids,u_id,m_id,total,paid,remainings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_details);
        intent =getIntent();
        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();


        if (sessionManager.isLoggedIn()) {

            MyToolBar();
            Init();
            MyProgressBar();
            Refesh();
            GetPaymentDetails();
        }


    }

    private void MyToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Payment Details");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void MyProgressBar() {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please wait.....");
        pd.show();
    }

    private void Refesh() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeColors(Color.RED, Color.MAGENTA, Color.GREEN);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetPaymentDetails();
                        pd.dismiss();
                        swipeLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }

    private void GetPaymentDetails() {

        RequestQueue queue = Volley.newRequestQueue(ViewDetails.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, BaseURL.PAYMENT_DETAILS,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        //Toast.makeText(ViewDetails.this,response,Toast.LENGTH_LONG).show();
                        if (response.equalsIgnoreCase("No Results Found.")) {
                        //Toast.makeText(ViewDetails.this,response,Toast.LENGTH_LONG).show();
                        } else {
                            SetData(response);
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", Objects.requireNonNull(user.get(SessionManager.KEY_U_ID)));
                params.put("member_id", intent.getStringExtra("m_id"));
                params.put("payment_id", intent.getStringExtra("id"));
                params.put("token", BaseURL.MY_KEY);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private void Init() {
        member_name=(TextView)findViewById(R.id.membername);
        member_mobile=(TextView)findViewById(R.id.membermobile);
        member_address=(TextView)findViewById(R.id.memberaddress);
        title=(TextView)findViewById(R.id.title);
        total_amount=(TextView)findViewById(R.id.total_amount);
        paid_amount=(TextView)findViewById(R.id.paid_amount);
        remaining_amount=(TextView)findViewById(R.id.remaining_amount);
        start_date=(TextView)findViewById(R.id.start_date);
        end_date=(TextView)findViewById(R.id.end_date);
        paid_date=(TextView)findViewById(R.id.paid_date);
        status=(TextView)findViewById(R.id.status);
        update=(Button)findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateContent();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void SetData(String response) {

        try {
            JSONArray jsonarray = new JSONArray(response);
            JSONObject jsonobject = jsonarray.getJSONObject(0);

            member_name.setText(jsonobject.getString("m_name"));
            member_mobile.setText(jsonobject.getString("m_mobile"));
            member_address.setText(jsonobject.getString("m_address"));
            title.setText(jsonobject.getString("amount_title"));
            total_amount.setText("\u20B9 "+jsonobject.getString("total_amount"));
            paid_amount.setText("\u20B9 "+jsonobject.getString("paid_amount"));
            remaining_amount.setText("\u20B9 "+jsonobject.getString("remaining_amount"));
            start_date.setText(jsonobject.getString("start_date"));
            end_date.setText(jsonobject.getString("end_date"));
            paid_date.setText(jsonobject.getString("paid_date"));
            jsonobject.getString("paid_date");
            status.setText(jsonobject.getString("status"));
            member_mobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = member_mobile.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    Intent chooser= Intent.createChooser(intent,"Please Launch call manager");
                    startActivity(chooser);

                }
            });

            ids=jsonobject.getString("id");
            u_id=jsonobject.getString("u_id");
            m_id=jsonobject.getString("m_id");
            paid=jsonobject.getString("paid_amount");
            remainings=jsonobject.getString("remaining_amount");

            if(jsonobject.getString("status").equalsIgnoreCase("Pending")){
                update.setVisibility(View.VISIBLE);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    private void UpdateContent() {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View mView = layoutInflaterAndroid.inflate(R.layout.view_update_amount_content, null);
        android.app.AlertDialog.Builder alertDialogBuilderUserInput = new android.app.AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(mView);

        TextView membername=(TextView)mView.findViewById(R.id.membername);
        membername.setText(member_name.getText().toString());
        TextView totalamount=(TextView)mView.findViewById(R.id.view_total_amount);
        totalamount.setText(total_amount.getText().toString());
        TextView paidamount=(TextView)mView.findViewById(R.id.view_paid_amount);
        paidamount.setText(paid_amount.getText().toString());
        TextView remaining=(TextView)mView.findViewById(R.id.view_remaining_amount);
        remaining.setText(remaining_amount.getText().toString());
        final EditText paying=(EditText)mView.findViewById(R.id.view_paying_amount);
        paying.setText(remainings);

        final EditText title=(EditText)mView.findViewById(R.id.amount_note);

        alertDialogBuilderUserInput
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        UpdateAmount(ids,u_id,m_id,remainings,paying.getText().toString(),title.getText().toString());

                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                    }
                })

        ;
        android.app.AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    private void UpdateAmount(final String ids, final String u_id, final String m_id, final String remainings, final String paying, final String title) {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Updating Please wait.....");
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(ViewDetails.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST,BaseURL.UPDATE_AMOUNT ,
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
                params.put("a_id", ids);
                params.put("u_id", u_id);
                params.put("m_id", m_id);
                params.put("remaining_amount",remainings);
                params.put("paying_amount", paying);
                params.put("title", title);
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
                MDToast mdToast = MDToast.makeText(ViewDetails.this, message, 2, MDToast.TYPE_SUCCESS);
                mdToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                mdToast.show();
            }
            else if(error.equalsIgnoreCase("true")) {
                MDToast mdToast = MDToast.makeText(ViewDetails.this, message, 2, MDToast.TYPE_ERROR);
                mdToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                mdToast.show();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
