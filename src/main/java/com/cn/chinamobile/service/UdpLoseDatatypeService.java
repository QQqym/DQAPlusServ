package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.UdpLoseDatatypeMapper;
import com.cn.chinamobile.pojo.mybatis.UdpLoseDatatype;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhou on 2017/11/8.
 */

@Service
public class UdpLoseDatatypeService {

    @Resource
    private UdpLoseDatatypeMapper udpLoseDatatypeMapper;

    public int insert(UdpLoseDatatype record){
        return udpLoseDatatypeMapper.insert(record);
    }

    public void deletebytaskid(int taskid){
        udpLoseDatatypeMapper.deletebytaskid(taskid);
    }
}
