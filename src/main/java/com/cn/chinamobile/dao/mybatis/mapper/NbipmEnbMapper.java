package com.cn.chinamobile.dao.mybatis.mapper;


import com.cn.chinamobile.pojo.mybatis.EnbNbiPm;
import com.cn.chinamobile.pojo.mybatis.NbipmEnb;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NbipmEnbMapper {
    int deleteByPrimaryKey(Integer pm1);

    int insert(NbipmEnb record);

    int insertSelective(NbipmEnb record);

    NbipmEnb selectByPrimaryKey(Integer pm1);

    int updateByPrimaryKeySelective(NbipmEnb record);

    int updateByPrimaryKey(NbipmEnb record);

    EnbNbiPm selectSumNBIByProAndDate(@Param("pm4") String pm4,@Param("pm2") String pm2);

    EnbNbiPm selectSumNBIByProAndDateFromTmp(@Param("pm4") String pm4,@Param("pm2") String pm2,@Param("tableName") String tableName);

    List<EnbNbiPm> selectCommonNBIByProAndDate(@Param("pm4") String pm4,@Param("pm2") String pm2);

    void createNbiTmpTabel(@Param("sql") String sql);
}