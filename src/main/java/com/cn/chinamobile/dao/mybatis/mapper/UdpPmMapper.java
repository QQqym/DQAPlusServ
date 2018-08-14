package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.UdpPm;

import java.util.List;
import java.util.Map;

public interface UdpPmMapper {
    int insert(UdpPm record);

    int insertSelective(UdpPm record);

    List<UdpPm> selectByParas(Map map);

    String getVersion(String netype);

    String getHigherVersion(Map map);
}