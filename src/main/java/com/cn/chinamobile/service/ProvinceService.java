package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.ProvinceMapper;
import com.cn.chinamobile.pojo.mybatis.Province;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询省份信息
 * @author xueweixia
 */
@Service
public class ProvinceService {

    @Resource
    private ProvinceMapper provinceMapper;

    /**
     *查询省份信息
     * @return  返回省份信息，key是英文简称，value是中文
     */
    public Map<String,String> selectAll(){
        List<Province> provinceList = provinceMapper.selectAll();
        Map<String,String> provinceMap = new HashMap<>(54);
        for(Province p : provinceList){
            provinceMap.put(p.getEnglishname(),p.getCabbreviation());
        }
        return provinceMap;
    }
}
