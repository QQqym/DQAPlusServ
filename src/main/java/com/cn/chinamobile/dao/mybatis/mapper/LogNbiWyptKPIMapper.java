package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.entity.BatchInfo;
import com.cn.chinamobile.pojo.mybatis.LogNbiWyptKPI;

import java.util.List;

public interface LogNbiWyptKPIMapper {
    //通过任务ID删除入库的数据
    void deletebytaskid(LogNbiWyptKPI record);

    int insert(LogNbiWyptKPI record);

    //批量入库
    void batchinsert(BatchInfo batchInfo);

    int insertSelective(LogNbiWyptKPI record);

    LogNbiWyptKPI selectByPrimaryKey(Integer intid);

    int updateByPrimaryKeySelective(LogNbiWyptKPI record);

    int updateByPrimaryKey(LogNbiWyptKPI record);
}