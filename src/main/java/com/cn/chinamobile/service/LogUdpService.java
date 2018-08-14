package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.LogUdpMapper;
import com.cn.chinamobile.pojo.mybatis.LogUdp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xueweixia
 */
@Service
public class LogUdpService {

    @Resource
    private LogUdpMapper logUdpMapper;

    public int insert(LogUdp record){
        return logUdpMapper.insert(record);
    }

    public void deletebytaskid(int taskid){
        logUdpMapper.deletebytaskid(taskid);
    }

    public  void batchinsert(List<LogUdp> list){
        logUdpMapper.batchinsert(list);
    }
}
