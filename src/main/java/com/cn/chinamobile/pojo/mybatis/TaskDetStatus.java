package com.cn.chinamobile.pojo.mybatis;

public class TaskDetStatus implements Cloneable{
    private Integer taskid;

    private String subtask;

    private String province;

    private String netype;

    private String vendor;

    private String version;

    private String datatype;

    private String datetime;

    private String taskstatus;

    private String commtent;

    public TaskDetStatus(Integer taskid, String subtask, String province, String netype, String vendor, String version, String datatype, String datetime, String taskstatus, String commtent) {
        this.taskid = taskid;
        this.subtask = subtask;
        this.province = province;
        this.netype = netype;
        this.vendor = vendor;
        this.version = version;
        this.datatype = datatype;
        this.datetime = datetime;
        this.taskstatus = taskstatus;
        this.commtent = commtent;
    }

    public TaskDetStatus() {
        super();
    }


    public TaskDetStatus(ScheduleTask scheduleTask){
        this.province = scheduleTask.getProvince();
        this.taskid = scheduleTask.getIntid();
        this.netype = scheduleTask.getNetype();
        this.vendor = scheduleTask.getVendor();
        this.version = scheduleTask.getVersion();
        this.datatype = scheduleTask.getDatatype();
        this.datetime = scheduleTask.getDatetime();
    }

    public Integer getTaskid() {
        return taskid;
    }

    public void setTaskid(Integer taskid) {
        this.taskid = taskid;
    }

    public String getSubtask() {
        return subtask;
    }

    public void setSubtask(String subtask) {
        this.subtask = subtask;
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

    public String getTaskstatus() {
        return taskstatus;
    }

    public void setTaskstatus(String taskstatus) {
        this.taskstatus = taskstatus;
    }

    public String getCommtent() {
        return commtent;
    }

    public void setCommtent(String commtent) {
        this.commtent = commtent;
    }

   public TaskDetStatus clone(){
       TaskDetStatus o = null;
       try {
           o = (TaskDetStatus) super.clone();
       } catch (CloneNotSupportedException e) {
           e.printStackTrace();
       }
       return o;
   }
}