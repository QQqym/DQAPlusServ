package com.cn.chinamobile.compare;

import com.cn.chinamobile.pojo.mybatis.LogNbiHwwgKpi;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.service.LogNbiHwwgKpiService;
import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

/**
 * @author xueweixia
 * 北向与话务网管的KPI对比
 */
public class BxHwwgKpiCompare implements Callable {

    private String vendorName = "";
    private String elementType = "";
    private String pmVersion = "";
    private String objectType = "";
    private LogNbiHwwgKpiService logNbiHwwgKpiService;
    private File bxkpiFile;
    private File hwwgFile;
    private int taskid;
    private String provinceCHName;


    public BxHwwgKpiCompare(String vendorName,String elementType ,String pmVersion ,String objectType,File bxkpiFile,File hwwgFile,int taskid,String provinceCHName){
        super();
        this.vendorName = vendorName;
        this.elementType = elementType;
        this.pmVersion = pmVersion;
        this.objectType = objectType;
        this.bxkpiFile = bxkpiFile;
        this.hwwgFile = hwwgFile;
        this.taskid = taskid;
        this.provinceCHName = provinceCHName;
    }

    @Override
    public Boolean call() throws Exception {
        Compare compare = new Compare(vendorName,elementType,pmVersion,objectType,provinceCHName);
        String compareFilePath = bxkpiFile.getParent()+File.separator +"log_nbi_hwwg_kpi_"+elementType;
        try{
            compare.compare2File(bxkpiFile,hwwgFile,compareFilePath);
        }catch (Exception e){
            Log.error("compare bx and hwwg kpi error,bxfile is:"+bxkpiFile.getAbsolutePath()+" and hwwgfile is:"+hwwgFile.getAbsolutePath(),e);
            return false;
        }

        logNbiHwwgKpiService = IniDomain.ct.getBean(LogNbiHwwgKpiService.class);
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
            logNbiHwwgKpiService.deletebytaskid(tablename,taskid);

            while ((line = srcReader.readLine()) != null) {
                String[] ds = line.split(splitchar,-1);
                LogNbiHwwgKpi logNbiHwwgKpi = new LogNbiHwwgKpi(ds,taskid);
                logNbiHwwgKpi.setTablename(tablename);
                logNbiHwwgKpiService.insert(logNbiHwwgKpi);
            }
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
