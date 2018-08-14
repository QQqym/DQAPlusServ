package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.TaskDetStatus;

import java.util.Map;

public interface TaskDetStatusMapper {
    int insert(TaskDetStatus record);

    int insertSelective(TaskDetStatus record);

    TaskDetStatus selectByParas(Map map);

    int updateByTaskSubId(TaskDetStatus record);
}