package com.casualweather.android.util;

import android.text.TextUtils;
import android.util.Log;

import com.casualweather.android.db.City;
import com.casualweather.android.db.County;
import com.casualweather.android.db.Province;
import com.casualweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//解析服务器返回的JSON数据
public class Utility {

    //解析省级数据
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvince=new JSONArray(response);//将返回的数据实例化为JSON数组
                for (int i=0;i<allProvince.length();i++){
                    JSONObject ProvinceObject=allProvince.getJSONObject(i);//遍历数组元素，实例化为单个省对象
                    Province province=new Province();//实例化数据库对象
                    province.setProvinceName(ProvinceObject.getString("name"));//获取省名
                    province.setProvinceCode(ProvinceObject.getInt("id"));//获取省编号
                    province.save();//将数据保存到数据库
                }
                return true;//解析成功
            }catch (Exception e){
                Log.d("Utility", "这是解析省级数据报错："+e.getMessage());
                e.printStackTrace();
            }
        }
        return false;//解析失败
    }

    //解析市级数据
    public static boolean handleCityResponse(String response,int ProvinceId){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray allCities=new JSONArray(response);
                for(int i=0;i<allCities.length();i++){
                    JSONObject CityObject=allCities.getJSONObject(i);
                    City city=new City();
                    city.setCityName(CityObject.getString("name"));
                    city.setCityCode(CityObject.getInt("id"));
                    city.setProvinceId(ProvinceId);//设置所属省份ID
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                Log.d("Utility", "这是解析市级数据报错："+e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析县级数据
    public static boolean handleCountyResponse(String response,int CityId){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray allCounties=new JSONArray(response);
                for (int i=0;i<allCounties.length();i++){
                    JSONObject CountyObject=allCounties.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(CountyObject.getString("name"));
                    county.setWeatherId(CountyObject.getString("weather_id"));
                    county.setCityId(CityId);
                    county.save();
                }
                return true;
            }catch (Exception e){
                Log.d("Utility", "这是解析县级数据报错："+e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析和天气数据
    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String weatherContent=jsonArray.getJSONObject(0).toString();//获取天气信息的主体内容
            return new Gson().fromJson(weatherContent,Weather.class);//将JSON数据转换成weather对象

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

}
