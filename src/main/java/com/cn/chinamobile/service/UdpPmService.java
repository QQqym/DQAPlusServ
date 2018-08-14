package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.UdpPmMapper;
import com.cn.chinamobile.pojo.mybatis.UdpPm;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhou on 2017/11/1.
 */
@Service
public class UdpPmService {

    @Resource
    private UdpPmMapper udpPmMapper;

    /**
     * 通过网元类型和版本号查询PM指标
     * @param netype
     * @param ver
     * @return 返回Map，顺序为子网元类型，指标名，指标实体类
     */
    public Map<String,Map<String,UdpPm>> selectByParas(String netype,String ver){
        Map<String,Map<String,UdpPm>> pmMap = new HashMap<>();
        Map map = new HashMap<String,String>();
        map.put("neType",netype);
        map.put("ver",ver);
        List<UdpPm> udpNrmList = udpPmMapper.selectByParas(map);
        if(null!=udpNrmList){
            for(UdpPm udpPm : udpNrmList){
                Map<String,UdpPm> udpNrmMap ;
                if(pmMap.containsKey(udpPm.getSpaceGranularity())){
                    udpNrmMap = pmMap.get(udpPm.getSpaceGranularity());
                }else {
                    udpNrmMap = new HashMap<>();
                    pmMap.put(udpPm.getSpaceGranularity(),udpNrmMap);
                }
                udpNrmMap.put(udpPm.getPmnameEn(),udpPm);
            }
        }


        return pmMap;
    }

    public String getVersion(String netype){
        return udpPmMapper.getVersion(netype);
    }

    public String getHigherVersion(String netype,String ver){
        Map<String,String> map = new HashMap<>();
        map.put("netype",netype);
        map.put("ver",ver);
        return udpPmMapper.getHigherVersion(map);
    }
}
