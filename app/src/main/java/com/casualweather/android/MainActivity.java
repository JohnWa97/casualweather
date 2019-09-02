package com.casualweather.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.casualweather.android.R;

import interfaces.heweather.com.interfacesmodule.view.HeConfig;

public class MainActivity extends AppCompatActivity {

    private IntentFilter intentFilter; //广播意图过滤器
    private NetWorkChangeReceiver netWorkChangeReceiver;//网络广播接收器
    private boolean test=true;
    public SwipeRefreshLayout swipeRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //判断是否有网络接入
        intentFilter=new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        netWorkChangeReceiver=new NetWorkChangeReceiver();
        registerReceiver(netWorkChangeReceiver,intentFilter);//注册网络广播接受器

        if (netWorkChangeReceiver.isNetworkConnected(MainActivity.this)) {
            Log.d("MainActivity","这是来自判断有网："+String.valueOf(test));
            setContentView(R.layout.activity_main);
        }else {
            test=netWorkChangeReceiver.isNetworkConnected(MainActivity.this);
            Log.d("MainActivity","这是没网false："+String.valueOf(test));
            setContentView(R.layout.activity_badnetwork);
            swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_badnetwork);
            swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (netWorkChangeReceiver.isNetworkConnected(MainActivity.this)){
                        setContentView(R.layout.activity_main);
                        swipeRefresh.setRefreshing(false);
                    }else{
                        Toast.makeText(MainActivity.this, "网络不可用，请先检查网络", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                }
            });

        }



        //先从缓存中读取数据，有则直接显示天气信息
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString("weather",null)!=null){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netWorkChangeReceiver);
    }
}
