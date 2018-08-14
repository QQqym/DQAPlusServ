package com.cn.chinamobile.util;


import java.util.Properties;

/**
 * 常量控制类
 */
public class ContentInfo {
    //根路径
    public static final String ROOT_PATH = System.getProperty("user.dir")+"/";

    public static final String CONF_PATH="conf/config.properties";
//    //配置文件存储报表字段
//    public static final String EXCEL_COLUMN="conf/excel_column.properties";

    private static final Properties DATA_PROP = ConfigUtils.getConf(CONF_PATH);
//    private static final Properties EXCEL_COLUMN_PROP = ConfigUtils.getConf(EXCEL_COLUMN);

    public static final String RESOURCE_FILE_PATH = DATA_PROP.getProperty("INIT_DATA_PATH");
    public static final String REDIS_HOTS = DATA_PROP.getProperty("REDIS.HOST");
    public static final String REDIS_PORT = DATA_PROP.getProperty("REDIS.PORT");
    public static final String FILE_TEMP_PATH = DATA_PROP.getProperty("FILE_TEMP_PATH");
    public static final String MYSQL_TEMP_URL = DATA_PROP.getProperty("MYSQL_TEMP_URL");
    public static final String MYSQL_DATA_URL = DATA_PROP.getProperty("MYSQL_DATA_URL");
    public static final String SUM_CONFIG = DATA_PROP.getProperty("SUM_CONFIG");
    public static final String DQA_VERSION_BASED_PATH = ROOT_PATH + DATA_PROP.getProperty("VERSION_CONFIG");
    public static String PMCONFIG_FILE = DATA_PROP.getProperty("PMCONFIG_FILE");
    public static String KPICONFIG_FILE = DATA_PROP.getProperty("KPICONFIG_FILE");
    public static String VENDOR_VERSION_CONFIG = DATA_PROP.getProperty("VENDOR_VERSION_CONFIG");
    public static String PMTYPE_TITLE_CONFIG = DATA_PROP.getProperty("PMTYPE_TITLE_CONFIG");
    public static String KPIALGORITHM_CONFIG = DATA_PROP.getProperty("KPI_ALGORITHM_CONFIG");
    public static String PMDESC_FILE = DATA_PROP.getProperty("PMDESC");
    public static String LEFT_PMKPI_FILE = DATA_PROP.getProperty("LEFT_PMKPI_FILE");
    public static String UDP_FILE_PATH = DATA_PROP.getProperty("UDP_VERSION_CONFIG");

    public static final String SOURCE_FILE_ROOT_PATH = DATA_PROP.getProperty("SOURCE_FILE_ROOT_PATH");

    public static final String FAIL_NUM=DATA_PROP.getProperty("FAIL_NUM");
    public static final String ENCODING = DATA_PROP.getProperty("ENCODING");

    //pm 1h汇总时的分组字段
    public static final String GROUP_COLUMN = DATA_PROP.getProperty("GROUPBY_COLUMENAME");
    public static final String COUNTER_GROUPBY_COLUMENAME ="小区名称（userlabel）,小区名称(userlabel)"; //DATA_PROP.getProperty("COUNTER_GROUPBY_COLUMENAME");



    public static final String COUNTER_MME_GCOLUMN ="网元设备名称,NE_NAME"; //DATA_PROP.getProperty("COUNTER_MME_GCOLUMN");
    public static final String COUNTER_SWVERION = DATA_PROP.getProperty("COUNTER_SWVERION");

    //数字正则
    public static final String FLOAT_REGEX = "^\\d*[0-9|\\.]\\d*$";

    //文件中的标签
    public static final String VENDOR_NAME = "VendorName";
    public static final String BEGIN_TIME = "BeginTime";
    public static final String END_TIME = "EndTime";
    public static final String DATE_TIME = "DateTime";
    public static final String OBJECT_TYPE = "ObjectType";
    public static final String DN_PREFIX = "DnPrefix";
    public static final String PMNAME = "PmName";
    public static final String N = "N";
    public static final String V = "V";
    public static final String CV = "CV";
    public static final String SN = "SN";
    public static final String SV = "SV";
    public static final String PM = "Pm";
    public static final String CM = "Cm";
    public static final String MEASURENTS = "Measurements";
    public static final String PMFILE = "PmFile";
    public static final String OBJECT = "Object";

    public static final String  FIELDNAME="FieldName";

    //各环节标识
    //北向解析标识
    public static final String BX_PARSE = "bxparse";
    //omc解析标识
    public static final String OMC_PARSE = "omcparse";
    //网优平台解析标识
    public static final String WYPT_PARSE = "wyptparse";
    //话务网管解析标识
    public static final String HWWG_PARSE = "hwwgparse";
    //北向与omc对比标识
    public static final String LOG_NBI_COUNTER = "log_nbi_counter";
    //北向与网优平台对比标识
    public static final String LOG_NBI_WYPT = "log_nbi_wypt";
    //北向与话务网管对比标识
    public static final String LOG_NBI_HWWG = "log_nbi_hwwg";
    //北向入库标识
    public static final String BXIMPORT = "bximport";
    //omc入库标识
    public static final String OMCIMPORT = "omcimport";
    //网优平台入库标识
    public static final String WYPTIMPORT = "wyptimport";
    //话务网管入库标识
    public static final String HWWGIMPORT = "hwwgimport";


}
