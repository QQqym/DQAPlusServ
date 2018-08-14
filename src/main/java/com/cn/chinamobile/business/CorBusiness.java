package com.cn.chinamobile.business;

import com.cn.chinamobile.compare.BXCounterCompare;
import com.cn.chinamobile.compare.BXWyptKpiCompare;
import com.cn.chinamobile.compare.BXWyptPMCompare;
import com.cn.chinamobile.compare.BxHwwgKpiCompare;
import com.cn.chinamobile.dao.ImportBXData;
import com.cn.chinamobile.entity.CompareEntity;
import com.cn.chinamobile.parse.ParseXml;
import com.cn.chinamobile.pojo.mybatis.FileCompleteWithBLOBs;
import com.cn.chinamobile.pojo.mybatis.MngdnSwversion;
import com.cn.chinamobile.pojo.mybatis.ScheduleTask;
import com.cn.chinamobile.pojo.mybatis.TaskDetStatus;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.service.*;
import com.cn.chinamobile.util.*;
import org.mozilla.intl.chardet.nsDetector;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by xueweixia on 2017/5/22.
 * 核心业务类
 */
@Service
public class CorBusiness {
    //omc文件
    private List<File> counterFiles = new ArrayList<>();
    //北向文件
    private List<File> bxFiles = new ArrayList<>(50);
    //网优平台KPI文件
    private List<File> wyptKpiFiles = new ArrayList<>();
    //网优平台PM文件
    private List<File> wyptPmFiles = new ArrayList<>();
    //omc check文件
    private List<File> countercheckFiles = new ArrayList<>();
    //北向check文件
    private List<File> bxcheckFiles = new ArrayList<>();
    //网优平台check文件
    private List<File> wyptcheckFiles = new ArrayList<>();
    //话务网管KPI文件
    private List<File> hwwgKPIFiles = new ArrayList<>();
    //话务网管check文件
    private List<File> hwwgCheckFiles = new ArrayList<>();

    FileUtil fileUtil = new FileUtil();

    private CompareEntity bxEntity = null;
    private CompareEntity omcEntity = null;

    @Resource
    private ScheduleTaskService scheduleTaskService;

    @Resource
    private FileCompleteService fileCompleteService;

    @Resource
    private TaskDetStatusService taskDetStatusService;

    @Resource
    private CityInfoService cityInfoService;

    @Resource
    private ProvinceService provinceService;

    @Resource
    private VendorService vendorService;

    @Resource
    private MngdnSwversionService mngdnSwversionService;

    private TaskDetStatus taskDetStatus;

    //北向文件是否存在
    private boolean bxflag;

    //omc文件是否存在
    private boolean omcflag;

    //话务网优文件是否存在
    private boolean wyhwflag;

    /**
     * 业务流程控制
     *
     * @param bxPath        北向存放文件的路径
     * @param omcPath       OMC存放文件的路径
     * @param wyptPath      网优平台存放文件的路径
     * @param hwwgPath      话务网管存放文件的路径
     * @param neType        网元类型
     * @param dataType      数据类型
     * @param parsetime     解析的时间
     * @param tmpFilePrefix 临时文件的路径
     * @param compare       是否比较
     */
    public void startBusiness(String bxPath, String omcPath, String wyptPath, String hwwgPath, ScheduleTask scheduleTask, String neType, String dataType, String parsetime, String vendorname, String version, String tmpFilePrefix, String compare) {
        //scheduleTask网元判断一下
        //初始化公共的信息
        taskDetStatus = new TaskDetStatus(scheduleTask);
        bxflag = checkBXFiles(scheduleTask.getProvince(), bxPath, neType, dataType, parsetime, vendorname, version);

        if (compare.equalsIgnoreCase("yes")) {
            omcflag = checkOMCFiles(scheduleTask.getProvince(), omcPath);
            wyhwflag = checkWYHWFiles(scheduleTask.getProvince(), neType, wyptPath, hwwgPath);
        }


        // 三方数据源存在一个即可解析
        if (bxflag || omcflag || wyhwflag) {
            //建立文件夹记录数据信息"D:\\adapterdata\\temp_out\\"
            String path=ContentInfo.FILE_TEMP_PATH+"\\"+scheduleTask.getProvince()+"_RECORD\\"+scheduleTask.getVendor()+"\\";
            ExcelUtil.creatDirectory(path);
            //counter、PM、网优平台的文件全，执行解析、对比、汇总、回填、入库
            try {
                //查询省份、地市、厂家信息
                Map<String, String> provinceMap = provinceService.selectAll();
                Map<String, String> cityMap = cityInfoService.getCityInfo();
                Map<String, String> vendorMap = vendorService.selectAll();

                //非ENB网元，按照原流程解析、计算、汇总
                if (!neType.equalsIgnoreCase("ENB")) {
                    parseSumCal(scheduleTask.getIntid(), neType, dataType, parsetime, vendorname, version, tmpFilePrefix, null, null, scheduleTask.getProvince(),scheduleTask.getVendor());
                } else {
                    MngdnSwversion mngdnSwversion = new MngdnSwversion(scheduleTask.getProvince(), scheduleTask.getVendor(), scheduleTask.getNetype(), scheduleTask.getVersion(), "", "");
                    //dnSwversionMap 包含所有的Dn，Swversion
                    Map<String, String> dnSwversionMap = mngdnSwversionService.selecAlltByParas(mngdnSwversion);
                    //定义check文件的map
                    Map<String, Long> checkFileMap = new HashMap<>();
                    DataUtil dataUtil = new DataUtil();
                    //读取bxcheck文件
                    for (File checkFile : bxcheckFiles) {
                        String encode = new FileCharsetDetector().guessFileEncoding(checkFile, new nsDetector());
                        checkFileMap.putAll(dataUtil.readCheckFile(checkFile.getAbsolutePath(), encode));
                    }
                    File op_file = null;
                    ParseXml parseXml = new ParseXml();
                    try {
                        op_file = parseXml.parse(bxFiles, scheduleTask.getIntid(), neType, dataType, parsetime, tmpFilePrefix, checkFileMap, provinceMap, cityMap, dnSwversionMap, scheduleTask.getProvince(),scheduleTask.getVendor());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Map<String, String> userSwMap = parseXml.getUserSwMap();
                    parseSumCal(scheduleTask.getIntid(), neType, dataType, parsetime, vendorname, version, tmpFilePrefix, op_file, userSwMap, scheduleTask.getProvince(),scheduleTask.getVendor());

                }


                /**
                 * 数据解析完成，开始对比
                 */
                String provinceCHName = provinceMap.get(scheduleTask.getProvince());
                String vendorCHName = vendorMap.get(vendorname);

                //使用中文的省份和厂家填写log信息
                if (compare.equalsIgnoreCase("yes")) {
                    compare(neType, dataType, vendorCHName, version, scheduleTask.getIntid(), parsetime, provinceCHName);
                }


                //回填数据
                backfilData(scheduleTask.getIntid(), scheduleTask.getProvince(), neType, dataType, vendorname, version);

                importpmKPidata(neType, dataType, parsetime, scheduleTask.getIntid(), vendorname, version);

                //执行成功，任务状态设置为2
                scheduleTask.setTaskstatus("2");

            } catch (Exception e) {
                Log.error("执行任务失败：" + scheduleTask.getIntid(), e);
                scheduleTask.setTaskstatus("0");
            }
        } else {
            scheduleTask.setTaskstatus("0");
        }
        //执行次数加1
        scheduleTask.setExecutetime(scheduleTask.getExecutetime() + 1);
        //更新任务状态
        scheduleTaskService.updateByPrimaryKey(scheduleTask);
    }

    /**
     * pm计算汇总
     *
     * @param taskid        任务号
     * @param neType        网元类型
     * @param dataType      数据类型
     * @param parsetime     解析时间
     * @param vendorname    厂家名称
     * @param version       版本号
     * @param tmpFilePrefix 临时路径前缀
     * @param op_file       北向解析到的资源文件
     * @param userSwMap     小区名软件版本的映射
     * @param province      省份简称
     */
    public void parseSumCal(int taskid, String neType, String dataType, String parsetime, String vendorname, String version, String tmpFilePrefix, File op_file, Map<String, String> userSwMap, String province,String vendor) {

        //定义线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(3);


        /**
         * 北向文件运行的线程
         */
        BXBusiness bxBusinessThread = IniDomain.ct.getBean(BXBusiness.class);
        bxBusinessThread.setParas(bxFiles, bxcheckFiles, taskid, neType, dataType, parsetime, vendorname, version, tmpFilePrefix, op_file, province,vendor);
        Future<CompareEntity> bxfuture = threadPool.submit(bxBusinessThread);

        /**
         * omc文件运行的线程
         */

        OMCBusiness omcBusinessThread = IniDomain.ct.getBean(OMCBusiness.class);
        omcBusinessThread.setParas(counterFiles, countercheckFiles, neType, dataType, parsetime, vendorname, version, tmpFilePrefix, taskid, userSwMap,province,vendor);
        Future<CompareEntity> omcfuture = threadPool.submit(omcBusinessThread);
        //北向满足文件要求才做解析
//        if(bxflag){
        try {
            bxEntity = bxfuture.get();
        } catch (Exception e) {
            Log.error(taskid + "北向数据解析计算失败", e);
            insertUpdateStatus(ContentInfo.BX_PARSE, "0", "解析计算失败");
        }
        insertUpdateStatus(ContentInfo.BX_PARSE, "1", "");
//        }

        //omc文件满足要求才做解析
//        if(omcflag){
        try {
            omcEntity = omcfuture.get();
        } catch (Exception e) {
            Log.error(taskid + "omc数据解析计算失败", e);
            insertUpdateStatus(ContentInfo.OMC_PARSE, "0", "解析计算失败");
        }
        insertUpdateStatus(ContentInfo.OMC_PARSE, "1", "");
//        }

        //关闭线程池
        threadPool.shutdown();
    }

    public void compare(String neType, String dataType, String vendorname, String version, int taskid, String parsetime, String provinceCHName) {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        Future<Boolean> bxCounterCom = null;
        if (bxEntity != null && omcEntity != null) {
            BXCounterCompare bxCounterCompare = new BXCounterCompare(vendorname, neType, version, dataType, bxEntity.getPmfile(), omcEntity.getPmfile(), taskid, parsetime, provinceCHName);
            bxCounterCom = threadPool.submit(bxCounterCompare);
        }


        Future<Boolean> bxwyptpm = null;
        Future<Boolean> bxwyptkpi = null;

        Future<Boolean> bxhwwgkpi = null;

        try {
            if (neType.equalsIgnoreCase("ENB") && wyhwflag) {
                BXWyptPMCompare bxWyptPMCompare = new BXWyptPMCompare(vendorname, neType, version, dataType, bxEntity.getPmfile(), wyptPmFiles.get(0), taskid, provinceCHName);
                bxwyptpm = threadPool.submit(bxWyptPMCompare);

                BXWyptKpiCompare bxWyptKpiCompare = new BXWyptKpiCompare(vendorname, neType, version, dataType, bxEntity.getKpiFile(), wyptKpiFiles.get(0), taskid, provinceCHName);
                bxwyptkpi = threadPool.submit(bxWyptKpiCompare);
            } else {
                //若文件存在
                if (wyhwflag) {
                    BxHwwgKpiCompare bxHwwgKpiCompare = new BxHwwgKpiCompare(vendorname, neType, version, dataType, bxEntity.getKpiFile(), hwwgKPIFiles.get(0), taskid, provinceCHName);
                    bxhwwgkpi = threadPool.submit(bxHwwgKpiCompare);
                }
            }

            Boolean bxCounter = false;
            if (bxEntity != null && omcEntity != null) {
                bxCounter = bxCounterCom.get();
            }
            if (bxCounter) {
                insertUpdateStatus(ContentInfo.LOG_NBI_COUNTER, "1", "");
            } else {
                insertUpdateStatus(ContentInfo.LOG_NBI_COUNTER, "0", "北向与omc数据对比失败");
            }

            if (neType.equalsIgnoreCase("ENB")) {
                //若文件不存在
                if (bxwyptpm == null && bxwyptkpi == null) {
                    insertUpdateStatus(ContentInfo.LOG_NBI_WYPT, "0", "网优平台文件不存在");
                } else {
                    boolean bxwpm = bxwyptpm.get();
                    boolean bxwkpi = bxwyptkpi.get();
                    if (bxwpm && bxwkpi) {
                        insertUpdateStatus(ContentInfo.LOG_NBI_WYPT, "1", "");
                    } else {
                        insertUpdateStatus(ContentInfo.LOG_NBI_WYPT, "0", "北向与网优平台数据对比失败");
                    }
                }

            } else {
                if (bxhwwgkpi == null) {
                    insertUpdateStatus(ContentInfo.LOG_NBI_HWWG, "0", "话务网管文件不存在");
                } else {
                    boolean bxh = bxhwwgkpi.get();
                    if (bxh) {
                        insertUpdateStatus(ContentInfo.LOG_NBI_HWWG, "1", "");
                    } else {
                        insertUpdateStatus(ContentInfo.LOG_NBI_HWWG, "0", "北向与话务网管数据对比失败");
                    }
                }

            }
        } catch (Exception e) {
            Log.error("任务：" + taskid + "对比失败", e);
        }

        threadPool.shutdown();
    }


    /**
     * 回填省份地市数据
     *
     * @param taskid     任务ID
     * @param province   省份
     * @param neType     网元类型
     * @param dataType   子网元类型
     * @param vendorname 厂家名称
     * @param version    版本
     */
    private void backfilData(int taskid, String province, String neType, String dataType, String vendorname, String version) {
        BackFilColumn backFilColumn = new BackFilColumn(province, vendorname, neType, version, dataType);
        backFilColumn.initProvinceVendor();

        try {
            if (bxEntity == null && omcEntity == null)
                return;
            String bxpm = bxEntity == null ? "" : bxEntity.getPmfile().getAbsolutePath();
            String omcpm = omcEntity == null ? "" : omcEntity.getPmfile().getAbsolutePath();

            String bxkpi = "";
            //先判空，否则只取北向的会报错，只取北向的不计算KPI
            if (bxEntity != null && bxEntity.getKpiFile() != null)
                bxkpi = bxEntity.getKpiFile().getAbsolutePath();
            String omckpi = omcEntity == null ? "" : omcEntity.getKpiFile().getAbsolutePath();
            backFilColumn.beginCompare(omcpm, bxpm);

            //文件都不存在不回填
            if (!(omckpi.equalsIgnoreCase("") && bxkpi.equalsIgnoreCase("")))
                backFilColumn.beginCompare(omckpi, bxkpi);
        } catch (Exception e) {
            Log.error(taskid + "backfil data error", e);
        }
    }

    /**
     * PM、KPI数据入库
     *
     * @param neType     网元类型
     * @param dataType   数据类型
     * @param parsetime  解析时间
     * @param taskid     任务id
     * @param vendorname 厂家名称
     * @param version    版本号
     */
    private void importpmKPidata(String neType, String dataType, String parsetime, int taskid, String vendorname, String version) {
        ImportBXData importBXData = IniDomain.ct.getBean(ImportBXData.class);

        //入库 北向pm
        boolean bxpmimport = false;
        boolean bxkpiimport = false;
        if (bxEntity != null) {
            String bxpmName = bxEntity.getPmfile().getAbsolutePath() + "_dest";
            String bxpmtable = "NBIPM";
            bxpmimport = importBXData.importData(bxpmName, bxpmtable, parsetime, taskid, "\\|", ContentInfo.ENCODING, neType, dataType);

            //计算KPI的时候，入库北向kpi
            if (bxEntity.getKpiFile() != null) {
                String bxkpiName = bxEntity.getKpiFile().getAbsolutePath() + "_dest";
                String bxkpitable = "NBIKPI";
                bxkpiimport = importBXData.importData(bxkpiName, bxkpitable, parsetime, taskid, "\\|", ContentInfo.ENCODING, neType, dataType);
            }

        }
        if (bxpmimport && bxkpiimport) {
            insertUpdateStatus(ContentInfo.BXIMPORT, "1", "");
        } else {
            insertUpdateStatus(ContentInfo.BXIMPORT, "0", "入库失败");
        }

        //入库 omc pm
        boolean omcpmimport = false;
        boolean omckpiimport = false;
        if (omcEntity != null) {
            String omcpmName = omcEntity.getPmfile().getAbsolutePath() + "_dest";
            String omcpmtable = "CounterPM";
            omcpmimport = importBXData.importData(omcpmName, omcpmtable, parsetime, taskid, "\\|", ContentInfo.ENCODING, neType, dataType);

            //入库 omc kpi
            String omckpiName = omcEntity.getKpiFile().getAbsolutePath() + "_dest";
            String omckpitable = "CounterKPI";
            omckpiimport = importBXData.importData(omckpiName, omckpitable, parsetime, taskid, "\\|", ContentInfo.ENCODING, neType, dataType);

        }

        if (omcpmimport && omckpiimport) {
            insertUpdateStatus(ContentInfo.OMCIMPORT, "1", "");
        } else {
            insertUpdateStatus(ContentInfo.OMCIMPORT, "0", "入库失败");
        }

        DataUtil dataUtil = new DataUtil();
        //定义网优平台check文件的map
        Map<String, Long> wyptcheckFileMap = new HashMap<>();
        //读取网优平台check文件
        for (File checkFile : wyptcheckFiles) {
            String encode = new FileCharsetDetector().guessFileEncoding(checkFile, new nsDetector());
            wyptcheckFileMap.putAll(dataUtil.readCheckFile(checkFile.getAbsolutePath(), encode));
        }

        //定义话务网管check文件的map
        Map<String, Long> hwwgcheckFileMap = new HashMap<>();
        //读取网优平台check文件
        for (File checkFile : hwwgCheckFiles) {
            String encode = new FileCharsetDetector().guessFileEncoding(checkFile, new nsDetector());
            hwwgcheckFileMap.putAll(dataUtil.readCheckFile(checkFile.getAbsolutePath(), encode));
        }

        //入库网优平台 pm
        if (wyptPmFiles.size() > 0) {
            String encode = new FileCharsetDetector().guessFileEncoding(wyptPmFiles.get(0), new nsDetector());
            //检测文件完整性
            FileCompleteWithBLOBs fileComplete = new FileCompleteWithBLOBs().checkComPlete(wyptPmFiles.get(0), encode, "wyptfile_", dataType, vendorname, version, taskid);
            fileComplete.setFactlength(wyptPmFiles.get(0).length());
            fileComplete.setChecklength(wyptcheckFileMap.get(wyptPmFiles.get(0).getName()));

            String wyptpmName = wyptPmFiles.get(0).getAbsolutePath();
            String wyptpmtable = "WYPTPM";
            boolean parseflag = importBXData.importData(wyptpmName, wyptpmtable, parsetime, taskid, ",", encode, neType, dataType);
            if (parseflag) {
                fileComplete.setParsestatus(1);
                insertUpdateStatus(ContentInfo.WYPT_PARSE, "1", "");
                insertUpdateStatus(ContentInfo.WYPTIMPORT, "1", "");
            } else {
                fileComplete.setParsestatus(0);
                insertUpdateStatus(ContentInfo.WYPT_PARSE, "0", "解析失败");
                insertUpdateStatus(ContentInfo.WYPTIMPORT, "0", "入库失败");
            }
            fileCompleteService.insert(fileComplete);
        }

        if (wyptKpiFiles.size() > 0) {
            //入库网优平台kpi
            String kpiencode = new FileCharsetDetector().guessFileEncoding(wyptKpiFiles.get(0), new nsDetector());
            //检测文件完整性
            FileCompleteWithBLOBs fileComplete = new FileCompleteWithBLOBs().checkComPlete(wyptKpiFiles.get(0), kpiencode, "wyptfile_", dataType, vendorname, version, taskid);
            fileComplete.setFactlength(wyptKpiFiles.get(0).length());
            fileComplete.setChecklength(wyptcheckFileMap.get(wyptKpiFiles.get(0).getName()));
            //入库
            String wyptkpiName = wyptKpiFiles.get(0).getAbsolutePath();
            String wyptkpitable = "WYPTKPI";
            boolean parseflag = importBXData.importData(wyptkpiName, wyptkpitable, parsetime, taskid, ",", kpiencode, neType, dataType);
            if (parseflag) {
                insertUpdateStatus(ContentInfo.WYPT_PARSE, "1", "");
                insertUpdateStatus(ContentInfo.WYPTIMPORT, "1", "");
                fileComplete.setParsestatus(1);
            } else {
                fileComplete.setParsestatus(0);
                insertUpdateStatus(ContentInfo.WYPT_PARSE, "0", "解析失败");
                insertUpdateStatus(ContentInfo.WYPTIMPORT, "0", "入库失败");
            }
            fileCompleteService.insert(fileComplete);
        }

        if (hwwgKPIFiles.size() > 0) {
            //入库网优平台kpi
            String kpiencode = new FileCharsetDetector().guessFileEncoding(hwwgKPIFiles.get(0), new nsDetector());

            //检测文件完整性
            FileCompleteWithBLOBs fileComplete = new FileCompleteWithBLOBs().checkComPlete(hwwgKPIFiles.get(0), kpiencode, "hwwgfile_", dataType, vendorname, version, taskid);
            fileComplete.setFactlength(hwwgKPIFiles.get(0).length());
            fileComplete.setChecklength(hwwgcheckFileMap.get(hwwgKPIFiles.get(0).getName()));

            String hwwgkpiName = hwwgKPIFiles.get(0).getAbsolutePath();
            String hwwgkpitable = "HWWGKPI";
            boolean parseflag = importBXData.importData(hwwgkpiName, hwwgkpitable, parsetime, taskid, ",", kpiencode, neType, dataType);

            if (parseflag) {
                fileComplete.setParsestatus(1);
                insertUpdateStatus(ContentInfo.HWWG_PARSE, "1", "");
                insertUpdateStatus(ContentInfo.HWWGIMPORT, "1", "");
            } else {
                fileComplete.setParsestatus(0);
                insertUpdateStatus(ContentInfo.HWWG_PARSE, "0", "解析失败");
                insertUpdateStatus(ContentInfo.HWWGIMPORT, "0", "入库失败");
            }
            fileCompleteService.insert(fileComplete);
        }

        //关闭数据库连接
        importBXData.closeConnection();

    }

    /**
     * 检查各类文件是否存在
     *
     * @param province   省份
     * @param bxPath     北向的路径
     * @param neType     网元类型
     * @param dataType   数据类型
     * @param parsetime  解析时间
     * @param vendorname 厂家名称
     * @param version    文件版本号
     * @return 文件是否齐全
     */
    private boolean checkBXFiles(String province, String bxPath, String neType, String dataType, String parsetime, String vendorname, String version) {
        //判断北向的文件是否存在

        //如果是天津，时间没有-分割
//        if(province.equalsIgnoreCase("TJ")){
//            bxPath = bxPath.replace("-","");
//        }

        if (neType.equalsIgnoreCase("ENB")) {
            fileUtil.findGivenFiles(bxPath, dataType.toLowerCase(), bxFiles);
        } else {
            String time = parsetime.replaceFirst("-", "").replaceFirst("-", "");
            fileUtil.findGivenTimeFiles(bxPath, dataType.toLowerCase(), time, bxFiles);
        }

        //核心网数据，且按子网元查不到，按照时间查询所有文件
        if (!neType.equalsIgnoreCase("ENB") && bxFiles.size() == 0) {
            String time = parsetime.replaceFirst("-", "").replaceFirst("-", "");
            fileUtil.findGivenTimeFiles(bxPath, "", time, bxFiles);
        }

        //避免写过多的类，将不符合版本的数据remove掉
        Iterator<File> it = bxFiles.iterator();
        while (it.hasNext()) {
            File bxfile = it.next();
            //如果文件名不包含版本号，删除
            if (!bxfile.getName().contains(version)) {
                it.remove();
            }
        }


        if (bxFiles.size() == 0) {
            Log.info(bxPath + "路径下无文件");
            insertUpdateStatus(ContentInfo.BX_PARSE, "0", bxPath + "路径下无文件");
            return false;
        }

        if (!judgecontinue(neType)) {
            Log.info(bxPath + "路径下文件时间粒度不足");
            insertUpdateStatus(ContentInfo.BX_PARSE, "0", bxPath + "路径下文件时间粒度不足");
            return false;
        }

        //查找北向check文件
        fileUtil.findGivenFiles(bxPath, "check", bxcheckFiles);

        return true;
    }

    /**
     * 检测omc文件是否存在
     *
     * @param province 省份
     * @param omcPath  omc文件路径
     * @return 文件是否存在
     */
    public boolean checkOMCFiles(String province, String omcPath) {
        //如果是天津，时间没有-分割
//        if(province.equalsIgnoreCase("TJ")){
//            omcPath = omcPath.replace("-","");
//        }
        //判断counter的文件是否存在
        fileUtil.findAllFiles(omcPath, counterFiles);
        if (counterFiles.size() == 0) {
            Log.info(omcPath + "路径下无文件");
            insertUpdateStatus(ContentInfo.OMC_PARSE, "0", omcPath + "路径下无文件");
            return false;
        }

        //查找omc check文件
        fileUtil.findGivenFiles(omcPath, "check", countercheckFiles);

        return true;
    }

    /**
     * 检测网优平台或话务网管文件是否存在
     *
     * @param province 省份
     * @param neType   网元类型
     * @param wyptPath 网优平台路径
     * @param hwwgPath 话务网管路径
     * @return 是否存在文件
     */
    public boolean checkWYHWFiles(String province, String neType, String wyptPath, String hwwgPath) {
        //如果是天津，时间没有-分割
//        if(province.equalsIgnoreCase("TJ")){
//            wyptPath = wyptPath.replace("-","");
//            hwwgPath = hwwgPath.replace("-","");
//        }
        //如果是ENB数据，判断网优平台数据是否存在
        if (neType.equalsIgnoreCase("ENB")) {
            //判断网优平台pm的文件是否存在
            fileUtil.findGivenFiles(wyptPath, "pm", wyptPmFiles);
            if (wyptPmFiles.size() == 0) {
                Log.info(wyptPath + "路径下无PM文件");
                insertUpdateStatus(ContentInfo.WYPT_PARSE, "0", wyptPath + "路径下无PM文件");
                return false;
            }

            //判断网优平台kpi的文件是否存在
            fileUtil.findGivenFiles(wyptPath, "kpi", wyptKpiFiles);
            if (wyptKpiFiles.size() == 0) {
                Log.info(wyptPath + "路径下无KPI文件");
                insertUpdateStatus(ContentInfo.WYPT_PARSE, "0", wyptPath + "路径下无KPI文件");
                return false;
            }

            //查找网优平台 check文件
            fileUtil.findGivenFiles(wyptPath, "check", wyptcheckFiles);
        } else {
            //判断话务网管kpi的文件是否存在
            fileUtil.findGivenFiles(hwwgPath, "kpi", hwwgKPIFiles);
            if (hwwgKPIFiles.size() == 0) {
                Log.info(hwwgPath + "路径下无KPI文件");
                insertUpdateStatus(ContentInfo.HWWG_PARSE, "0", hwwgPath + "路径下无KPI文件");
                return false;
            }

            //查找网优平台 check文件
            fileUtil.findGivenFiles(hwwgPath, "check", hwwgCheckFiles);
        }
        return true;
    }

    private void insertUpdateStatus(String step, String status, String comment) {
        TaskDetStatus ntask = taskDetStatus.clone();
        ntask.setSubtask(step);
        ntask.setTaskstatus(status);
        ntask.setCommtent(comment);
        //不存在insert
        if (taskDetStatusService.selectByParas(ntask) == null) {
            taskDetStatusService.insert(ntask);
        } else {//存在更新
            taskDetStatusService.updateByTaskSubId(ntask);
        }

    }

    private boolean judgecontinue(String netype) {
        boolean isContinue = true;
        Set<String> timeset = new HashSet<>();
        //将文件名的时间放到一个set，时间格式yyyymmdd-hhmi
        for (int i = 0; i < bxFiles.size(); i++) {
            String name = bxFiles.get(i).getName();
            String[] names = name.split("-");
            int length = names.length;
            String dateString = "";
            String date = names[length - 2];
            String time = names[length - 1];
            if (date.length() < 8) {
                date = names[length - 3];
                time = names[length - 2];
            }
            if (date.length() >= 12)
                dateString = date.substring(0, 8) + "-" + date.substring(8, 12);
            else
                dateString = date + "-" + time.substring(0, 4);
            timeset.add(dateString);
        }

        //ENB是五个时间点的文件
        if (netype.equalsIgnoreCase("ENB")) {
            if (timeset.size() != 5) {
                isContinue = false;
            }
        } else { //其他粒度是4个时间点的文件
            if (timeset.size() != 4) {
                isContinue = false;
            }
        }

        return isContinue;
    }
}
