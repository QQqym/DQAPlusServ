package com.cn.chinamobile.pojo.mybatis;

public class ScheduleConfig {
    private Integer intid;

    private String province;

    private String netype;

    private String vendor;

    private String version;

    private String datatype;

    private String scheduletime;

    private String compare;

    public ScheduleConfig(Integer intid, String province, String netype, String vendor, String version, String datatype, String scheduletime,String compare) {
        this.intid = intid;
        this.province = province;
        this.netype = netype;
        this.vendor = vendor;
        this.version = version;
        this.datatype = datatype;
        this.scheduletime = scheduletime;
        this.compare = compare;
    }

    public ScheduleConfig() {
        super();
    }

    public Integer getIntid() {
        return intid;
    }

    public void setIntid(Integer intid) {
        this.intid = intid;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getNetype() {
        return netype;
    }

    public void setNetype(String netype) {
        this.netype = netype;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getScheduletime() {
        return scheduletime;
    }

    public void setScheduletime(String scheduletime) {
        this.scheduletime = scheduletime;
    }

    public String getCompare() {
        return compare;
    }

    public void setCompare(String compare) {
        this.compare = compare;
    }
}