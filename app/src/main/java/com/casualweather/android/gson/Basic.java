package com.casualweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {

    //使用@SerializedName注解让JSON字段和Java字段之间建立连接
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
