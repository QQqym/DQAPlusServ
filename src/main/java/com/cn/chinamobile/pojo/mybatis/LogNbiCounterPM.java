package com.cn.chinamobile.pojo.mybatis;

import org.springframework.util.StringUtils;

/**
 * 北向与omc对比的PM结果实体类
 * 加一个表名的字段，统一处理
 */
public class LogNbiCounterPM {
    private Integer intid;

    private String province;

    private Integer taskid;

    private String city;

    private String timestamp;

    private String timezone;

    private String period;

    private String vendorname;

    private String elementtype;

    private String pmversion;

    private String rmuid;

    private String dn;

    private String userlabel;

    private String starttime;

    private String objecttype;

    private String pmname;

    private Double bxvalue;

    private Double omcvalue;

    private Double diffvalue;

    private String tablename;

    private  String swVersion;

    public LogNbiCounterPM(String[] values,int taskid,String parsetime) {
        this.intid = null;
        this.province = values[1];
        this.city = values[2];
        this.timestamp = parsetime;
        this.timezone = values[4];
        this.period = values[5];
        this.vendorname = values[6];
        this.elementtype = values[7];
        this.pmversion = values[8];
        this.rmuid = values[9];
        this.dn = values[10];
        this.userlabel = values[11];
        this.starttime = parsetime;
        this.objecttype = values[13];
        this.pmname = values[14];
        this.bxvalue = StringUtils.isEmpty(values[15])||values[15].equals("null")?null:Double.parseDouble(values[15]);
        this.omcvalue = StringUtils.isEmpty(values[16])||values[16].equals("null")?null:Double.parseDouble(values[16]);
        this.diffvalue = Double.parseDouble(values[17]);
        this.taskid = taskid;
        if(values.length==19)
            swVersion = values[18];
    }

    public LogNbiCounterPM() {
        super();
    }

    public Integer getIntid() {
        return intid;
    }

    public void setIntid(Integer intid) {
        this.intid = intid;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getVendorname() {
        return vendorname;
    }

    public void setVendorname(String vendorname) {
        this.vendorname = vendorname;
    }

    public String getElementtype() {
        return elementtype;
    }

    public void setElementtype(String elementtype) {
        this.elementtype = elementtype;
    }

    public String getPmversion() {
        return pmversion;
    }

    public void setPmversion(String pmversion) {
        this.pmversion = pmversion;
    }

    public String getRmuid() {
        return rmuid;
    }

    public void setRmuid(String rmuid) {
        this.rmuid = rmuid;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getUserlabel() {
        return userlabel;
    }

    public void setUserlabel(String userlabel) {
        this.userlabel = userlabel;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getObjecttype() {
        return objecttype;
    }

    public void setObjecttype(String objecttype) {
        this.objecttype = objecttype;
    }

    public String getPmname() {
        return pmname;
    }

    public void setPmname(String pmname) {
        this.pmname = pmname;
    }

    public Double getBxvalue() {
        return bxvalue;
    }

    public void setBxvalue(Double bxvalue) {
        this.bxvalue = bxvalue;
    }

    public Double getOmcvalue() {
        return omcvalue;
    }

    public void setOmcvalue(Double omcvalue) {
        this.omcvalue = omcvalue;
    }

    public Double getDiffvalue() {
        return diffvalue;
    }

    public void setDiffvalue(Double diffvalue) {
        this.diffvalue = diffvalue;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getSwVersion() {
        return swVersion;
    }

    public void setSwVersion(String swVersion) {
        this.swVersion = swVersion;
    }
}