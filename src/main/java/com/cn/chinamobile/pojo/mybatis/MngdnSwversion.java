package com.cn.chinamobile.pojo.mybatis;

public class MngdnSwversion {
    private String province;

    private String vendor;

    private String netype;

    private String ver;

    private String dn;

    private String swversion;

    public MngdnSwversion(String province, String vendor, String netype, String ver, String dn, String swversion) {
        this.province = province;
        this.vendor = vendor;
        this.netype = netype;
        this.ver = ver;
        this.dn = dn;
        this.swversion = swversion;
    }

    public MngdnSwversion() {
        super();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getNetype() {
        return netype;
    }

    public void setNetype(String netype) {
        this.netype = netype;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getSwversion() {
        return swversion;
    }

    public void setSwversion(String swversion) {
        this.swversion = swversion;
    }
}