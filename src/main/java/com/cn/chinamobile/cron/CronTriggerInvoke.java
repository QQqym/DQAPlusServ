package com.cn.chinamobile.cron;

import com.cn.chinamobile.pojo.mybatis.ScheduleConfig;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.service.ScheduleConfigService;
import com.cn.chinamobile.service.ScheduleRunnable;
import com.cn.chinamobile.util.TimeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xueweixia on 2017/6/9.
 * 定时器触发全国数据扫描任务
 */
@Service
public class CronTriggerInvoke {

    @Resource
    private ScheduleConfigService scheduleConfigService;

    private TimeUtil timeUtil = new TimeUtil() ;

    public void doJob(){
        Date date = new Date();
        //当前时间，小时
        int currenthour = timeUtil.getHour(date);
        //当前日期 yyyy-mm-dd
        String currentDay = timeUtil.getDataTime(date);
        List<ScheduleConfig> list = scheduleConfigService.selectAll();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        for(int i=0;i<list.size();i++){
            ScheduleConfig scheduleConfig = list.get(i);
            String collecttime =  scheduleConfig.getScheduletime();
            String[] times = collecttime.split(",");
            for(String time : times){
                //如果小于当前时间，检索
                if(Integer.parseInt(time)<currenthour){
                    //如果小时是1位，前面补0
                    if(time.length()==1){
                        time = "0"+time;
                    }
                    String ctime = currentDay + "-" + time;
                    ScheduleRunnable scheduleRunnable = IniDomain.ct.getBean(ScheduleRunnable.class);
                    scheduleRunnable.setPara(scheduleConfig,ctime);
                    fixedThreadPool.submit(scheduleRunnable);
                }else{//时间按照大小排序，遇到比当前时间大的该任务跳过
                    break;
                }
            }

        }
        fixedThreadPool.shutdown();
    }
}
