package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.GlobalFieldMapper;
import com.cn.chinamobile.pojo.mybatis.GlobalField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zh on 2017/6/11.
 */
@Service
public class GlobalFieldService {

    @Resource
    private GlobalFieldMapper globalFieldMapper;

    public List<GlobalField> selectByTableName(String tableName){
        return globalFieldMapper.selectByTableName(tableName);
    }

    public List<GlobalField> selectByparas(String tableprefix,String netype,String datatype){
        Map map = new HashMap();
        map.put("tableprefix",tableprefix);
        map.put("netype",netype);
        map.put("dataType",datatype);
        return globalFieldMapper.selectByparas(map);
    }

    public int insert(GlobalField record){
        return 0;
    }

    public int insertSelective(GlobalField record){
        return 0;
    }
}
