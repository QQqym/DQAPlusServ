package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.UdpExempDtypeMapper;
import com.cn.chinamobile.pojo.mybatis.UdpExempDtype;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author zhou on 2017/11/18.
 */
@Service
public class UdpExempDtypeService {

    @Resource
    private UdpExempDtypeMapper udpExempDtypeMapper;

    public Map<String,Set<String>> selectByParas(String netype,String type,String version){
        Map<String,Set<String>> exemMap = new HashMap<>();
        Map<String,String> map = new HashMap<>();
        map.put("neType",netype);
        map.put("type",type);

        //版本不为空，设置版本
        if(!version.equalsIgnoreCase("")){
            map.put("version",version);
        }
        List<UdpExempDtype> udpExempDtypeList = udpExempDtypeMapper.selectByParas(map);

        for(UdpExempDtype udpExempDtype : udpExempDtypeList){
            Set<String> udpSet;
            if(exemMap.containsKey(udpExempDtype.getExcetype())){
                udpSet = exemMap.get(udpExempDtype.getExcetype());
            }else {
                udpSet = new HashSet<>();
                exemMap.put(udpExempDtype.getExcetype(),udpSet);
            }
            udpSet.add(udpExempDtype.getDatatype());
        }


        return exemMap;
    }
}
