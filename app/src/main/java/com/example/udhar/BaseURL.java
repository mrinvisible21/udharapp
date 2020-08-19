package com.example.udhar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class BaseURL {


    public static final String MY_KEY="08ee418395cb6e2c8df58eb949feaadc";

    private static String BASE_PATH = "https://udhar.000webhostapp.com/";
    //private static String BASE_PATH = "http://192.168.1.15/";


    private static final String FOLDER= BASE_PATH + "udhar/";

    //admin modules urls
    public static final String ADD_USER= FOLDER+ "RegisterUser.php";
    public static final String USER_LOGIN= FOLDER+ "UserLogin.php";
    public static final String GET_HOME_DATA= FOLDER+ "HomeData.php";
    public static final String ADD_MEMBER= FOLDER+ "RegisterMember.php";
    public static final String GET_MEMBERS= FOLDER+ "Members.php";
    public static final String GET_PENDING_MEMBER= FOLDER+ "PendingMember.php";
    public static final String GET_HOME_PAYMENT= FOLDER+ "HomePayments.php";
    public static final String GET_MEMBER_PAYMENTS= FOLDER+ "MemberPayments.php";
    public static final String ADD_AMOUNT= FOLDER+ "AddAmount.php";
    public static final String UPDATE_AMOUNT= FOLDER+ "MemberPaidAmount.php";
    public static final String PAYMENT_DETAILS= FOLDER+ "PaymentDetails.php";
    public static final String PARTIAL_PAYMENT= FOLDER+ "PartialPayment.php";

    static boolean isOnline(Context applicationContext) {

        ConnectivityManager cm = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }

}
