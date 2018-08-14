package com.cn.chinamobile.business;

import com.cn.chinamobile.pojo.mybatis.UdpTask;
import com.cn.chinamobile.resource.IniDomain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author xueweixia
 */
public class UdpThread implements Runnable{

    private UdpTask udpTask = null;
    private List<String> udpfilePath = new ArrayList<>();

    public UdpThread(List<String> udpFilePaths,UdpTask udpTask){
        this.udpfilePath = udpFilePaths;
        this.udpTask = udpTask;
    }


    @Override
    public void run() {
        UdpBusiness udpBusiness = IniDomain.ct.getBean(UdpBusiness.class);
        udpBusiness.setParas(udpfilePath,udpTask);
        udpBusiness.startBusiness();
    }
}
