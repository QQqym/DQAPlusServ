package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.UdpLosednMapper;
import com.cn.chinamobile.pojo.mybatis.UdpLosedn;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xueweixia
 */
@Service
public class UdpLosednService {
    @Resource
    private UdpLosednMapper udpLosednMapper;

    public int insert(UdpLosedn record){
        return udpLosednMapper.insert(record);
    }


    public void deletebytaskid(int taskid){
        udpLosednMapper.deletebytaskid(taskid);
    }

    public void batchinsert(List<UdpLosedn> losednList){
        udpLosednMapper.batchinsert(losednList);
    }

}
