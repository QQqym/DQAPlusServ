package com.cn.chinamobile.compare;

import com.cn.chinamobile.pojo.mybatis.LogNbiCounterPM;
import com.cn.chinamobile.pojo.mybatis.LogNbiWyptKPI;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.service.LogNbiCounterPMService;
import com.cn.chinamobile.service.LogNbiWyptKPIService;
import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by zh on 2017/7/5.
 * 北向与网优平台KPI对比
 */
public class BXWyptKpiCompare implements Callable {
    private String vendorName = "";
    private String elementType = "";
    private String pmVersion = "";
    private String objectType = "";
    private LogNbiWyptKPIService logNbiWyptKPIService;
    private File bxkpiFile;
    private File wyptkpiFile;
    private int taskid;
    private String provinceCHName;

    public BXWyptKpiCompare(String vendorName,String elementType ,String pmVersion ,String objectType,File bxkpiFile,File wyptkpiFile,int taskid,String provinceCHName){
        super();
        this.vendorName = vendorName;
        this.elementType = elementType;
        this.pmVersion = pmVersion;
        this.objectType = objectType;
        this.bxkpiFile = bxkpiFile;
        this.wyptkpiFile = wyptkpiFile;
        this.taskid = taskid;
        this.provinceCHName = provinceCHName;
    }

    @Override
    public Boolean call() throws Exception {
        Compare compare = new Compare(vendorName,elementType,pmVersion,objectType,provinceCHName);
        String compareFilePath = bxkpiFile.getParent()+File.separator +"Log_NBI_WYPT_KPI_"+elementType;
        try{
            compare.compare2File(bxkpiFile,wyptkpiFile,compareFilePath);
        }catch (Exception e){
            Log.error("compare bx and wypt kpi error,bxfile is:"+bxkpiFile.getAbsolutePath()+" and wyptfile is:"+wyptkpiFile.getAbsolutePath(),e);
            return false;
        }

        logNbiWyptKPIService = IniDomain.ct.getBean(LogNbiWyptKPIService.class);
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
            logNbiWyptKPIService.deletebytaskid(tablename,taskid);
            int count = 0;
            List<LogNbiWyptKPI> batchList = new ArrayList<>(1600);
            while ((line = srcReader.readLine()) != null) {
                count++;
                String[] ds = line.split(splitchar,-1);
                LogNbiWyptKPI logNbiWyptKPI = new LogNbiWyptKPI(ds,taskid);
                logNbiWyptKPI.setTablename(tablename);
                batchList.add(logNbiWyptKPI);
                //批量入库一千条
                if(count%1000==0){
                    logNbiWyptKPIService.batchinsert(tablename,batchList);
                    //入库完清空list
                    batchList.clear();
                }
            }
            if(batchList.size()>0)
                logNbiWyptKPIService.batchinsert(tablename,batchList);
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
