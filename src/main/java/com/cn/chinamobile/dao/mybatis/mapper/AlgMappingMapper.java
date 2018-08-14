package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.AlgMapping;

import java.util.List;
import java.util.Map;

public interface AlgMappingMapper {
    int insert(AlgMapping record);

    int insertSelective(AlgMapping record);

    List<AlgMapping> selectByParas(Map map);
}