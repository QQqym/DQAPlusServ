package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.LogNbiHwwgKpiMapper;
import com.cn.chinamobile.pojo.mybatis.LogNbiHwwgKpi;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xueweixia
 */
@Service
public class LogNbiHwwgKpiService{

    @Resource
    private LogNbiHwwgKpiMapper logNbiHwwgKpiMapper;

    public int insert(LogNbiHwwgKpi record){
        return logNbiHwwgKpiMapper.insert(record);
    }

    /**
     * 根据任务ID删除数据
     * @param tablename 表名
     * @param taskid 任务号
     */
   public void deletebytaskid(String tablename,int taskid){
       LogNbiHwwgKpi logNbiHwwgKpi = new LogNbiHwwgKpi();
       logNbiHwwgKpi.setTablename(tablename);
       logNbiHwwgKpi.setTaskid(taskid);
       logNbiHwwgKpiMapper.deletebytaskid(logNbiHwwgKpi);
    }
}
