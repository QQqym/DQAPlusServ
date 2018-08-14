package com.cn.chinamobile.compare;

import com.cn.chinamobile.pojo.mybatis.LogNbiCounterPM;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.service.LogNbiCounterPMService;
import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by zh on 2017/6/25.
 * 北向与Counter的PM对比
 */
public class BXCounterCompare implements Callable {
    private String vendorName = "";
    private String elementType = "";
    private String pmVersion = "";
    private String objectType = "";
    private LogNbiCounterPMService logNbiCounterPMService;
    private File bxpmFile;
    private File omcPmFile;
    private int taskid;
    private String parsetime;
    private String provinceCHName;

    public BXCounterCompare(String vendorName,String elementType ,String pmVersion ,String objectType,File bxpmFile,File omcPmFile,int taskid,String parsetime,String provinceCHName){
        super();
        this.vendorName = vendorName;
        this.elementType = elementType;
        this.pmVersion = pmVersion;
        this.objectType = objectType;
        this.bxpmFile = bxpmFile;
        this.omcPmFile = omcPmFile;
        this.taskid = taskid;
        this.parsetime = parsetime;
        this.provinceCHName = provinceCHName;
    }

    @Override
    public Boolean call() throws Exception {
        Compare compare = new Compare(vendorName,elementType,pmVersion,objectType,provinceCHName);
        String compareFilePath = bxpmFile.getParent()+File.separator +"log_nbi_counter_"+elementType;
        try{
            compare.compare2File(bxpmFile,omcPmFile,compareFilePath);
        }catch (Exception e){
            Log.error("compare bx and omc pm error,bxfile is:"+bxpmFile.getAbsolutePath()+" and omcfile is:"+omcPmFile.getAbsolutePath(),e);
            return false;
        }

        logNbiCounterPMService = IniDomain.ct.getBean(LogNbiCounterPMService.class);
        File logfile = new File(compareFilePath);
        insertLog(logfile,"\\|");
        return true;
    }

    private void insertLog(File file,String splitchar){
        String starttime = parsetime.substring(0,parsetime.lastIndexOf("-"))+" " + parsetime.substring(parsetime.lastIndexOf("-")+1)+":00:00";
        BufferedReader srcReader = null ;
        try {
            srcReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), ContentInfo.ENCODING));
            String line;
            String tablename = file.getName();
            //根据taskid删除已经入库的数据
            logNbiCounterPMService.deletebytaskid(tablename,taskid);
            int count = 0;
            List<LogNbiCounterPM> batchList = new ArrayList<>(1600);
            while ((line = srcReader.readLine()) != null) {
                count++;
                String[] ds = line.split(splitchar,-1);
                LogNbiCounterPM logNbiCounterPM = new LogNbiCounterPM(ds,taskid,starttime);
                logNbiCounterPM.setTablename(tablename);
                batchList.add(logNbiCounterPM);
                //批量入库一千条
                if(count%1000==0){
                    logNbiCounterPMService.batchinsert(tablename,batchList);
                    //入库完清空list
                    batchList.clear();
                }
            }
            if(batchList.size()>0)
                logNbiCounterPMService.batchinsert(tablename,batchList);
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
