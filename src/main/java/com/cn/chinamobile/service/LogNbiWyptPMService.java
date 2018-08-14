package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.LogNbiWyptPMMapper;
import com.cn.chinamobile.entity.BatchInfo;
import com.cn.chinamobile.pojo.mybatis.LogNbiWyptKPI;
import com.cn.chinamobile.pojo.mybatis.LogNbiWyptPM;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zh on 2017/7/6.
 */
@Service
public class LogNbiWyptPMService {
    @Resource
    private LogNbiWyptPMMapper logNbiWyptPMMapper;

    public int insert(LogNbiWyptPM record){
        return  logNbiWyptPMMapper.insert(record);
    }

    /**
     * 批量入库
     * @param tablename 表名
     * @param pmList pm差异list
     */
    public void batchinsert(String tablename,List<LogNbiWyptPM> pmList){
        BatchInfo batchInfo = new BatchInfo(tablename,pmList);
        logNbiWyptPMMapper.batchinsert(batchInfo);
    }

    /**
     * 根据任务ID删除数据
     * @param tablename 表名
     * @param taskid 任务号
     */
    public void deletebytaskid(String tablename,int taskid){
        LogNbiWyptPM logNbiWyptPM = new LogNbiWyptPM();
        logNbiWyptPM.setTablename(tablename);
        logNbiWyptPM.setTaskid(taskid);
        logNbiWyptPMMapper.deletebytaskid(logNbiWyptPM);
    }
}
