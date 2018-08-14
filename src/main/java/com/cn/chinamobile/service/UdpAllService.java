package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.UdpAllMapper;
import com.cn.chinamobile.pojo.mybatis.UdpAll;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author xueweixia
 */
@Service
public class UdpAllService {

    @Resource
    private UdpAllMapper udpAllMapper;

    public int insert(UdpAll record){
        return udpAllMapper.insert(record);
    }

    public List<UdpAll> selectByType(String filetype,String type){
        Map<String,String> map = new HashMap<>();
        map.put("type",type);
        map.put("filetype",filetype);
        return udpAllMapper.selectByType(map);
    }

    public List<UdpAll> selectDisTask(String filetype,String type){
        Map<String,String> map = new HashMap<>();
        map.put("type",type);
        map.put("filetype",filetype);
        return udpAllMapper.selectDisTask(map);
    }

    public void deletebyparas(String province, String filetype, String netype, String vendor, String version, String type, String datetime){
        Map<String,String> map = new HashMap<>();
        map.put("province",province);
        map.put("filetype",filetype);
        map.put("netype",netype);
        map.put("vendor",vendor);
        map.put("version",version);
        map.put("type",type);
        map.put("datetime",datetime);
        udpAllMapper.deletebyparas(map);
    }
}
