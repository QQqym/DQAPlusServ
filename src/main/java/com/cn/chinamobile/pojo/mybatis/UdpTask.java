package com.cn.chinamobile.pojo.mybatis;

public class UdpTask implements Cloneable{

    private Integer intid;

    private String province;

    private String netype;

    private String vendor;

    private String version;

    private String datetime;

    private String type;

    private String taskstatus;

    private String failurereason;

    public UdpTask(Integer intid, String province, String netype, String vendor, String version, String datetime, String type, String taskstatus, String failurereason) {
        this.intid = intid;
        this.province = province;
        this.netype = netype;
        this.vendor = vendor;
        this.version = version;
        this.datetime = datetime;
        this.type = type;
        this.taskstatus = taskstatus;
        this.failurereason = failurereason;
    }

    /**
     * 通过时间和参数生成实例
     * @param datetime 时间 yyyy-mm-dd-hh
     * @param args 参数 省份、网元类型、厂家、类型（NRM/PM）、版本
     */
    public UdpTask(String datetime,String...args){
        this.province = args[0];
        this.netype = args[1];
        this.vendor = args[2];
        this.version = args[4];
        this.datetime = datetime;
        this.type = args[3];
    }

    public UdpTask() {
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

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTaskstatus() {
        return taskstatus;
    }

    public void setTaskstatus(String taskstatus) {
        this.taskstatus = taskstatus;
    }

    public String getFailurereason() {
        return failurereason;
    }

    public void setFailurereason(String failurereason) {
        this.failurereason = failurereason;
    }

    public UdpTask clone(){
        UdpTask o = null;
        try {
            o = (UdpTask) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
}