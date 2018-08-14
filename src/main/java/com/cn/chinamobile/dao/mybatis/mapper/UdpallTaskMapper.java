package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.UdpallTask;

import java.util.List;

public interface UdpallTaskMapper {
    int insert(UdpallTask record);

    int insertSelective(UdpallTask record);

    List<UdpallTask> selectByType(String type);
}