package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.LogNbiWyptKPIMapper;
import com.cn.chinamobile.entity.BatchInfo;
import com.cn.chinamobile.pojo.mybatis.LogNbiWyptKPI;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zh on 2017/7/5.
 */
@Service
public class LogNbiWyptKPIService {
    @Resource
    private LogNbiWyptKPIMapper logNbiWyptKPIMapper;

    public int insert(LogNbiWyptKPI record){
        return logNbiWyptKPIMapper.insert(record);
    }

    /**
     * 根据任务ID删除数据
     * @param tablename 表名
     * @param taskid 任务号
     */
    public void deletebytaskid(String tablename,int taskid){
        LogNbiWyptKPI logNbiWyptKPI = new LogNbiWyptKPI();
        logNbiWyptKPI.setTablename(tablename);
        logNbiWyptKPI.setTaskid(taskid);
        logNbiWyptKPIMapper.deletebytaskid(logNbiWyptKPI);
    }

    /**
     * 批量入库
     * @param tablename 表名
     * @param kpiList kpi差异list
     */
    public void batchinsert(String tablename,List<LogNbiWyptKPI> kpiList){
        BatchInfo batchInfo = new BatchInfo(tablename,kpiList);
        logNbiWyptKPIMapper.batchinsert(batchInfo);
    }
}
