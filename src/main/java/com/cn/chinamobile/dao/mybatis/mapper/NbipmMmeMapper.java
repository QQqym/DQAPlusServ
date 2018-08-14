package com.cn.chinamobile.dao.mybatis.mapper;


import com.cn.chinamobile.pojo.mybatis.NbipmMme;

public interface NbipmMmeMapper {
    int deleteByPrimaryKey(Integer pm1);

    int insert(NbipmMme record);

    int insertSelective(NbipmMme record);

    NbipmMme selectByPrimaryKey(Integer pm1);

    int updateByPrimaryKeySelective(NbipmMme record);

    int updateByPrimaryKey(NbipmMme record);
}