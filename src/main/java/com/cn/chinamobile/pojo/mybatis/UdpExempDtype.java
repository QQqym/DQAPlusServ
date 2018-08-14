package com.cn.chinamobile.pojo.mybatis;

public class UdpExempDtype {
    private String netype;

    private String datatype;

    private String type;

    private String excetype;

    private String version;

    public UdpExempDtype(String netype, String datatype, String type, String excetype,String version) {
        this.netype = netype;
        this.datatype = datatype;
        this.type = type;
        this.excetype = excetype;
        this.version = version;
    }

    public UdpExempDtype() {
        super();
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExcetype() {
        return excetype;
    }

    public void setExcetype(String excetype) {
        this.excetype = excetype;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}