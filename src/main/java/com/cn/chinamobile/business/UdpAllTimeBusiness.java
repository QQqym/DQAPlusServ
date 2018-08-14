package com.cn.chinamobile.business;

import com.cn.chinamobile.pojo.mybatis.UdpAll;
import com.cn.chinamobile.pojo.mybatis.UdpTask;
import com.cn.chinamobile.service.UdpAllService;
import com.cn.chinamobile.util.FilePathGenerate;
import com.cn.chinamobile.util.Log;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhou on 2017/12/1.
 */
@Service
public class UdpAllTimeBusiness {

    FilePathGenerate filePathGenerate = new FilePathGenerate();

    @Resource
    private UdpAllService udpAllService;

    public void start(String filetype,String type){
        List<UdpAll> udpallTasks =udpAllService.selectByType(filetype,type);
        Log.info("总路径数：" + udpallTasks.size());
        //定义线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        for(UdpAll task : udpallTasks){
            String province = task.getProvince();
            String datetime = task.getDatetime();
            try{
                System.out.println("开始执行任务："+ datetime+province+task.getNetype()+task.getVendor()+task.getType());
                //获取Udp路径
                String udpPath = filePathGenerate.getUdpPath(filetype,datetime,province,task.getNetype(),task.getVendor(),task.getType());
                List<String> udppaths = new ArrayList<>();
                udppaths.add(udpPath);
                //根据参数，生产UdpTask对象
                UdpTask udpTask = new UdpTask(datetime,province,task.getNetype(),task.getVendor(),task.getType(),task.getVersion());

                UdpThread udpThread = new UdpThread(udppaths,udpTask);
                threadPool.submit(udpThread);
            }catch (Exception e){
                Log.error("任务执行失败：" + datetime + province + task.getNetype() + task.getVendor() + task.getType(), e);
            }
        }
        threadPool.shutdown();
    }
}
