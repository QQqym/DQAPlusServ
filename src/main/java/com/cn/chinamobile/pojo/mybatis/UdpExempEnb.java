package com.cn.chinamobile.pojo.mybatis;

public class UdpExempEnb {
    private String vendor;

    private String type;

    private String version;

    private String swversion;

    private String datatype;

    private String pmname;

    private String grade;

    private String excetype;

    private String excereasion;

    public UdpExempEnb(String vendor, String type, String version, String swversion, String datatype, String pmname, String grade, String excetype, String excereasion) {
        this.vendor = vendor;
        this.type = type;
        this.version = version;
        this.swversion = swversion;
        this.datatype = datatype;
        this.pmname = pmname;
        this.grade = grade;
        this.excetype = excetype;
        this.excereasion = excereasion;
    }

    public UdpExempEnb() {
        super();
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public String getExcetype() {
        return excetype;
    }

    public void setExcetype(String excetype) {
        this.excetype = excetype;
    }

    public String getExcereasion() {
        return excereasion;
    }

    public void setExcereasion(String excereasion) {
        this.excereasion = excereasion;
    }
}