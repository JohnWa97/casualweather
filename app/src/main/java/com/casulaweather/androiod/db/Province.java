package com.casulaweather.androiod.db;

import org.litepal.crud.LitePalSupport;

public class Province extends LitePalSupport {
    private int id;
    private String provinceName;
    private int proviceCode;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProviceCode(int proviceCode) {
        this.proviceCode = proviceCode;
    }

    public int getProviceCode() {
        return proviceCode;
    }
}
