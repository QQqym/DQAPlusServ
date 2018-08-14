package com.cn.chinamobile.dao.mybatis.mapper;


import com.cn.chinamobile.pojo.mybatis.CounterpmEnb;
import com.cn.chinamobile.pojo.mybatis.EnbCounterPm;
import com.cn.chinamobile.pojo.mybatis.VenderInformation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CounterpmEnbMapper {
    int deleteByPrimaryKey(Integer pm1);

    int insert(CounterpmEnb record);

    int insertSelective(CounterpmEnb record);

    CounterpmEnb selectByPrimaryKey(Integer pm1);

    int updateByPrimaryKeySelective(CounterpmEnb record);

    int updateByPrimaryKey(CounterpmEnb record);

    List<VenderInformation> selectCounterVenderInformation();

    List<String> selectCounterUserlabels(@Param("pm2") String pm2, @Param("pm7") String pm7, @Param("pm8") String pm8, @Param("pm9") String pm9);

    EnbCounterPm selectSumCOUNTERByProAndDate(@Param("pm4") String pm4, @Param("pm2") String pm2);

    EnbCounterPm selectSumCOUNTERByProAndDateFromTmp(@Param("pm4") String pm4, @Param("pm2") String pm2,@Param("tableName") String tableName);

    List<EnbCounterPm> selectCommonCOUNTERByProAndDate(@Param("pm4") String pm4,@Param("pm2") String pm2);

    void createCounterTmpTabel(@Param("sql") String sql);

}