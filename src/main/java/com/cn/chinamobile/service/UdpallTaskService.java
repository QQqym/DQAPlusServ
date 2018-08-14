package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.UdpallTaskMapper;
import com.cn.chinamobile.pojo.mybatis.UdpallTask;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhou on 2017/11/11.
 */
@Service
public class UdpallTaskService {

    @Resource
    private UdpallTaskMapper udpallTaskMapper;

    public List<UdpallTask> selectByType(String type){
        return udpallTaskMapper.selectByType(type);
    }
}
