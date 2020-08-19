package com.example.udhar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.udhar.MemberPayments.MemberPayments;
import com.example.udhar.MemberPayments.MemberPaymetAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberProfile extends AppCompatActivity {

    private MemberPaymetAdapter mAdapter;
    private List<MemberPayments> cList;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeLayout;
    ProgressDialog pd;
    SessionManager sessionManager;
    HashMap<String, String> user;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_profile);
        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();
        intent =getIntent();

        if(sessionManager.isLoggedIn()) {

            MyToolBar();
            Init();
            MyRecyclerView();
            MyProgressBar();
            Refesh();
            GetData();
        }
    }



    private void MyToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Member Profile");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
    }

    private void Init() {
        ImageView imageView=(ImageView)findViewById(R.id.image);
        final TextView id=(TextView)findViewById(R.id.memberid);
        final TextView name=(TextView)findViewById(R.id.membername);
        TextView  mobile=(TextView)findViewById(R.id.membermobile);
        TextView address=(TextView)findViewById(R.id.memberaddress);
        TextView addlist=(TextView)findViewById(R.id.addamountlist);
        id.setText(intent.getStringExtra("m_id"));
        name.setText(intent.getStringExtra("m_name"));
        mobile.setText(intent.getStringExtra("m_mobile"));
        address.setText(intent.getStringExtra("m_address"));

        ColorGenerator generator=ColorGenerator.MATERIAL;
        Drawable drawable= TextDrawable.builder().buildRound(intent.getStringExtra("m_name").substring(0,1),generator.getRandomColor());
        imageView.setImageDrawable(drawable);

        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = intent.getStringExtra("m_mobile");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                Intent chooser= Intent.createChooser(intent,"Please Launch call manager");
                startActivity(chooser);

            }
        });

        addlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MemberProfile.this,AddPaymet.class);
                intent.putExtra("m_id",id.getText().toString());
                intent.putExtra("m_name",name.getText().toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void MyProgressBar() {
        pd=new ProgressDialog(this);
        pd.setMessage("Loading Please wait.....");
        pd.show();
    }

    private void Refesh() {
        swipeLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_container) ;
        swipeLayout.setColorSchemeColors(Color.RED, Color.MAGENTA, Color.GREEN);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        GetData();
                        pd.dismiss();
                        swipeLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }

    private void MyRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        cList = new ArrayList<>();
        mAdapter = new MemberPaymetAdapter(this,cList);
        recyclerView.setAdapter(mAdapter);

    }

    private void GetData() {

        RequestQueue queue = Volley.newRequestQueue(MemberProfile.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST,BaseURL.GET_MEMBER_PAYMENTS ,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        if(response.equalsIgnoreCase("No Results Found.")){
                            MDToast mdToast = MDToast.makeText(MemberProfile.this, "No result found", 2, MDToast.TYPE_WARNING);
                            mdToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                            mdToast.show();

                        }
                        else{

                            List<MemberPayments> items = new Gson().fromJson(response, new TypeToken<List<MemberPayments>>() {
                            }.getType());
                            cList.clear();
                            cList.addAll(items);
                            mAdapter.notifyDataSetChanged();
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
                params.put("user_id",user.get(SessionManager.KEY_U_ID));
                params.put("member_id",intent.getStringExtra("m_id"));
                params.put("token",BaseURL.MY_KEY);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
}
