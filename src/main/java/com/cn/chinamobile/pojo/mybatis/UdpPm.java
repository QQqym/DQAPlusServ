package com.cn.chinamobile.pojo.mybatis;

public class UdpPm {
    private String neType;

    private String ver;

    private String spaceGranularity;

    private String pmGrade;

    private String pmnameEn;

    private String datatype;

    private String pmDefinition;

    public UdpPm(String neType, String ver, String spaceGranularity, String pmGrade, String pmnameEn, String datatype, String pmDefinition) {
        this.neType = neType;
        this.ver = ver;
        this.spaceGranularity = spaceGranularity;
        this.pmGrade = pmGrade;
        this.pmnameEn = pmnameEn;
        this.datatype = datatype;
        this.pmDefinition = pmDefinition;
    }

    public UdpPm() {
        super();
    }

    public String getNeType() {
        return neType;
    }

    public void setNeType(String neType) {
        this.neType = neType;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getSpaceGranularity() {
        return spaceGranularity;
    }

    public void setSpaceGranularity(String spaceGranularity) {
        this.spaceGranularity = spaceGranularity;
    }

    public String getPmGrade() {
        return pmGrade;
    }

    public void setPmGrade(String pmGrade) {
        this.pmGrade = pmGrade;
    }

    public String getPmnameEn() {
        return pmnameEn;
    }

    public void setPmnameEn(String pmnameEn) {
        this.pmnameEn = pmnameEn;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getPmDefinition() {
        return pmDefinition;
    }

    public void setPmDefinition(String pmDefinition) {
        this.pmDefinition = pmDefinition;
    }
}