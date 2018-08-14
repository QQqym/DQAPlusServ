package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.UdpNrmMapper;
import com.cn.chinamobile.pojo.mybatis.UdpNrm;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhou on 2017/10/29.
 */
@Service
public class UdpNrmService {

    @Resource
    private UdpNrmMapper udpNrmMapper;

    /**
     * 通过网元类型和版本号查询NRM指标
     * @param netype
     * @param ver
     * @return 返回Map，顺序为子网元类型，指标名，指标实体类
     */
    public Map<String,Map<String,UdpNrm>> selectByParas(String netype,String ver){
        Map<String,Map<String,UdpNrm>> pmMap = new HashMap<>();
        Map map = new HashMap<String,String>();
        map.put("neType",netype);
        map.put("ver",ver);
        List<UdpNrm> udpNrmList = udpNrmMapper.selectByParas(map);
        if(null!=udpNrmList){
            for(UdpNrm udpNrm : udpNrmList){
                Map<String,UdpNrm> udpNrmMap ;
                if(pmMap.containsKey(udpNrm.getManagedClass())){
                    udpNrmMap = pmMap.get(udpNrm.getManagedClass());
                }else {
                    udpNrmMap = new HashMap<>();
                    pmMap.put(udpNrm.getManagedClass(),udpNrmMap);
                }
                udpNrmMap.put(udpNrm.getNrmnameEn(),udpNrm);
            }
        }


        return pmMap;
    }

    /**
     * 通过网元类型和版本，查询NRM的层级关系
     * @param netype 网元类型
     * @param ver 版本
     * @return 网元层级关系，key是子级，value是父级
     */
    public Map<String,String> selectLevelInfo(String netype,String ver){
        Map<String,String> levelMap = new HashMap<>();
        Map map = new HashMap<String,String>();
        map.put("neType",netype);
        map.put("ver",ver);
        List<UdpNrm> udpNrmList = udpNrmMapper.selectLevelInfo(map);
        if(null!=udpNrmList) {
            for (UdpNrm udpNrm : udpNrmList) {
                levelMap.put(udpNrm.getManagedClass(),udpNrm.getLastClass());
            }
        }
        return levelMap;
    }


    public String getVersion(String netype){
        return udpNrmMapper.getVersion(netype);
    }

    public String getHigherVersion(String netype,String ver){
        Map<String,String> map = new HashMap<>();
        map.put("netype",netype);
        map.put("ver",ver);
        return udpNrmMapper.getHigherVersion(map);
    }
}
