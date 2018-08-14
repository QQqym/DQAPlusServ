package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.CityInfoMapper;
import com.cn.chinamobile.pojo.mybatis.CityInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xueweixia
 */
@Service
public class CityInfoService {

    @Resource
    private CityInfoMapper cityInfoMapper;

    /**
     * 获取地市简称和名称的信息
     * @return 地市简称和名称，省份-地市简称,省份名称-地市名称
     */
    public Map<String,String> getCityInfo(){
        Map<String,String> cityMap = new HashMap<>(500);
        List<CityInfo> cityInfoList = cityInfoMapper.selectAll();
        for(CityInfo cityInfo : cityInfoList){
            if(cityMap.containsKey(cityInfo.getProvincename()+"-"+cityInfo.getEnglishname())){
                cityMap.put(cityInfo.getProvincename()+"-"+cityInfo.getEnglishname(),cityInfo.getProvincename()+"-"+cityInfo.getEnglishname());
            }else {
                cityMap.put(cityInfo.getProvincename()+"-"+cityInfo.getEnglishname(),cityInfo.getProvincename()+"-"+cityInfo.getCityname());
            }

        }

        return cityMap;
    }

}
