package com.cn.chinamobile.business;

import com.cn.chinamobile.parse.CheckDn;
import com.cn.chinamobile.util.FileUtil;
import com.cn.chinamobile.util.GZIPUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhou on 2017/11/21.
 */
public class CheckDnThread implements Runnable {
    //存储省份信息
    private Map<String,String> provinceMap;

    //存储地市信息
    private Map<String,String> cityMap;

    //存储厂家信息
    private Map<String,String> vendorMap;

    private String udpPath;

    private List<File> fileList = new ArrayList<>(50);

    FileUtil fileUtil = new FileUtil();

    public CheckDnThread(String udpPath ,Map<String,String> provinceMap, Map<String,String> cityMap,Map<String,String> vendorMap){
        this.udpPath = udpPath;
        this.provinceMap = provinceMap;
        this.cityMap = cityMap;
        this.vendorMap = vendorMap;
    }
    @Override
    public void run() {
        fileUtil.findAllFiles(udpPath,fileList);
        if(fileList.size()>0){
            CheckDn checkDn = new CheckDn(cityMap,provinceMap,vendorMap);
            for(File file : fileList){
                if(file.getName().contains(".gz")){
                    File defile = new GZIPUtil().decompress(file);
                    if(defile != null){
                        checkDn.parseFile(defile, true,"dn",null);
                    }
                }else {
                    checkDn.parseFile(file, false,"dn",null);
                }
            }
        }
    }

}
