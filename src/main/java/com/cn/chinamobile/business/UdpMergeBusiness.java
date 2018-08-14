package com.cn.chinamobile.business;

import com.cn.chinamobile.pojo.mybatis.UdpAll;
import com.cn.chinamobile.pojo.mybatis.UdpTask;
import com.cn.chinamobile.service.UdpAllService;
import com.cn.chinamobile.util.FilePathGenerate;
import com.cn.chinamobile.util.FileUtil;
import com.cn.chinamobile.util.Log;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xueweixia
 */
@Service
public class UdpMergeBusiness {

    FilePathGenerate filePathGenerate = new FilePathGenerate();
    FileUtil fileUtil = new FileUtil();

    @Resource
    private UdpAllService udpAllService;

    public void start(String filetype,String type,String times){
        List<UdpAll> udpallTasks =udpAllService.selectDisTask(filetype,type);
        //定义线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        for(UdpAll task : udpallTasks) {
            String udproot = filePathGenerate.getUdpRootPath(filetype,task.getProvince(), task.getNetype(), task.getVendor(), task.getType());
            List<String> udppaths = new ArrayList<>();
            String[] lstime = times.split(",");
            //将符合条件的路径全部加载到udppaths
            for (String time : lstime) {
                List<String> dates = fileUtil.findUdpDatePath(udproot, time);
                for (String datetime : dates) {
                    String udpPath = filePathGenerate.getUdpPath(filetype,datetime, task.getProvince(), task.getNetype(), task.getVendor(), task.getType());
                    udppaths.add(udpPath);
                }
            }

            if(udppaths.size()>0){
                try{
                    System.out.println("开始执行任务："+ times+task.getProvince()+task.getNetype()+task.getVendor()+task.getType());

                    //根据参数，生产UdpTask对象
                    UdpTask udpTask = new UdpTask(times,task.getProvince(),task.getNetype(),task.getVendor(),task.getType(),task.getVersion());

                    UdpThread udpThread = new UdpThread(udppaths,udpTask);
                    threadPool.submit(udpThread);
                }catch (Exception e){
                    Log.error("任务执行失败：" + times + task.getProvince() + task.getNetype() + task.getVendor() + task.getType(), e);
                }

            }
        }
        threadPool.shutdown();
    }
}
