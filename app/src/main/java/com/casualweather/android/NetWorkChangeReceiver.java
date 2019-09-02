package com.casualweather.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetWorkChangeReceiver extends BroadcastReceiver {
   // public boolean networkIsAvailable=false;
    @Override
    public void onReceive(Context context, Intent intent) {
        //网络管理类
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();//(需添加网络访问权限)
        if (networkInfo!=null&&networkInfo.isAvailable()){
            Toast.makeText(context, "网络已连接", Toast.LENGTH_SHORT).show();
            // networkIsAvailable=true;
        }else{
            Toast.makeText(context, "喵了个喵，网络不可用", Toast.LENGTH_SHORT).show();
          //  networkIsAvailable=false;
        }
    }

    //有网返回true没网返回false
    public  boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

}
