package com.cn.chinamobile.pojo.mybatis;

public class LogUdp {
    private Integer taskid;

    private String cityname;

    private String swversion;

    private String datatype;

    private String pmname;

    private String grade;

    private String errortype;

    public LogUdp(Integer taskid, String cityname, String swversion, String datatype, String pmname, String grade, String errortype) {
        this.taskid = taskid;
        this.cityname = cityname;
        this.swversion = swversion;
        this.datatype = datatype;
        this.pmname = pmname;
        this.grade = grade;
        this.errortype = errortype;
    }

    public LogUdp() {
        super();
    }

    public Integer getTaskid() {
        return taskid;
    }

    public void setTaskid(Integer taskid) {
        this.taskid = taskid;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getSwversion() {
        return swversion;
    }

    public void setSwversion(String swversion) {
        this.swversion = swversion;
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

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getErrortype() {
        return errortype;
    }

    public void setErrortype(String errortype) {
        this.errortype = errortype;
    }
}