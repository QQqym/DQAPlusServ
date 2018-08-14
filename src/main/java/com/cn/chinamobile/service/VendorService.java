package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.ProvinceMapper;
import com.cn.chinamobile.dao.mybatis.mapper.VendorMapper;
import com.cn.chinamobile.pojo.mybatis.Province;
import com.cn.chinamobile.pojo.mybatis.Vendor;
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
public class VendorService {

    @Resource
    private VendorMapper vendorMapper;

    /**
     *查询厂家信息
     * @return  返回省份信息，key是厂家简称，value是中文
     */
    public Map<String,String> selectAll(){
        List<Vendor> vendorList = vendorMapper.selectAll();
        Map<String,String> vendorMap = new HashMap<>(16);
        for(Vendor v : vendorList){
            vendorMap.put(v.getAbridge(),v.getVendor());
        }
        return vendorMap;
    }
}
