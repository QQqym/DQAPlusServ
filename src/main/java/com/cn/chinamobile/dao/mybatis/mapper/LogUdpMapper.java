package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.LogUdp;

import java.util.List;

public interface LogUdpMapper {
    int insert(LogUdp record);

    int insertSelective(LogUdp record);

    void deletebytaskid(int taskid);

    //批量入库
    void batchinsert(List<LogUdp> list);
}