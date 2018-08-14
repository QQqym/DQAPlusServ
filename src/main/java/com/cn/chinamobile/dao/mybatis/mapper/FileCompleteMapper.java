package com.cn.chinamobile.dao.mybatis.mapper;

import com.cn.chinamobile.pojo.mybatis.FileCompleteWithBLOBs;

public interface FileCompleteMapper {
    int insert(FileCompleteWithBLOBs record);

    int insertSelective(FileCompleteWithBLOBs record);

    FileCompleteWithBLOBs selectByParas(FileCompleteWithBLOBs record);
}