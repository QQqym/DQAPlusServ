package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.UdpExeSet;

import java.util.List;

public interface UdpExeSetMapper {
    int insert(UdpExeSet record);

    int insertSelective(UdpExeSet record);

    UdpExeSet selectByPrimaryKey(Integer intid);

    int updateByPrimaryKeySelective(UdpExeSet record);

    int updateByPrimaryKey(UdpExeSet record);

    List<UdpExeSet> selectByNetype(String netype);
}