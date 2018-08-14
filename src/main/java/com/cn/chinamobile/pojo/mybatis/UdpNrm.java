package com.cn.chinamobile.pojo.mybatis;

public class UdpNrm {
    private String neType;

    private String ver;

    private String managedClass;

    private String lastClass;

    private String nrmGrade;

    private String nrmnameEn;

    private String datatype;

    private String nrmDefinition;

    public UdpNrm(String neType, String ver, String managedClass, String lastClass, String nrmGrade, String nrmnameEn, String datatype, String nrmDefinition) {
        this.neType = neType;
        this.ver = ver;
        this.managedClass = managedClass;
        this.lastClass = lastClass;
        this.nrmGrade = nrmGrade;
        this.nrmnameEn = nrmnameEn;
        this.datatype = datatype;
        this.nrmDefinition = nrmDefinition;
    }

    public UdpNrm(String managedClass, String lastClass) {
        this.managedClass = managedClass;
        this.lastClass = lastClass;
    }

    public UdpNrm() {
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

    public String getManagedClass() {
        return managedClass;
    }

    public void setManagedClass(String managedClass) {
        this.managedClass = managedClass;
    }

    public String getLastClass() {
        return lastClass;
    }

    public void setLastClass(String lastClass) {
        this.lastClass = lastClass;
    }

    public String getNrmGrade() {
        return nrmGrade;
    }

    public void setNrmGrade(String nrmGrade) {
        this.nrmGrade = nrmGrade;
    }

    public String getNrmnameEn() {
        return nrmnameEn;
    }

    public void setNrmnameEn(String nrmnameEn) {
        this.nrmnameEn = nrmnameEn;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getNrmDefinition() {
        return nrmDefinition;
    }

    public void setNrmDefinition(String nrmDefinition) {
        this.nrmDefinition = nrmDefinition;
    }
}