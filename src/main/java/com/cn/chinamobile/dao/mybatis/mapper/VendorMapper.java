package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.Vendor;

import java.util.List;

public interface VendorMapper {
    List<Vendor> selectAll();

    int insert(Vendor record);

    int insertSelective(Vendor record);
}