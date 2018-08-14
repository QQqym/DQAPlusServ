package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.LogNbiHwwgKpi;

import java.util.Map;

public interface LogNbiHwwgKpiMapper {
    //通过任务ID删除入库的数据
    void deletebytaskid(LogNbiHwwgKpi logNbiHwwgKpi);

    int insert(LogNbiHwwgKpi record);

    int insertSelective(LogNbiHwwgKpi record);

    LogNbiHwwgKpi selectByPrimaryKey(Integer intid);

    int updateByPrimaryKeySelective(LogNbiHwwgKpi record);

    int updateByPrimaryKey(LogNbiHwwgKpi record);
}