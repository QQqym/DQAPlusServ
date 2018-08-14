package com.cn.chinamobile.compare;

import com.cn.chinamobile.pojo.mybatis.LogNbiWyptKPI;
import com.cn.chinamobile.pojo.mybatis.LogNbiWyptPM;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.service.LogNbiWyptKPIService;
import com.cn.chinamobile.service.LogNbiWyptPMService;
import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by zh on 2017/6/25.
 * 北向与网优平台的PM对比
 */
public class BXWyptPMCompare implements Callable {
    private String vendorName = "";
    private String elementType = "";
    private String pmVersion = "";
    private String objectType = "";
    private LogNbiWyptPMService logNbiWyptPMService;
    private File bxpmFile;
    private File wyptpmFile;
    private int taskid;
    private String provinceCHName;

    public BXWyptPMCompare(String vendorName,String elementType ,String pmVersion ,String objectType,File bxpmFile,File wyptpmFile,int taskid,String provinceCHName){
        super();
        this.vendorName = vendorName;
        this.elementType = elementType;
        this.pmVersion = pmVersion;
        this.objectType = objectType;
        this.bxpmFile = bxpmFile;
        this.wyptpmFile = wyptpmFile;
        this.taskid = taskid;
        this.provinceCHName = provinceCHName;
    }

    @Override
    public Boolean call() throws Exception {
        Compare compare = new Compare(vendorName,elementType,pmVersion,objectType,provinceCHName);
        String compareFilePath = bxpmFile.getParent()+File.separator +"Log_NBI_WYPT_PM_"+elementType;
        try{
            compare.compare2File(bxpmFile,wyptpmFile,compareFilePath);
        }catch (Exception e){
            Log.error("compare bx and wypt pm error,bxfile is:"+bxpmFile.getAbsolutePath()+" and wyptfile is:"+wyptpmFile.getAbsolutePath(),e);
            return false;
        }

        logNbiWyptPMService = IniDomain.ct.getBean(LogNbiWyptPMService.class);
        File logfile = new File(compareFilePath);
        insertLog(logfile,"\\|");
        return true;
    }

    private void insertLog(File file,String splitchar){
        BufferedReader srcReader = null;
        try {
            srcReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), ContentInfo.ENCODING));
            String line;
            String tablename = file.getName();
            //根据taskid删除已经入库的数据
            logNbiWyptPMService.deletebytaskid(tablename,taskid);
            int count = 0;
            List<LogNbiWyptPM> batchList = new ArrayList<>(1600);
            while ((line = srcReader.readLine()) != null) {
                count++;
                String[] ds = line.split(splitchar,-1);
                LogNbiWyptPM logNbiWyptPM = new LogNbiWyptPM(ds,taskid);
                logNbiWyptPM.setTablename(tablename);
                batchList.add(logNbiWyptPM);
                //批量入库一千条
                if(count%1000==0){
                    logNbiWyptPMService.batchinsert(tablename,batchList);
                    //入库完清空list
                    batchList.clear();
                }

            }
            if(batchList.size()>0)
                logNbiWyptPMService.batchinsert(tablename,batchList);
        }catch (Exception e){
            Log.error("insert compare result error,file:"+file.getAbsolutePath(),e);
        }finally {
            if(srcReader != null){
                try{
                    srcReader.close();
                }catch (Exception e){

                }
            }
        }

    }
}
