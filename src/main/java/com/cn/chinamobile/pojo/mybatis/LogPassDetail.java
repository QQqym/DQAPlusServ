package com.cn.chinamobile.pojo.mybatis;

public class LogPassDetail {
    private Integer taskid;

    private String cityname;

    private String datatype;

    private String swversion;

    private String pnname;

    private Integer passcount;

    private Integer totalcount;

    public LogPassDetail(Integer taskid, String cityname,String datatype, String swversion, String pnname, Integer passcount, Integer totalcount) {
        this.taskid = taskid;
        this.cityname = cityname;
        this.datatype = datatype;
        this.swversion = swversion;
        this.pnname = pnname;
        this.passcount = passcount;
        this.totalcount = totalcount;
    }

    public LogPassDetail() {
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

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getSwversion() {
        return swversion;
    }

    public void setSwversion(String swversion) {
        this.swversion = swversion;
    }

    public String getPnname() {
        return pnname;
    }

    public void setPnname(String pnname) {
        this.pnname = pnname;
    }

    public Integer getPasscount() {
        return passcount;
    }

    public void setPasscount(Integer passcount) {
        this.passcount = passcount;
    }

    public Integer getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(Integer totalcount) {
        this.totalcount = totalcount;
    }
}