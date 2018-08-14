package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.ScheduleTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ScheduleTaskMapper {
    int insert(ScheduleTask record);

    int insertSelective(ScheduleTask record);

    ScheduleTask selectByParas(Map map);

    ScheduleTask selectByPrimaryKey(Integer intid);

    List<ScheduleTask> selectFailTask(Map map);

    int updateByPrimaryKeySelective(ScheduleTask record);

    int updateByPrimaryKey(ScheduleTask record);

    List<ScheduleTask> selectByDateAndStatus(@Param("datetime") String datetime);
}