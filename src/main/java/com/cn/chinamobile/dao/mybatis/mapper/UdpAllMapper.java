package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.UdpAll;

import java.util.List;
import java.util.Map;

public interface UdpAllMapper {
    int insert(UdpAll record);

    int insertSelective(UdpAll record);

    List<UdpAll> selectByType(Map map);

    List<UdpAll> selectDisTask(Map map);

    void  deletebyparas(Map map);
}