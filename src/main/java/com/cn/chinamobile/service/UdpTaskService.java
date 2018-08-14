package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.UdpTaskMapper;
import com.cn.chinamobile.pojo.mybatis.UdpTask;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhou on 2017/10/29.
 */
@Service
public class UdpTaskService {

    @Resource
    private UdpTaskMapper udpTaskMapper;


    public UdpTask selectByParas(UdpTask udpTask){
        return udpTaskMapper.selectByParas(udpTask);
    }

    public int insert(UdpTask record){
        return udpTaskMapper.insert(record);
    }

    public int updateByPrimaryKey(UdpTask record){
       return udpTaskMapper.updateByPrimaryKey(record);
    }
}
