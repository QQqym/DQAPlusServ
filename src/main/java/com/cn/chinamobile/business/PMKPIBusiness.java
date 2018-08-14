package com.cn.chinamobile.business;

import com.cn.chinamobile.dao.Dao;
import com.cn.chinamobile.entity.ColType;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.service.DynamicSqlService;
import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.DataUtil;
import com.cn.chinamobile.util.FileUtil;
import com.cn.chinamobile.util.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zh on 2017/6/22.
 * 完成PM1小时汇总以及KPI汇总
 */

public class PMKPIBusiness {
    private String neType;
    private String vendorname;
    private String version;
    private String dataType;
    private String parsetime;
    private String tmpFilePrefix;
    private Dao dao;
    private Map<String,String> kpiexpresionMap;
    private LinkedHashMap<String, ColType> colTypeLinkedHashMap;
    private DynamicSqlService dynamicSqlService;

    public PMKPIBusiness(String neType,String dataType,String parsetime,String vendorname, String version,String tmpFilePrefix,Dao dao ){
        this.neType = neType;
        this.dataType = dataType;
        this.parsetime = parsetime;
        this.vendorname = vendorname;
        this.version = version;
        this.tmpFilePrefix = tmpFilePrefix;
        this.dao = dao;
    }

    /**
     * 创建15min pm临时表
     * @return
     */
    public String  createtmpTable(String prefix,List<String> pms){
        String tablename = prefix +tmpFilePrefix.replace(File.separator,"_") + "_" + dataType + "_"+parsetime.replace("-","");
        tablename = tablename.replace(".","");
        try {
            String deletesql = "drop table if exists " + tablename ;
            Log.info(deletesql);
            dao.exeSql(deletesql);
            dynamicSqlService = IniDomain.ct.getBean(DynamicSqlService.class);
            colTypeLinkedHashMap = dynamicSqlService.getPMColMap(pms,IniDomain.sumlgorithmMap);
            String bxpmcreatesql = dynamicSqlService.getCreatePMSql(tablename,colTypeLinkedHashMap);
            Log.info(bxpmcreatesql);
            dao.exeSql(bxpmcreatesql);
        }catch (Exception e){
            Log.error("create tmp table error:"+tablename,e);
        }
        return tablename;
    }

    /**
     * 将文件的数据导入数据库
     * @param file 文件
     * @param tablename 表名
     */
    public void importData(File file,String tablename){
        try{
            dynamicSqlService.importData(file.getAbsolutePath(),tablename,colTypeLinkedHashMap,dao.getConnection(),"\\|",ContentInfo.ENCODING);
        }catch (Exception e){
            Log.error("将文件load到数据库错误，文件名"+ file.getAbsolutePath(),e);
        }

    }

    /**
     * 将ENB的pm文件的数据导入数据库
     * @param file 文件
     * @param tablename 表名
     */
    public void importENBData(File file,String tablename,String parsetime){
        try{
            dynamicSqlService.importENBPMData(file.getAbsolutePath(),tablename,colTypeLinkedHashMap,dao.getConnection(),"\\|",ContentInfo.ENCODING,parsetime);
        }catch (Exception e){
            Log.error("将文件load到数据库错误，文件名"+ file.getAbsolutePath(),e);
        }

    }

    /**
     * 创建1h 汇总表
     * @return
     */
    public String  createsmTable(String prefix){
        String tablename = prefix +tmpFilePrefix.replace(File.separator,"_") + "_" + dataType + "_"+parsetime.replace("-","");
        tablename = tablename.replace(".","");
        try {
            String deletesql = "drop table if exists " + tablename ;
            dao.exeSql(deletesql);
            String bxpmcreatesql = dynamicSqlService.getCreatePMSql(tablename,colTypeLinkedHashMap);
            dao.exeSql(bxpmcreatesql);
        }catch (Exception e){
            Log.error("create tmp table error:"+tablename,e);
        }
        return tablename;
    }

    /**
     * 汇总PM，15min to 1h
     * @param tablename
     * @return
     */
    public File getSumPM(String tablename,String prefix,String groupbycolumn){
        //获取汇总的sql
        Map<String,List<String>> summap =  dynamicSqlService.getSumPMSql(tablename,colTypeLinkedHashMap,IniDomain.sumlgorithmMap, groupbycolumn);
        String sumcoutersql = "";
        List<String> pms = null;
        for(Map.Entry entry : summap.entrySet()){
            sumcoutersql = (String) entry.getKey();
            pms = (List<String>) entry.getValue();
        }
        List<Map<String ,String>> pmdatas = dao.getResultSetMap(sumcoutersql,pms);
        String filename = ContentInfo.FILE_TEMP_PATH+File.separator+tmpFilePrefix+File.separator+dataType.toLowerCase()
                +File.separator+parsetime+File.separator+prefix+dataType.toLowerCase();
        File pmSumfile = new File(filename);

        //如果文件存在则删除
        if(pmSumfile.exists())
            pmSumfile.delete();
        new FileUtil().writedatas(pmSumfile,pmdatas,"|");

        //清空数据引用
        pmdatas.clear();
        return pmSumfile;
    }

    /**
     * 获取1h小区粒度的kpi
     * @param tablename pm 一小时表
     * @return
     */
    public File getKPIs(String tablename,String prefix){
        String kpipath = ContentInfo.DQA_VERSION_BASED_PATH+ File.separator+"kpi"+File.separator;

        //如果没有配置该子网元的KPI，返回null
        if(IniDomain.kpialgorithmConfig.get(dataType) == null){
            return null;
        }
        String kpiconfig = kpipath+ IniDomain.kpialgorithmConfig.get(dataType).get(vendorname).get(version);
        kpiexpresionMap = new DataUtil().getExpressionMap(kpiconfig);

        Map<String,List<String>> kpiMap = dynamicSqlService.getKPISql(tablename,colTypeLinkedHashMap,kpiexpresionMap);
        String kpisql = "";
        List<String> kpis = null;
        for(Map.Entry entry : kpiMap.entrySet()){
            kpisql = (String) entry.getKey();
            kpis = (List<String>) entry.getValue();
        }
        List<Map<String ,String>> pmdatas = dao.getResultSetMap(kpisql,kpis);
        String filename = ContentInfo.FILE_TEMP_PATH+File.separator+tmpFilePrefix+File.separator+dataType.toLowerCase()
                +File.separator+parsetime+File.separator+prefix+dataType.toLowerCase();
        File kpifile = new File(filename);

        //如果文件存在则删除
        if(kpifile.exists())
            kpifile.delete();
        new FileUtil().writedatas(kpifile,pmdatas,"|");

        //清空数据引用
        pmdatas.clear();
        return kpifile;
    }

}
