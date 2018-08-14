package com.cn.chinamobile.pojo.mybatis;

public class UdpExeSet {
    private Integer intid;

    private String netype;

    private String datatype;

    private String pmname;

    public UdpExeSet(Integer intid, String netype, String datatype, String pmname) {
        this.intid = intid;
        this.netype = netype;
        this.datatype = datatype;
        this.pmname = pmname;
    }

    public UdpExeSet() {
        super();
    }

    public Integer getIntid() {
        return intid;
    }

    public void setIntid(Integer intid) {
        this.intid = intid;
    }

    public String getNetype() {
        return netype;
    }

    public void setNetype(String netype) {
        this.netype = netype;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getPmname() {
        return pmname;
    }

    public void setPmname(String pmname) {
        this.pmname = pmname;
    }
}