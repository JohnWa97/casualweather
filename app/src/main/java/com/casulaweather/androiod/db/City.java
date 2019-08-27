package com.casulaweather.androiod.db;

import org.litepal.crud.LitePalSupport;

public class City extends LitePalSupport {
    private int id; //城市类ID
    private String cityName;//城市名
    private int cityCode;//城市编号
    private int provinceId;//所属省ID号

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getProvinceId() {
        return provinceId;
    }
}
