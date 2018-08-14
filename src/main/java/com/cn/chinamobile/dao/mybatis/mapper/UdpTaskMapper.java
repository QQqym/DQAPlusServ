package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.UdpTask;

import java.util.Map;

public interface UdpTaskMapper {

    int insert(UdpTask record);

    int insertSelective(UdpTask record);

    UdpTask selectByPrimaryKey(Integer intid);

    int updateByPrimaryKeySelective(UdpTask record);

    int updateByPrimaryKey(UdpTask record);

    UdpTask selectByParas(UdpTask udpTask);
}