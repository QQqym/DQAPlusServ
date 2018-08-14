package com.cn.chinamobile.pojo.mybatis;

public class Province {
    private Double id;

    private String fullname;

    private String cabbreviation;

    private String englishname;

    private Double pid;

    public Province(Double id, String fullname, String cabbreviation, String englishname, Double pid) {
        this.id = id;
        this.fullname = fullname;
        this.cabbreviation = cabbreviation;
        this.englishname = englishname;
        this.pid = pid;
    }

    public Province() {
        super();
    }

    public Double getId() {
        return id;
    }

    public void setId(Double id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCabbreviation() {
        return cabbreviation;
    }

    public void setCabbreviation(String cabbreviation) {
        this.cabbreviation = cabbreviation;
    }

    public String getEnglishname() {
        return englishname;
    }

    public void setEnglishname(String englishname) {
        this.englishname = englishname;
    }

    public Double getPid() {
        return pid;
    }

    public void setPid(Double pid) {
        this.pid = pid;
    }
}