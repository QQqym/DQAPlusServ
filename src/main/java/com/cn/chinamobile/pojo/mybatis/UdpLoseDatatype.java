package com.cn.chinamobile.pojo.mybatis;

public class UdpLoseDatatype {
    private Integer taskid;

    private String province;

    private String city;

    private String datatype;

    public UdpLoseDatatype(Integer taskid, String province, String city, String datatype) {
        this.taskid = taskid;
        this.province = province;
        this.city = city;
        this.datatype = datatype;
    }

    public UdpLoseDatatype() {
        super();
    }

    public Integer getTaskid() {
        return taskid;
    }

    public void setTaskid(Integer taskid) {
        this.taskid = taskid;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }
}