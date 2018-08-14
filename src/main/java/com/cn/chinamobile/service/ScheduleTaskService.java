package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.ScheduleTaskMapper;
import com.cn.chinamobile.pojo.mybatis.ScheduleConfig;
import com.cn.chinamobile.pojo.mybatis.ScheduleTask;
import com.cn.chinamobile.util.ContentInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xueweixia on 2017/7/12.
 * 任务处理业务类
 */
@Service
public class ScheduleTaskService {

    @Resource
    private ScheduleTaskMapper scheduleTaskMapper;

    public int insert(ScheduleTask record){
        return scheduleTaskMapper.insert(record);
    }

    public int insertSelective(ScheduleTask record){
        return scheduleTaskMapper.insertSelective(record);
    }

    /**
     * 通过参数查询任务
     * @param datetime 执行时间
     * @param args 参数 省份、网元类型、版本、厂家、数据类型
     * @return  任务，存在返回任务实体类，不存在返回null
     */
    public ScheduleTask selectByParas(String datetime,String... args){
        Map map = new HashMap();
        map.put("province",args[0]);
        map.put("netype",args[1]);
        map.put("vendor",args[2]);
        map.put("version",args[3]);
        map.put("datatype",args[4]);
        map.put("datetime",datetime);
        return scheduleTaskMapper.selectByParas(map);
    }

    /**
     * 通过参数查询执行失败任务
     * @return  任务，返回执行失败的任务
     */
    public List<ScheduleTask> selectFailTask(){
        Map map = new HashMap();
        map.put("executetime", ContentInfo.FAIL_NUM);
        map.put("taskstatus","0");
        return scheduleTaskMapper.selectFailTask(map);
    }

    public ScheduleTask selectByPrimaryKey(Integer intid){
        return scheduleTaskMapper.selectByPrimaryKey(intid);
    }

    public int updateByPrimaryKeySelective(ScheduleTask record){
        return scheduleTaskMapper.updateByPrimaryKeySelective(record);
    }

    public int updateByPrimaryKey(ScheduleTask record){
        return scheduleTaskMapper.updateByPrimaryKey(record);
    }

    public List<ScheduleTask> selectByDateAndStatus(String datetime){

        return scheduleTaskMapper.selectByDateAndStatus(datetime);

    }


}
