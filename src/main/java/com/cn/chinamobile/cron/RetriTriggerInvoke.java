package com.cn.chinamobile.cron;

import com.cn.chinamobile.pojo.mybatis.ScheduleTask;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.service.RetriRunnable;
import com.cn.chinamobile.service.ScheduleTaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xueweixia
 * 重新解析失败的任务
 */
@Service
public class RetriTriggerInvoke {

    @Resource
    private ScheduleTaskService scheduleTaskService;

    public void doRetri(){
        List<ScheduleTask> failTasks = scheduleTaskService.selectFailTask();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        for(ScheduleTask failTask : failTasks){
            RetriRunnable retriRunnable = IniDomain.ct.getBean(RetriRunnable.class);
            retriRunnable.setPara(failTask);
            fixedThreadPool.submit(retriRunnable);
        }

    }
}
