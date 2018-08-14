package com.cn.chinamobile.service;

import com.cn.chinamobile.pojo.mybatis.ScheduleTask;
import com.cn.chinamobile.util.FilePathGenerate;
import org.springframework.stereotype.Service;

/**
 * @author xueweixia
 * 补采业务类
 */
@Service
public class RetriRunnable implements Runnable {
    private ScheduleTask scheduleTask;
    private FilePathGenerate filePathGenerate = new FilePathGenerate();

    public void setPara(ScheduleTask scheduleTask){
        this.scheduleTask = scheduleTask;
    }
    @Override
    public void run() {


    }
}
