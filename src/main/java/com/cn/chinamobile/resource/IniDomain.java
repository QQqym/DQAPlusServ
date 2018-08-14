package com.cn.chinamobile.resource;

import com.cn.chinamobile.entity.IndexConfig;
import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.QciConfigUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;
import java.util.*;

/**
 * Created by zh on 2017/4/25.
 */
public class IniDomain {
    public static Map<String, List<String>> qcimap;
    public static Map<String, List<String>> qcimapdesc;
    public static Map<String,String> pmdescconfig;
    public static List<String> leftpms;
    public static List<String> leftkpis;
    public static Map<String,List<String>> kpiconfigMap;
    public static Map<String,Map<String,Map<String,String>>> versionconfig;
    public static Map<String,Map<String,Map<String,String>>> kpialgorithmConfig;
    public static Map<String,Map<String,String>> titleconfig;
    public static Map<String,String> sumlgorithmMap;
    public static ApplicationContext ct;
    public static Map<String,IndexConfig> indexConfigHashMap;
    public static Map<String,Map<String,String>> title;
    public static Map<String,Map<String,String>> udpfileconfig;

    public static void initSpringCfg(String configPath){
        ct=new FileSystemXmlApplicationContext(configPath);
    }
    public static void initResource(){
        qcimap = QciConfigUtil.readQciConfig(ContentInfo.DQA_VERSION_BASED_PATH+ContentInfo.PMCONFIG_FILE);
        qcimapdesc = QciConfigUtil.readQciConfigDesc(ContentInfo.DQA_VERSION_BASED_PATH+ContentInfo.PMCONFIG_FILE);
        kpiconfigMap =QciConfigUtil.readKPIConfig(ContentInfo.DQA_VERSION_BASED_PATH+ContentInfo.KPICONFIG_FILE);
        versionconfig = QciConfigUtil.readVendoraVersionCounterConfig(ContentInfo.DQA_VERSION_BASED_PATH+ContentInfo.VENDOR_VERSION_CONFIG);
        kpialgorithmConfig = QciConfigUtil.readVendoraVersionKPIConfig(ContentInfo.DQA_VERSION_BASED_PATH+ContentInfo.KPIALGORITHM_CONFIG);
        titleconfig = QciConfigUtil.readTitleFileConfig(ContentInfo.DQA_VERSION_BASED_PATH+ContentInfo.PMTYPE_TITLE_CONFIG);
        sumlgorithmMap = QciConfigUtil.getSUMAlgorithmMap(ContentInfo.DQA_VERSION_BASED_PATH+ContentInfo.SUM_CONFIG);
        pmdescconfig = QciConfigUtil.readPmDescFileConfig(ContentInfo.DQA_VERSION_BASED_PATH+ContentInfo.PMDESC_FILE);
        leftpms = QciConfigUtil.getLeftPMS(ContentInfo.DQA_VERSION_BASED_PATH+ContentInfo.LEFT_PMKPI_FILE);
        leftkpis = QciConfigUtil.getLeftKPIS(ContentInfo.DQA_VERSION_BASED_PATH+ContentInfo.LEFT_PMKPI_FILE);
        indexConfigHashMap = QciConfigUtil.getIndexConfigMap(ContentInfo.ROOT_PATH + "conf/indexconfig.xml");

    }

    public static void initCheck(){
        title = QciConfigUtil.readTitle(ContentInfo.DQA_VERSION_BASED_PATH+ File.separator+"title.xml");
    }

    public static void initUdpConfig(){
        udpfileconfig = QciConfigUtil.readUdpConf(ContentInfo.DQA_VERSION_BASED_PATH + ContentInfo.UDP_FILE_PATH);
    }

}
