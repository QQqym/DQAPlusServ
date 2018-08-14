package com.cn.chinamobile.business;

import com.cn.chinamobile.dao.Dao;
import com.cn.chinamobile.dao.TempDao;
import com.cn.chinamobile.entity.ColType;
import com.cn.chinamobile.entity.CompareEntity;
import com.cn.chinamobile.parse.ParseXml;
import com.cn.chinamobile.pojo.mybatis.GlobalField;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.service.CityInfoService;
import com.cn.chinamobile.service.DynamicSqlService;
import com.cn.chinamobile.service.GlobalFieldService;
import com.cn.chinamobile.service.ProvinceService;
import com.cn.chinamobile.util.*;
import org.mozilla.intl.chardet.nsDetector;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by zh on 2017/5/22.
 * 北向业务类
 */
@Service
public class BXBusiness implements Callable {
    private List<File> bxFiles ;
    private List<File> bxcheckFiles;
    private String neType;
    private String vendorname;
    private String version;
    private String dataType;
    private String parsetime;
    private String tmpFilePrefix;
    private String vendor;
    private int taskid;
    private Dao dao;
    private PMKPIBusiness pmkpiBusiness;
    private File parse_file;

    @Resource
    private CityInfoService cityInfoService;

    @Resource
    private ProvinceService provinceService;
    //存储省份信息
    private Map<String,String> provinceMap;

    //存储地市信息
    private Map<String,String> cityMap;

    private String province;

    public void setParas(List<File> bxFiles,List<File> bxcheckFiles,int taskid,String neType,String dataType,String parsetime,String vendorname, String version,String tmpFilePrefix,File op_file ,String province,String vendor){
        this.bxFiles = bxFiles;
        this.bxcheckFiles = bxcheckFiles;
        this.taskid = taskid;
        this.neType = neType;
        this.dataType = dataType;
        this.parsetime = parsetime;
        this.vendorname = vendorname;
        this.version = version;
        this.tmpFilePrefix = tmpFilePrefix;
        this.parse_file= op_file;
        this.province = province;
        this.vendor=vendor;
    }

    @Override
    public CompareEntity call() throws Exception {

        //查询省份、地市、厂家信息
        provinceMap = provinceService.selectAll();
        cityMap = cityInfoService.getCityInfo();

        //返回解析后的PM文件路径
        File op_file = null;
        if(!neType.equalsIgnoreCase("ENB")){
            //定义check文件的map
            Map<String,Long> checkFileMap = new HashMap<>();
            DataUtil dataUtil = new DataUtil();
            //读取bxcheck文件
            for(File checkFile : bxcheckFiles){
                String encode = new FileCharsetDetector().guessFileEncoding(checkFile, new nsDetector());
                checkFileMap.putAll(dataUtil.readCheckFile(checkFile.getAbsolutePath(),encode));
            }

            try{
                ParseXml parseXml = new ParseXml();
                op_file = parseXml.parse(bxFiles,taskid,neType,dataType,parsetime,tmpFilePrefix,checkFileMap,provinceMap,cityMap,null,province,vendor);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            op_file = parse_file;
        }


        //实例化临时库连接
        dao = IniDomain.ct.getBean(Dao.class);

        pmkpiBusiness = new PMKPIBusiness(neType,dataType,parsetime,vendorname,version,tmpFilePrefix,dao);

        //创建临时表
        String tablename = pmkpiBusiness.createtmpTable("bx_",IniDomain.qcimap.get(neType+"-"+dataType));

        //将数据导入临时表
        if(neType.equalsIgnoreCase("ENB")){
            //ENB数据单独处理
            pmkpiBusiness.importENBData(op_file,tablename,parsetime);

        }else {
            pmkpiBusiness.importData(op_file,tablename);
        }

        //汇总PM
        File pmsumFile = pmkpiBusiness.getSumPM(tablename,"bxsum_",ContentInfo.GROUP_COLUMN);

        //创建汇总表
        String sumtable = pmkpiBusiness.createsmTable("bxsum_");
        //将汇总数据入库
        pmkpiBusiness.importData(pmsumFile,sumtable);

        //获取KPI计算指标
        File kpiFile =  pmkpiBusiness.getKPIs(sumtable,"bxkpi_");

        dao.closeConnect();
        CompareEntity compareEntity = new CompareEntity(pmsumFile,kpiFile);
        return compareEntity;
    }



}
