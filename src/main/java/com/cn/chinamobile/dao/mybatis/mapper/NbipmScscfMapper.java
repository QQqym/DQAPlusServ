package com.cn.chinamobile.dao.mybatis.mapper;


import com.cn.chinamobile.pojo.mybatis.NbipmScscf;

public interface NbipmScscfMapper {
    int deleteByPrimaryKey(Integer pm1);

    int insert(NbipmScscf record);

    int insertSelective(NbipmScscf record);

    NbipmScscf selectByPrimaryKey(Integer pm1);

    int updateByPrimaryKeySelective(NbipmScscf record);

    int updateByPrimaryKey(NbipmScscf record);
}