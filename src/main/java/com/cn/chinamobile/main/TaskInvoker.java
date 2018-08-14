package com.cn.chinamobile.main;


import com.cn.chinamobile.business.CorBusiness;
import com.cn.chinamobile.pojo.mybatis.ScheduleTask;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.service.ScheduleTaskService;
import com.cn.chinamobile.util.*;
import org.apache.log4j.PropertyConfigurator;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zh on 2017/5/16.
 */
public class TaskInvoker {

    static SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
    static TimeUtil timeUtil = new TimeUtil();
    static FilePathGenerate filePathGenerate = new FilePathGenerate();

    static ScheduleTaskService scheduleTaskService;

    public static void main(String[] args) {


        //加载spring配置文件
        IniDomain.initSpringCfg(ContentInfo.ROOT_PATH + "conf/springCommon.xml");
        PropertyConfigurator.configure(ContentInfo.ROOT_PATH + "conf/log4j.properties");

        if (args.length != 8) {
            Log.info("输入参数个数错误");
            System.exit(0);
        }

        //网元类型 ENB
        String neType = args[1];
        //数据类型 EutranCellTdd
        String dataType = args[4];
        String begintimeStr = args[5];
        String endtimeStr = args[6];
        Date begintime = null;
        Date endtime = null;
        String vendorname = args[2];
        String version = args[3];
        String province = args[0];

        try {
            begintime = timeFormatter.parse(begintimeStr);
            endtime = timeFormatter.parse(endtimeStr);
        } catch (Exception e) {
            Log.error("输入的时间参数有误，开始时间：" + begintimeStr + ",结束时间：" + endtimeStr, e);
        }

        //北向、OMC、网优平台存放中间结果的路径 省份/网元类型/厂家/版本号
        String tmpFilePrefix = args[0] + File.separator + args[1] + File.separator + args[2] + File.separator + args[3];

        //加载资源文件
        IniDomain.initResource();

        scheduleTaskService = IniDomain.ct.getBean(ScheduleTaskService.class);

        //开始时间与结束时间比较，开始时间<=结束时间 执行任务
        while (begintime.getTime() <= endtime.getTime()) {
            String parsetimestr = timeFormatter.format(begintime);
            String datetime = timeUtil.getDestTime(parsetimestr);
            //判断任务是否存在
            //判断是否已经跑完或者正在跑数据
            ScheduleTask scheduleTask = scheduleTaskService.selectByParas(datetime, province,
                    neType, vendorname, version, dataType);
            if (scheduleTask != null && scheduleTask.getTaskstatus().equals("2")) {
                Log.info("task exist :" + scheduleTask.getProvince() + "-" + scheduleTask.getNetype() + "-" +
                        scheduleTask.getDatatype() + "-" + datetime + "-" + scheduleTask.getVendor());

                //如果任务存在，开始时间增加1小时，否则会陷入死循环
                begintime = timeUtil.getNextHour(begintime);
                continue;
            }
//            //获取业务类实例
            CorBusiness corBusiness = IniDomain.ct.getBean(CorBusiness.class);
//            //如果任务不存在，插入任务记录
            if (scheduleTask == null) {
                scheduleTask = new ScheduleTask(datetime, province, neType, vendorname, version, dataType);
                scheduleTaskService.insert(scheduleTask);
            } else {
                Log.info("task status :" + scheduleTask.getIntid() + "-" + scheduleTask.getTaskstatus());
            }

            //北向文件存放路径
            String bxfilePath = filePathGenerate.getBeiXiangPMPath(datetime, args);
            //omc文件存放路径
            String omcFilePath = filePathGenerate.getOMCPath(datetime, args);
            //网优平台文件存放路径
            String wyptFilePath = filePathGenerate.getWyptPath(datetime, args);


            //话务网管文件存放路径
            String hwwgFilePath = filePathGenerate.getHwwgPath(datetime, args);
            corBusiness.startBusiness(bxfilePath, omcFilePath, wyptFilePath, hwwgFilePath, scheduleTask, neType, dataType, datetime, vendorname, version, tmpFilePrefix, args[7]);

            //开始时间增加1小时
            begintime = timeUtil.getNextHour(begintime);
        }

        System.out.println("-----------");
    }

}
