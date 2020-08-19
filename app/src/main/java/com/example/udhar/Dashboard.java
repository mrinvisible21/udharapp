package com.example.udhar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    SessionManager sessionManager;
    HashMap<String, String> user;
    private Context context=this;
    SwipeRefreshLayout swipeLayout;
    ProgressDialog pd;

    private PendingPaymentAdapter mAdapter;
    private List<PendingPayments> cList;
    RecyclerView recyclerViewmonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);


        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();

        if(sessionManager.isLoggedIn()){

            Mytoolbar();
            Init();
            Drawer();
            MyNavigation();
            MyProgressBar();
            GetHomeData();
            GetMonthList();
            MyRecyclerViewMonth();
            Refresh();


        }
    }

    private void Init() {
        ImageView logout=(ImageView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout();
            }
        });
    }

    private void Refresh() {
        swipeLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_container) ;
        swipeLayout.setColorSchemeColors(Color.RED, Color.MAGENTA, Color.GREEN);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                            GetHomeData();
                            GetMonthList();
                            pd.dismiss();
                        swipeLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

    }

    private void Mytoolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private void MyProgressBar() {
        pd=new ProgressDialog(this);
        pd.setMessage("Loading Please wait.....");
        pd.show();
    }

    private void Drawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawer.closeDrawer(GravityCompat.START);
        toggle.syncState();

    }

    private void MyNavigation() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0);
        navigationView.setItemIconTintList(null);

        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            applyFontToMenuItem(mi);
        }

        TextView user_name=(TextView)headerview.findViewById(R.id.user_name);
        TextView user_mobile=(TextView)headerview.findViewById(R.id.user_mobile);
        ImageView user_image=(ImageView)headerview.findViewById(R.id.user_pic);

        user_name.setText(user.get(SessionManager.KEY_U_NAME));
        user_mobile.setText(user.get(SessionManager.KEY_U_MOBILE));
        ColorGenerator generator=ColorGenerator.MATERIAL;
        Drawable drawable= TextDrawable.builder().buildRound(user.get(SessionManager.KEY_U_NAME).substring(0,1),generator.getRandomColor());
        user_image.setImageDrawable(drawable);

    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface face = Typeface.createFromAsset(getAssets(), "opensans_light.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , face), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        // mNewTitle.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, mNewTitle.length(), 0);
        mi.setTitle(mNewTitle);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_add_member) { GoToAddMember();}

        else if (id == R.id.nav_view_members) { GoToViewMember(); }

        else if (id == R.id.nav_pending_members) { GoToPendingMember(); }

        else if (id == R.id.nav_contactus) { GoToContact(); }

        else if (id == R.id.nav_about_app) { GoToAboutApp(); }


        return true;
    }

    private void GetHomeData() {

        RequestQueue queue = Volley.newRequestQueue(Dashboard.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST,BaseURL.GET_HOME_DATA ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SetHomeData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                    }
                }
        ) {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", Objects.requireNonNull(user.get(SessionManager.KEY_U_ID)));
                params.put("token",BaseURL.MY_KEY);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }

    @SuppressLint("SetTextI18n")
    private void SetHomeData(String response) {

        try {

            JSONArray jsonarray = new JSONArray(response);
            JSONObject jsonobject = jsonarray.getJSONObject(0);

            final String total_member = jsonobject.getString("total_member");
            final String total_pending = jsonobject.getString("total_pending");
            final String total_given = jsonobject.getString("total_given");
            final String total_remaining = jsonobject.getString("total_remaining");

            DecimalFormat formatter = new DecimalFormat("#,##,##,##,###");
            DecimalFormat formatter2 = new DecimalFormat("#,##,##,##,###");

            TextView totalmeber=(TextView)findViewById(R.id.totalmembers);
            TextView pendingmember=(TextView)findViewById(R.id.pendingmembers);

            TextView totalgivenamount=(TextView)findViewById(R.id.totalgivenamount);
            TextView pendingamount=(TextView)findViewById(R.id.pendingamount);

            totalmeber.setText(formatter2.format(Integer.parseInt(total_member)));
            pendingmember.setText(formatter2.format(Integer.parseInt(total_pending)));

            totalgivenamount.setText(formatter.format(Integer.parseInt(total_given)));
            pendingamount.setText(formatter.format(Integer.parseInt(total_remaining)));
        }
        catch (JSONException e) { e.printStackTrace(); }

    }

    private void Logout() {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View mView = layoutInflaterAndroid.inflate(R.layout.view_logout_content, null);
        android.app.AlertDialog.Builder alertDialogBuilderUserInput = new android.app.AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(mView);

        ImageView useri=(ImageView)mView.findViewById(R.id.userimage);
        TextView usern=(TextView)mView.findViewById(R.id.username);

        usern.setText(user.get(SessionManager.KEY_U_NAME));
        ColorGenerator generator=ColorGenerator.MATERIAL;
        Drawable drawable= TextDrawable.builder().buildRound(user.get(SessionManager.KEY_U_NAME).substring(0,1),generator.getRandomColor());
        useri.setImageDrawable(drawable);


        alertDialogBuilderUserInput
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        sessionManager.logoutUser();
                        finish();

                    }
                })
                .setNeutralButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                    }
                })

        ;
        android.app.AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    private void MyRecyclerViewMonth() {
        recyclerViewmonth = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerViewmonth.setLayoutManager(new GridLayoutManager(this, 1));
        cList = new ArrayList<>();
        mAdapter = new PendingPaymentAdapter(this,cList);
        recyclerViewmonth.setAdapter(mAdapter);

    }

    private void GetMonthList() {

        RequestQueue queue = Volley.newRequestQueue(Dashboard.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST,BaseURL.GET_HOME_PAYMENT ,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        if(response.equalsIgnoreCase("No Results Found.")){
                            MDToast mdToast = MDToast.makeText(Dashboard.this, "No list found", 2, MDToast.TYPE_INFO);
                            mdToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                            mdToast.show();
                        }
                        else{

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
                params.put("user_id", Objects.requireNonNull(user.get(SessionManager.KEY_U_ID)));
                params.put("token",BaseURL.MY_KEY);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private void GoToAddMember() {
        Intent intent=new Intent(Dashboard.this,AddMember.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void GoToViewMember() {
        Intent intent=new Intent(Dashboard.this,AllMembers.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void GoToPendingMember() {
        Intent intent=new Intent(Dashboard.this,PendingMembers.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void GoToContact() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View mView = layoutInflaterAndroid.inflate(R.layout.view_contact_content, null);
        android.app.AlertDialog.Builder alertDialogBuilderUserInput = new android.app.AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(mView);

        alertDialogBuilderUserInput
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })

        ;
        android.app.AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    private void GoToAboutApp() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View mView = layoutInflaterAndroid.inflate(R.layout.view_about_app_content, null);
        android.app.AlertDialog.Builder alertDialogBuilderUserInput = new android.app.AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(mView);


        TextView appversion=(TextView)mView.findViewById(R.id.appversion);
        appversion.setTag("App v -"+BuildConfig.VERSION_NAME);


        alertDialogBuilderUserInput
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {


                    }
                })

        ;
        android.app.AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }



}
