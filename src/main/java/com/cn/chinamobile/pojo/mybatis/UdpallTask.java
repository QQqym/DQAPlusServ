package com.cn.chinamobile.pojo.mybatis;

public class UdpallTask {
    private String netype;

    private String vendor;

    private String version;

    private String type;

    public UdpallTask(String netype, String vendor, String version, String type) {
        this.netype = netype;
        this.vendor = vendor;
        this.version = version;
        this.type = type;
    }

    public UdpallTask() {
        super();
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
}