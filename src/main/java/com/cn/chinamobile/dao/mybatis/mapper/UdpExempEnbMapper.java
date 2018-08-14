package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.UdpExempEnb;

import java.util.List;
import java.util.Map;

public interface UdpExempEnbMapper {
    int insert(UdpExempEnb record);

    int insertSelective(UdpExempEnb record);

    List<UdpExempEnb> selectByParas(Map map);
}