package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.UdpExeSetMapper;
import com.cn.chinamobile.pojo.mybatis.UdpExeSet;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author zhou on 2017/11/14.
 */
@Service
public class UdpExeSetService {

    @Resource
    private UdpExeSetMapper udpExeSetMapper;

    /**
     * 获取0值豁免指标集
     * @param netype 网元类型
     * @return 数据类型，豁免指标集
     */
    public Map<String,Set<String>> selectByNetype(String netype){
        List<UdpExeSet> udpExeSetList = udpExeSetMapper.selectByNetype(netype);
        Map<String,Set<String>> udpExceMap = new HashMap<>();
        for(UdpExeSet udpExe : udpExeSetList){
            Set<String>  udpSet;
            if(udpExceMap.containsKey(udpExe.getDatatype())){
                udpSet = udpExceMap.get(udpExe.getDatatype());
            }else {
                udpSet = new HashSet<>();
                udpExceMap.put(udpExe.getDatatype(),udpSet);
            }
            udpSet.add(udpExe.getPmname());
        }
        return udpExceMap;

    }
}
