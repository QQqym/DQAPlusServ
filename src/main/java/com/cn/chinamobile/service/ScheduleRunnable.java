package com.cn.chinamobile.service;

import com.cn.chinamobile.business.CorBusiness;
import com.cn.chinamobile.pojo.mybatis.ScheduleConfig;
import com.cn.chinamobile.pojo.mybatis.ScheduleTask;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.util.FilePathGenerate;
import com.cn.chinamobile.util.Log;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.concurrent.Callable;

/**
 * Created by xueweixia on 2017/7/11.
 * 任务调度
 */
@Service
public class ScheduleRunnable implements Runnable {

    private ScheduleConfig scheduleConfig;
    private String datatime;
    private FilePathGenerate filePathGenerate = new FilePathGenerate();

    @Resource
    private ScheduleTaskService scheduleTaskService;

    @Resource
    private CorBusiness corBusiness;

    //设置参数
    public void setPara(ScheduleConfig scheduleConfig,String datatime){
        this.scheduleConfig = scheduleConfig;
        this.datatime = datatime;
    }

    public void run() {
        //判断是否已经跑完或者正在跑数据
        ScheduleTask scheduleTask = scheduleTaskService.selectByParas(datatime,scheduleConfig.getProvince(),
                                                scheduleConfig.getNetype(),scheduleConfig.getVendor(),
                                                scheduleConfig.getVersion(),scheduleConfig.getDatatype());
        if(scheduleTask != null  && scheduleTask.getTaskstatus().equals("2")){
            Log.info("task exist :"+ scheduleTask.getProvince()+"-"+scheduleTask.getNetype()+"-"+
                        scheduleTask.getDatatype()+"-"+datatime+"-"+scheduleTask.getVendor());
            return ;
        }

        //如果任务不存在，插入任务记录
        if(scheduleTask == null){
            scheduleTask = new ScheduleTask(datatime,scheduleConfig.getProvince(),scheduleConfig.getNetype(),scheduleConfig.getVendor(),
                    scheduleConfig.getVersion(),scheduleConfig.getDatatype());
            scheduleTaskService.insert(scheduleTask);
        }


        //北向文件存放路径
        String bxfilePath = filePathGenerate.getBeiXiangPMPath(datatime,scheduleConfig.getProvince(),scheduleConfig.getNetype(),scheduleConfig.getVendor());
        //omc文件存放路径
        String omcFilePath = filePathGenerate.getOMCPath(datatime,scheduleConfig.getProvince(),scheduleConfig.getNetype(),scheduleConfig.getVendor(),scheduleConfig.getVersion());
        //网优平台文件存放路径
        String wyptFilePath = filePathGenerate.getWyptPath(datatime,scheduleConfig.getProvince(),scheduleConfig.getNetype(),scheduleConfig.getVendor(),scheduleConfig.getVersion());
        //获取话务网管文件存放的路径
        String hwwgFilePath = filePathGenerate.getHwwgPath(datatime,scheduleConfig.getProvince(),scheduleConfig.getNetype(),scheduleConfig.getVendor(),scheduleConfig.getVersion());

        //北向、OMC、网优平台存放中间结果的路径 省份/网元类型/厂家/版本号
        String tmpFilePrefix = scheduleConfig.getProvince()+ File.separator+scheduleConfig.getNetype()
                                +File.separator + scheduleConfig.getVendor()+File.separator + scheduleConfig.getVersion();
        corBusiness.startBusiness(bxfilePath,omcFilePath,wyptFilePath,hwwgFilePath,scheduleTask,scheduleConfig.getNetype(),scheduleConfig.getDatatype(),datatime,scheduleConfig.getVendor(),scheduleConfig.getVersion(),tmpFilePrefix,scheduleConfig.getCompare());
    }

}
