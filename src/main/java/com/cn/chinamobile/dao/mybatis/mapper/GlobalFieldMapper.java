package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.GlobalField;

import java.util.List;
import java.util.Map;

public interface GlobalFieldMapper {

    List<GlobalField> selectByTableName(String tableName);

    int insert(GlobalField record);

    int insertSelective(GlobalField record);

    List<GlobalField> selectByparas(Map map);
}