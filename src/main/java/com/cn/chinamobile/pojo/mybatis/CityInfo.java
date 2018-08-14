package com.cn.chinamobile.pojo.mybatis;

public class CityInfo {
    private Double id;

    private String provincename;

    private String cityname;

    private String englishname;

    public CityInfo(Double id, String provincename, String cityname, String englishname) {
        this.id = id;
        this.provincename = provincename;
        this.cityname = cityname;
        this.englishname = englishname;
    }

    public CityInfo() {
        super();
    }

    public Double getId() {
        return id;
    }

    public void setId(Double id) {
        this.id = id;
    }

    public String getProvincename() {
        return provincename;
    }

    public void setProvincename(String provincename) {
        this.provincename = provincename;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getEnglishname() {
        return englishname;
    }

    public void setEnglishname(String englishname) {
        this.englishname = englishname;
    }
}