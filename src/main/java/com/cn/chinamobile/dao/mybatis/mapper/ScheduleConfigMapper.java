package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.ScheduleConfig;

import java.util.List;

public interface ScheduleConfigMapper {

    List<ScheduleConfig> selectAll();

    int insert(ScheduleConfig record);

    int insertSelective(ScheduleConfig record);

    ScheduleConfig selectByPrimaryKey(Integer intid);

    int updateByPrimaryKeySelective(ScheduleConfig record);

    int updateByPrimaryKey(ScheduleConfig record);
}