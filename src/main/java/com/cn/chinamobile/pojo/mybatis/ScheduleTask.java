package com.cn.chinamobile.pojo.mybatis;

public class ScheduleTask {
    private Integer intid;

    private String province;

    private String netype;

    private String vendor;

    private String version;

    private String datatype;

    private String datetime;

    private Integer executetime;

    private String taskstatus;

    public ScheduleTask(Integer intid, String province, String netype, String vendor, String version, String datatype, String datetime, Integer executetime, String taskstatus) {
        this.intid = intid;
        this.province = province;
        this.netype = netype;
        this.vendor = vendor;
        this.version = version;
        this.datatype = datatype;
        this.datetime = datetime;
        this.executetime = executetime;
        this.taskstatus = taskstatus;
    }

    /**
     * 构建任务任务，首次正在执行
     * @param datetime 执行时间
     * @param args 参数，顺序为 省份、网元、厂家、版本、数据类型
     */
    public ScheduleTask(String datetime,String... args) {
        this.province = args[0];
        this.netype = args[1];
        this.vendor = args[2];
        this.version = args[3];
        this.datatype = args[4];
        this.datetime = datetime;
        this.executetime = 0;
        this.taskstatus = "1";
    }

    public ScheduleTask() {
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

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Integer getExecutetime() {
        return executetime;
    }

    public void setExecutetime(Integer executetime) {
        this.executetime = executetime;
    }

    public String getTaskstatus() {
        return taskstatus;
    }

    public void setTaskstatus(String taskstatus) {
        this.taskstatus = taskstatus;
    }
}