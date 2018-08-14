package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.UdpExempDtype;

import java.util.List;
import java.util.Map;

public interface UdpExempDtypeMapper {
    int insert(UdpExempDtype record);

    int insertSelective(UdpExempDtype record);

    List<UdpExempDtype> selectByParas(Map map);
}