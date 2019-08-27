package com.casulaweather.androiod.db;

import org.litepal.crud.LitePalSupport;

public class County extends LitePalSupport {
    private int id;//县城类ID
    private int cityId;//所属城市ID
    private String countyName;//县城名
    private String weatherId;//天气ID

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getWeatherId() {
        return weatherId;
    }
}
