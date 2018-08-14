package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.UdpLoseDatatype;

public interface UdpLoseDatatypeMapper {
    int insert(UdpLoseDatatype record);

    int insertSelective(UdpLoseDatatype record);

    void deletebytaskid(int taskid);
}