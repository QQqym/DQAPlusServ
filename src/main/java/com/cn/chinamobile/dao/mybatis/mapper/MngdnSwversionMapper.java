package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.MngdnSwversion;

import java.util.List;

public interface MngdnSwversionMapper {

    List<MngdnSwversion> selecAlltByParas(MngdnSwversion record);

    MngdnSwversion selectByParas(MngdnSwversion record);

    int insert(MngdnSwversion record);

    int insertSelective(MngdnSwversion record);

    //批量入库
    void batchinsert(List<MngdnSwversion> list);
}