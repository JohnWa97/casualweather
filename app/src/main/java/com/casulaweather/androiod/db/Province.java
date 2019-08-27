package com.casulaweather.androiod.db;

import org.litepal.crud.LitePalSupport;

public class Province extends LitePalSupport {
    private int id;//省类ID
    private String provinceName;//省名
    private int provinceCode;//该省编号

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

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public int getProvinceCode() {
        return provinceCode;
    }
}
