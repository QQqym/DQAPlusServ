package com.cn.chinamobile.pojo.mybatis;

public class UdpAll {
    private String province;

    private String filetype;

    private String netype;

    private String vendor;

    private String version;

    private String type;

    private String datetime;

    private String datatype;

    private String pctlose;

    public UdpAll(String province, String netype, String vendor, String version, String type) {
        this.province = province;
        this.netype = netype;
        this.vendor = vendor;
        this.version = version;
        this.type = type;
    }

    public UdpAll(String province, String filetype, String netype, String vendor, String version, String type, String datetime, String datatype, String pctlose) {
        this.province = province;
        this.filetype = filetype;
        this.netype = netype;
        this.vendor = vendor;
        this.version = version;
        this.type = type;
        this.datetime = datetime;
        this.datatype = datatype;
        this.pctlose = pctlose;
    }

    public UdpAll() {
        super();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getPctlose() {
        return pctlose;
    }

    public void setPctlose(String pctlose) {
        this.pctlose = pctlose;
    }
}