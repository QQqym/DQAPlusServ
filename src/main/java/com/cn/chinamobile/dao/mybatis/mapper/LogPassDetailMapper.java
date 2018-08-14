package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.LogPassDetail;

import java.util.List;

public interface LogPassDetailMapper {
    int insert(LogPassDetail record);

    int insertSelective(LogPassDetail record);

    void deletebytaskid(int taskid);

    void batchinsert(List<LogPassDetail> list);
}