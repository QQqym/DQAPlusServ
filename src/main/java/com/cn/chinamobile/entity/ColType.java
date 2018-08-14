package com.cn.chinamobile.entity;

/**
 * Created by zh on 2017/6/13.
 */
public class ColType {
    /**
     * 数据库列表
     */
    String colName;
    /**
     * 原始列名
     */
    String origColName;
    /**
     * 列名数据类型
     */
    Class cls;

    public ColType(String colName, String origColName) {
        this.colName = colName;
        this.origColName = origColName;
    }

    public ColType(String colName, String origColName, Class cls) {
        this.colName = colName;
        this.origColName = origColName;
        this.cls = cls;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getOrigColName() {
        return origColName;
    }

    public void setOrigColName(String origColName) {
        this.origColName = origColName;
    }

    public Class getCls() {
        return cls;
    }

    public void setCls(Class cls) {
        this.cls = cls;
    }
}
