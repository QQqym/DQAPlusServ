package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.UdpNrm;

import java.util.List;
import java.util.Map;

public interface UdpNrmMapper {
    int insert(UdpNrm record);

    int insertSelective(UdpNrm record);

    List<UdpNrm> selectByParas(Map map);

    List<UdpNrm> selectLevelInfo(Map map);

    String getVersion(String netype);

    String getHigherVersion(Map map);
}