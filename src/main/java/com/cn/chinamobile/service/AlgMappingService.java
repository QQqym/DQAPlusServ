package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.AlgMappingMapper;
import com.cn.chinamobile.pojo.mybatis.AlgMapping;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xueweixia
 */
@Service
public class AlgMappingService {

    @Resource
    private AlgMappingMapper algMappingMapper;

    public Map<String,String> selectByParas(String netype,String vendor,String version){
        Map<String,String> map = new HashMap<>();
        map.put("netype",netype);
        map.put("vendor",vendor);
        map.put("version",version);
        List<AlgMapping> exists = algMappingMapper.selectByParas(map);

        Map<String,String> swVersionMap = new HashMap<>();
        for(AlgMapping algMapping : exists){
            swVersionMap.put(algMapping.getSwversion(),algMapping.getAlgversion());
        }
        return swVersionMap;
    }

}
