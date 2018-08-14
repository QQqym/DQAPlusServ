package com.cn.chinamobile.business;

import com.cn.chinamobile.pojo.mybatis.UdpAll;
import com.cn.chinamobile.service.CityInfoService;
import com.cn.chinamobile.service.ProvinceService;
import com.cn.chinamobile.service.UdpAllService;
import com.cn.chinamobile.service.VendorService;
import com.cn.chinamobile.util.FilePathGenerate;
import com.cn.chinamobile.util.FileUtil;
import com.cn.chinamobile.util.Log;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhou on 2017/11/21.
 */
@Service
public class CheckDnBusiness {

    FilePathGenerate filePathGenerate = new FilePathGenerate();
    FileUtil fileUtil = new FileUtil();

    //存储省份信息
    private Map<String,String> provinceMap;

    //存储地市信息
    private Map<String,String> cityMap;

    //存储厂家信息
    private Map<String,String> vendorMap;

    @Resource
    private CityInfoService cityInfoService;

    @Resource
    private ProvinceService provinceService;

    @Resource
    private VendorService vendorService;

    @Resource
    private UdpAllService udpAllService;

    public void start(String type,String time){
        List<UdpAll> udpallTasks =udpAllService.selectByType("UDP",type);
        Log.info("总路径数：" + udpallTasks.size());

        //查询省份、地市、厂家信息
        provinceMap = provinceService.selectAll();
        vendorMap = vendorService.selectAll();
        cityMap = cityInfoService.getCityInfo();

        //定义线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        for(UdpAll task : udpallTasks){
            String province = task.getProvince();
            String udproot = filePathGenerate.getUdpRootPath("UDP",province,task.getNetype(),task.getVendor(),task.getType());
            List<String> dates = fileUtil.findUdpDatePath(udproot,time);
            for(String datetime : dates){
                Log.info("路径"+udproot + "下查到的时间点数："+dates.size());
                try{
                    //获取Udp路径
                    String udpPath = filePathGenerate.getUdpPath("UDP",datetime,province,task.getNetype(),task.getVendor(),task.getType());

                    CheckDnThread cnThread = new CheckDnThread(udpPath,provinceMap,cityMap,vendorMap);
                    threadPool.submit(cnThread);
                }catch (Exception e){
                    Log.error("任务执行失败：" + datetime + province + task.getNetype() + task.getVendor() + task.getType(), e);
                }

            }

        }
        threadPool.shutdown();
    }
}
