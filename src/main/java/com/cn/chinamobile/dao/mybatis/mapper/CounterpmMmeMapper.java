package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.CounterpmMme;

public interface CounterpmMmeMapper {
    int deleteByPrimaryKey(Integer pm1);

    int insert(CounterpmMme record);

    int insertSelective(CounterpmMme record);

    CounterpmMme selectByPrimaryKey(Integer pm1);

    int updateByPrimaryKeySelective(CounterpmMme record);

    int updateByPrimaryKey(CounterpmMme record);
}