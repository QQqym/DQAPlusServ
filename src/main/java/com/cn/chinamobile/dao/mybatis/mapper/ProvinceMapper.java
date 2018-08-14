package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.Province;

import java.util.List;

public interface ProvinceMapper {
    List<Province> selectAll();

    int insert(Province record);

    int insertSelective(Province record);

    Province selectByPrimaryKey(Double id);

    int updateByPrimaryKeySelective(Province record);

    int updateByPrimaryKey(Province record);
}