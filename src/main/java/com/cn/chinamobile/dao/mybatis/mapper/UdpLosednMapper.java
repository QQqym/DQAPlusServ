package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.UdpLosedn;

import java.util.List;

public interface UdpLosednMapper {
    int insert(UdpLosedn record);

    int insertSelective(UdpLosedn record);

    void deletebytaskid(int taskid);

    void batchinsert(List<UdpLosedn> list);
}