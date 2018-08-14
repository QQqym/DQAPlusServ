package com.cn.chinamobile.pojo.mybatis;

public class GlobalField {
    private String tableName;

    private String columnName;

    private String factColumnname;

    private String columnType;

    private String netype;

    private String dataType;

    public GlobalField(String tableName, String columnName, String factColumnname, String columnType, String netype,String dataType) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.factColumnname = factColumnname;
        this.columnType = columnType;
        this.netype = netype;
        this.dataType = dataType;
    }

    public GlobalField() {
        super();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getFactColumnname() {
        return factColumnname;
    }

    public void setFactColumnname(String factColumnname) {
        this.factColumnname = factColumnname;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getNetype() {
        return netype;
    }

    public void setNetype(String netype) {
        this.netype = netype;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}