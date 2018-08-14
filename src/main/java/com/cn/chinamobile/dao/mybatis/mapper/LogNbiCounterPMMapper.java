package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.entity.BatchInfo;
import com.cn.chinamobile.pojo.mybatis.LogNbiCounterPM;

import java.util.List;
import java.util.Map;

/**
 * 北向与counter对比结果的PM处理
 */
public interface LogNbiCounterPMMapper {
    //通过任务ID删除入库的数据
    void deletebytaskid(LogNbiCounterPM record);

    //批量入库
    void batchinsert(BatchInfo batchInfo);

    //ENB批量入库
    void batchenbinsert(BatchInfo batchInfo);

    int insert(LogNbiCounterPM record);

    int insertSelective(LogNbiCounterPM record);

    LogNbiCounterPM selectByPrimaryKey(Integer intid);

    int updateByPrimaryKeySelective(LogNbiCounterPM record);

    int updateByPrimaryKey(LogNbiCounterPM record);
}