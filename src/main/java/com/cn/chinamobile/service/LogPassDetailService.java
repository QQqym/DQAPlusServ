package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.LogPassDetailMapper;
import com.cn.chinamobile.pojo.mybatis.LogPassDetail;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhou on 2017/10/31.
 */
@Service
public class LogPassDetailService {

    @Resource
    private LogPassDetailMapper logPassDetailMapper;

    public int insert(LogPassDetail record){
        return logPassDetailMapper.insert(record);
    }

    public void deletebytaskid(int taskid){
        logPassDetailMapper.deletebytaskid(taskid);
    }

    public void batchinsert(List<LogPassDetail> list){
        logPassDetailMapper.batchinsert(list);
    }
}
