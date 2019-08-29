package com.casualweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.casualweather.android.R;
import com.casualweather.android.gson.Forecast;
import com.casualweather.android.gson.Weather;
import com.casualweather.android.service.AutoUpdateService;
import com.casualweather.android.util.HttpUtil;
import com.casualweather.android.util.Utility;

import java.io.IOException;
import java.io.Writer;
import java.util.zip.Inflater;

import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;

    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefresh;//下拉刷新
    private String mWeatherId;

    public DrawerLayout drawerLayout;
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (Build.VERSION.SDK_INT>=21){//安卓5.0以上则执行状态栏透明
//            View decorView=getWindow().getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
        setContentView(R.layout.activity_weather);

        HeConfig.init("HE1908281458301612", "909d069adc2a4494826396f829d888dc");
        HeConfig.switchToFreeServerNode();

        weatherLayout=(ScrollView)findViewById(R.id.weather_layout);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_time);
        degreeText=(TextView)findViewById(R.id.degree_text);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        aqiText=(TextView)findViewById(R.id.aqi_text);
        pm25Text=(TextView)findViewById(R.id.pm25_text);
        comfortText=(TextView)findViewById(R.id.comfort_text);
        carWashText=(TextView)findViewById(R.id.car_wash_text);
        sportText=(TextView)findViewById(R.id.sport_text);
        bingPicImg=(ImageView)findViewById(R.id.bing_pic_img);//背景图
        swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);//下拉刷新
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);//刷新时的颜色
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);//侧滑菜单栏布局
        navButton=(Button)findViewById(R.id.nav_button); //home键按钮

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);//点击home键打开侧滑菜单栏
            }
        });


        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);//设置本地缓存
        String weatherString=prefs.getString("weather",null);//获取缓存中的信息
        if (weatherString!=null){//如果存在缓存信息，直接解析天气并显示
            Weather weather= Utility.handleWeatherResponse(weatherString);
            mWeatherId=weather.basic.weatherId;
            showWeatherInfo(weather);
        }else {//无缓存信息则查询服务器
            mWeatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        //下拉刷新
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        //加载背景图片
        String bingPic=prefs.getString("bing_pic",null);
        if (bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else {
            loadBingPic();
        }

    }

    public void requestWeather(final String weatherId){
        //过期接口！！！需要重写！！
        String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=909d069adc2a4494826396f829d888dc";

        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                final String responseText=response.body().string();

                final Weather weather=Utility.handleWeatherResponse(responseText);//解析天气信息
                runOnUiThread(new Runnable() {//切换到主线程进行UI操作
                    @Override
                    public void run() {
                        if (weather!=null&&"ok".equals(weather.status)){
                            //获取缓存
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            mWeatherId=weather.basic.weatherId;
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败，请再试一次吧！", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);//刷新结束
                    }
                });
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "服务器未响应，获取天气信息失败！", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);//刷新结束
                    }
                });
            }
        });

        loadBingPic();

    }

    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

        });
    }

    private void showWeatherInfo(Weather weather){
        String cityName=weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime.split(" ")[1];//切割字符串获取时间
        String degree=weather.now.temperature+"℃";
        String weatherInfo=weather.now.more.info;

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        forecastLayout.removeAllViews();//将视图清空，显示未来几天的天气布局
        for (Forecast forecast:weather.forecastList){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView)view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);

            dateText.setText(forecast.date); //日期
            infoText.setText(forecast.more.info);//天气概括
            maxText.setText(forecast.temperature.max);//最高气温
            minText.setText(forecast.temperature.min);//最低气温

            forecastLayout.addView(view);
        }
        if (weather.aqi!=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        String comfort="舒适度："+weather.suggestion.comfort.info;
        String carWash="洗车建议："+weather.suggestion.carWash.info;
        String sport="运动建议："+weather.suggestion.sport.info;

        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);

        weatherLayout.setVisibility(View.VISIBLE);

        //开启定时更新天气服务
        Intent intent=new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

}
