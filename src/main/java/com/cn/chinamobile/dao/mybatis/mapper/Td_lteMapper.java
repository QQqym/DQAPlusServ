package com.cn.chinamobile.dao.mybatis.mapper;


import com.cn.chinamobile.pojo.mybatis.EnbCounterPm;
import com.cn.chinamobile.pojo.mybatis.EnbNbiPm;
import com.cn.chinamobile.pojo.mybatis.Td_lte;
import com.cn.chinamobile.pojo.mybatis.Td_lte_pm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface Td_lteMapper {
    int insert(Td_lte record);

    int insertSelective(Td_lte record);

    void insertByEnbCounterPm(@Param("ecp")EnbCounterPm ecp,@Param("dataStatus") String dataStatus);

    void insertByEnbNbiPm(@Param("enp")EnbNbiPm enp,@Param("dataStatus") String dataStatus);

    void insertByTdLtePm(@Param("tlp")List<String> tlp);
}