package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.UdpGradeTotalMapper;
import com.cn.chinamobile.pojo.mybatis.UdpGradeTotal;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author xueweixia
 */
@Service
public class UdpGradeTotalService {

    @Resource
    private UdpGradeTotalMapper udpGradeTotalMapper;

   public int insert(UdpGradeTotal record){
       return udpGradeTotalMapper.insert(record);
   }

    public void deletebytaskid(int taskid){
        udpGradeTotalMapper.deletebytaskid(taskid);
    }
}
