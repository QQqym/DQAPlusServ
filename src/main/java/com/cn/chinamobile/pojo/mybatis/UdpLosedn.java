package com.cn.chinamobile.pojo.mybatis;

public class UdpLosedn {
    private Integer taskid;

    private String datatype;

    private String filename;

    private String dn;

    public UdpLosedn(Integer taskid, String datatype, String filename, String dn) {
        this.taskid = taskid;
        this.datatype = datatype;
        this.filename = filename;
        this.dn = dn;
    }

    public UdpLosedn() {
        super();
    }

    public Integer getTaskid() {
        return taskid;
    }

    public void setTaskid(Integer taskid) {
        this.taskid = taskid;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }
}