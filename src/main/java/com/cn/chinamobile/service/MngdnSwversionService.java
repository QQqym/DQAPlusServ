package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.MngdnSwversionMapper;
import com.cn.chinamobile.pojo.mybatis.MngdnSwversion;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xueweixia
 */
@Service
public class MngdnSwversionService {

    @Resource
    private MngdnSwversionMapper mngdnSwversionMapper;

    /**
     * 通过参数查询dn，软件版本
     * @param record 参数
     * @return dn,软件版本
     */
    public Map<String,String> selecAlltByParas(MngdnSwversion record){
        Map<String,String> dnSwversionMap = new HashMap<>();
        List<MngdnSwversion> list = mngdnSwversionMapper.selecAlltByParas(record);
        if(null != list){
            for(MngdnSwversion mngdnSwversion : list){
                dnSwversionMap.put(mngdnSwversion.getDn(),mngdnSwversion.getSwversion());
            }
        }

        return dnSwversionMap;
    }

    public MngdnSwversion selectByParas(MngdnSwversion record){
        return mngdnSwversionMapper.selectByParas(record);
    }

    public int insert(MngdnSwversion record){
        return mngdnSwversionMapper.insert(record);
    }

    /**
     * 批量入库
     * @param list
     */
    public void batchinsert(List<MngdnSwversion> list){
        mngdnSwversionMapper.batchinsert(list);
    }


    /**
     * 批量入库资源获取到的dn信息
     * @param province 省份
     * @param vendor 厂家
     * @param netype 网元
     * @param version 版本
     * @param dnSwversion dn和软件版本号
     */
    public void insertbatchDnSwVersion(String province,String vendor,String netype,String version,Map<String,String> dnSwversion){
        int count=0;
        List<MngdnSwversion> batchList = new ArrayList<>(1600);
        for(String dn : dnSwversion.keySet()){
            count++;
            MngdnSwversion mngdnSwversion = new MngdnSwversion(province,vendor,netype,version,dn,dnSwversion.get(dn));
            batchList.add(mngdnSwversion);
            //批量入库一千条
            if(count%1000==0){
                mngdnSwversionMapper.batchinsert(batchList);
                //入库完清空list
                batchList.clear();
            }
        }
        if(batchList.size()>0){
            mngdnSwversionMapper.batchinsert(batchList);
            batchList.clear();
        }
    }
}
