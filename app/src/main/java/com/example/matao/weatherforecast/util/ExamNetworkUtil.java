package com.example.matao.weatherforecast.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 检查手机网络
 * Created by matao on 2018/6/29.
 */

public class ExamNetworkUtil {

    public static final int NET_NONE = 0;
    public static final int NET_WIFI = 1;
    public static final int NET_MOBILE = 2;


    public static int getNetState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null) {
            return NET_NONE;
        }
        int type = networkInfo.getType();
        if(type == ConnectivityManager.TYPE_MOBILE) {
            return NET_MOBILE;
        } else if (type == ConnectivityManager.TYPE_WIFI) {
            return NET_WIFI;
        }
        return NET_MOBILE;
    }




















}
