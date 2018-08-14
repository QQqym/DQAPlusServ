package com.cn.chinamobile.pojo.mybatis;

public class AlgMapping {
    private String netype;

    private String version;

    private String vendor;

    private String swversion;

    private String omcversion;

    private String algversion;

    public AlgMapping(String netype, String version, String vendor, String swversion, String omcversion, String algversion) {
        this.netype = netype;
        this.version = version;
        this.vendor = vendor;
        this.swversion = swversion;
        this.omcversion = omcversion;
        this.algversion = algversion;
    }

    public AlgMapping() {
        super();
    }

    public String getNetype() {
        return netype;
    }

    public void setNetype(String netype) {
        this.netype = netype;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getSwversion() {
        return swversion;
    }

    public void setSwversion(String swversion) {
        this.swversion = swversion;
    }

    public String getOmcversion() {
        return omcversion;
    }

    public void setOmcversion(String omcversion) {
        this.omcversion = omcversion;
    }

    public String getAlgversion() {
        return algversion;
    }

    public void setAlgversion(String algversion) {
        this.algversion = algversion;
    }
}