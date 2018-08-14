package com.cn.chinamobile.dao.mybatis.mapper;


import com.cn.chinamobile.pojo.mybatis.CounterpmScscf;

public interface CounterpmScscfMapper {
    int deleteByPrimaryKey(Integer pm1);

    int insert(CounterpmScscf record);

    int insertSelective(CounterpmScscf record);

    CounterpmScscf selectByPrimaryKey(Integer pm1);

    int updateByPrimaryKeySelective(CounterpmScscf record);

    int updateByPrimaryKey(CounterpmScscf record);
}