package com.cn.chinamobile.main;

import com.cn.chinamobile.pojo.mybatis.ScheduleConfig;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.service.ScheduleConfigService;
import com.cn.chinamobile.service.ScheduleRunnable;
import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.Log;
import com.cn.chinamobile.util.TimeUtil;
import org.apache.log4j.PropertyConfigurator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author xueweixia
 */
public class AllTaskInvoke {
    static SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
    static TimeUtil timeUtil = new TimeUtil();
    public static void main(String[] args) {
        //加载spring配置文件
        IniDomain.initSpringCfg(ContentInfo.ROOT_PATH +"conf/springCommon.xml");

        //加载资源文件
        IniDomain.initResource();

        //设置log4j
        PropertyConfigurator.configure(ContentInfo.ROOT_PATH + "conf/log4j.properties");

        if(args.length != 2){
            Log.info("输入参数个数错误，请输入开始和结束时间");
            System.exit(0);
        }


        ScheduleConfigService scheduleConfigService = IniDomain.ct.getBean(ScheduleConfigService.class);
        List<ScheduleConfig> list = scheduleConfigService.selectAll();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        for(int i=0;i<list.size();i++){
            ScheduleConfig scheduleConfig = list.get(i);
            String begintimeStr = args[0];
            String endtimeStr = args[1];
            Date begintime = null;
            Date endtime = null;
            try{
                begintime = timeFormatter.parse(begintimeStr);
                endtime = timeFormatter.parse(endtimeStr);
            }catch (Exception e){
                Log.error("输入的时间参数有误，开始时间："+begintimeStr + ",结束时间："+endtimeStr,e);
            }
            while (begintime.getTime()<=endtime.getTime()){
                String parsetimestr = timeFormatter.format(begintime);
                String datetime = timeUtil.getDestTime(parsetimestr);
                ScheduleRunnable scheduleRunnable = IniDomain.ct.getBean(ScheduleRunnable.class);
                scheduleRunnable.setPara(scheduleConfig,datetime);
                fixedThreadPool.execute(scheduleRunnable);

                //开始时间增加6小时
                begintime = timeUtil.getNextHour(begintime);
            }

        }

        fixedThreadPool.shutdown();

    }
}
