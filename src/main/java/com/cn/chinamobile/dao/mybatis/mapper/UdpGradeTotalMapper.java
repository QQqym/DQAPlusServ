package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.UdpGradeTotal;

public interface UdpGradeTotalMapper {
    int insert(UdpGradeTotal record);

    int insertSelective(UdpGradeTotal record);

    void deletebytaskid(int taskid);
}