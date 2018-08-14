package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.entity.BatchInfo;
import com.cn.chinamobile.pojo.mybatis.LogNbiWyptPM;

import java.util.List;

public interface LogNbiWyptPMMapper {

    //通过任务ID删除入库的数据
    void deletebytaskid(LogNbiWyptPM record);

    //批量入库
    void batchinsert(BatchInfo batchInfo);

    int insert(LogNbiWyptPM record);

    int insertSelective(LogNbiWyptPM record);

    LogNbiWyptPM selectByPrimaryKey(Integer intid);

    int updateByPrimaryKeySelective(LogNbiWyptPM record);

    int updateByPrimaryKey(LogNbiWyptPM record);
}