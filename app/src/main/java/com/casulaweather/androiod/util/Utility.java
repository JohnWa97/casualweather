package com.casulaweather.androiod.util;

import android.text.TextUtils;
import android.util.Log;

import com.casulaweather.androiod.db.City;
import com.casulaweather.androiod.db.County;
import com.casulaweather.androiod.db.Province;

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

}
