package com.cn.chinamobile.pojo.mybatis;

public class UdpGradeTotal {
    private Integer taskid;

    private String cityname;

    private String swversion;

    private String grade;

    private Integer totalnum;

    private Integer excepnum;

    public UdpGradeTotal(Integer taskid, String cityname, String swversion, String grade, Integer totalnum, Integer excepnum) {
        this.taskid = taskid;
        this.cityname = cityname;
        this.swversion = swversion;
        this.grade = grade;
        this.totalnum = totalnum;
        this.excepnum = excepnum;
    }

    public UdpGradeTotal() {
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

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Integer getTotalnum() {
        return totalnum;
    }

    public void setTotalnum(Integer totalnum) {
        this.totalnum = totalnum;
    }

    public Integer getExcepnum() {
        return excepnum;
    }

    public void setExcepnum(Integer excepnum) {
        this.excepnum = excepnum;
    }
}