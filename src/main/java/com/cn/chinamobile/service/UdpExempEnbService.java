package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.UdpExempEnbMapper;
import com.cn.chinamobile.pojo.mybatis.UdpExempEnb;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xueweixia
 */
@Service
public class UdpExempEnbService {

    @Resource
    private UdpExempEnbMapper udpExempEnbMapper;

    /**
     * 查询ENB豁免项
     * @param type 文件类型
     * @param vendor 厂家
     * @param version 北向版本
     * @return 豁免数据 软件版本，子网元-指标名，豁免类型
     */
    public Map<String,Map<String,String>> getEnbExcepMap(String type,String vendor,String version){
        Map<String,Map<String,String>> enbExcepMap = new HashMap<>();
        Map<String,String> map = new HashMap<>();
        map.put("type",type);
        map.put("vendor",vendor);
        map.put("version",version);
        List<UdpExempEnb> udpExempEnbList = udpExempEnbMapper.selectByParas(map);
        if(udpExempEnbList != null){
            for(UdpExempEnb udpExempEnb : udpExempEnbList){
                Map<String,String> datamap = new HashMap<>();
                if(enbExcepMap.containsKey(udpExempEnb.getSwversion())){
                    datamap = enbExcepMap.get(udpExempEnb.getSwversion());
                }else {
                    enbExcepMap.put(udpExempEnb.getSwversion(),datamap);
                }
                datamap.put(udpExempEnb.getDatatype()+"-"+udpExempEnb.getPmname(),udpExempEnb.getExcetype());
            }
        }

        return enbExcepMap;
    }
}
