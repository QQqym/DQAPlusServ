package com.cn.chinamobile.entity;

import java.util.List;

/**
 * 批量入库实体类，为了传表名和数据不同的类型构建
 * @author xueweixia
 */
public class BatchInfo {
    private String tablename;
    private List list;

    public BatchInfo(String tablename,List datalist){
        this.tablename = tablename;
        this.list = datalist;
    }
}
