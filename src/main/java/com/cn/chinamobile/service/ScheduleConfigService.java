package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.ScheduleConfigMapper;
import com.cn.chinamobile.pojo.mybatis.ScheduleConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zh on 2017/7/10.
 */
@Service
public class ScheduleConfigService {
    @Resource
    private ScheduleConfigMapper scheduleConfigMapper;

    public List<ScheduleConfig> selectAll(){
        return scheduleConfigMapper.selectAll();
    }
}
