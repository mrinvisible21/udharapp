package com.example.udhar;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.udhar.AllPendingPayments.PendingPaymentAdapter;
import com.example.udhar.AllPendingPayments.PendingPayments;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PendingMembers extends AppCompatActivity {

    private PendingPaymentAdapter mAdapter;
    private List<PendingPayments> cList;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeLayout;
    ProgressDialog pd;
    SessionManager sessionManager;
    HashMap<String, String> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending_members);
        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();


        if (sessionManager.isLoggedIn()) {

            MyToolBar();
            MyRecyclerView();
            MyProgressBar();
            Refesh();
            GetData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.clearFocus();
        searchView.setQueryHint("Type to search...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }
        });

        return true;

    }

    private void MyToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("All Pending Payments");
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
        mAdapter = new PendingPaymentAdapter(this, cList);
        recyclerView.setAdapter(mAdapter);

    }

    private void GetData() {

        RequestQueue queue = Volley.newRequestQueue(PendingMembers.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, BaseURL.GET_PENDING_MEMBER,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        if (response.equalsIgnoreCase("No Results Found.")) {
                        } else {

                            List<PendingPayments> items = new Gson().fromJson(response, new TypeToken<List<PendingPayments>>() {
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
                params.put("user_id", user.get(SessionManager.KEY_U_ID));
                params.put("token", BaseURL.MY_KEY);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
}