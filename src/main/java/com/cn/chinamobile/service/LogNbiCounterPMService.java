package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.LogNbiCounterPMMapper;
import com.cn.chinamobile.entity.BatchInfo;
import com.cn.chinamobile.pojo.mybatis.LogNbiCounterPM;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zh on 2017/6/25.
 */
@Service
public class LogNbiCounterPMService {

    @Resource
    private LogNbiCounterPMMapper logNbiCounterPMMapper;

   public int insert(LogNbiCounterPM record){
        return logNbiCounterPMMapper.insert(record);
    }

    /**
     * 根据任务ID删除数据
     * @param tablename 表名
     * @param taskid 任务号
     */
    public void deletebytaskid(String tablename,int taskid){
        LogNbiCounterPM logNbiCounterPM = new LogNbiCounterPM();
        logNbiCounterPM.setTablename(tablename);
        logNbiCounterPM.setTaskid(taskid);
        logNbiCounterPMMapper.deletebytaskid(logNbiCounterPM);
    }

    /**
     * 批量入库
     * @param tablename 表名
     * @param pmList pm差异list
     */
    public void batchinsert(String tablename,List<LogNbiCounterPM> pmList){
        BatchInfo batchInfo = new BatchInfo(tablename,pmList);
        if(tablename.toLowerCase().contains("enb")){
            logNbiCounterPMMapper.batchenbinsert(batchInfo);
        } else {
            logNbiCounterPMMapper.batchinsert(batchInfo);
        }

    }
}
