package com.cn.chinamobile.pojo.mybatis;

public class Vendor {
    private Integer id;

    private String vendor;

    private String abridge;

    public Vendor(Integer id, String vendor, String abridge) {
        this.id = id;
        this.vendor = vendor;
        this.abridge = abridge;
    }

    public Vendor() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getAbridge() {
        return abridge;
    }

    public void setAbridge(String abridge) {
        this.abridge = abridge;
    }
}