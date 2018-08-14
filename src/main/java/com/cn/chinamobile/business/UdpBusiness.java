package com.cn.chinamobile.business;

import com.cn.chinamobile.entity.UdpCount;
import com.cn.chinamobile.parse.UdpNrmParse;
import com.cn.chinamobile.parse.UdpPmParse;
import com.cn.chinamobile.pojo.mybatis.*;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.service.*;
import com.cn.chinamobile.util.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * @author zhou on 2017/10/29.
 */
@Service
public class UdpBusiness {

    @Resource
    private UdpTaskService udpTaskService;

    @Resource
    private UdpNrmService udpNrmService;

    @Resource
    private LogUdpService logUdpService;

    @Resource
    private LogPassDetailService logPassDetailService;

    @Resource
    private MngdnSwversionService mngdnSwversionService;

    @Resource
    private UdpGradeTotalService udpGradeTotalService;

    @Resource
    private UdpPmService udpPmService;

    @Resource
    private CityInfoService cityInfoService;

    @Resource
    private UdpLoseDatatypeService udpLoseDatatypeService;

    @Resource
    private ProvinceService provinceService;

    @Resource
    private VendorService vendorService;

    @Resource
    private UdpExeSetService udpExeSetService;

    @Resource
    private UdpLosednService udpLosednService;

    @Resource
    private UdpExempDtypeService udpExempDtypeService;

    @Resource
    private UdpExempEnbService udpExempEnbService;

    //存储省份信息
    private Map<String,String> provinceMap;

    //存储地市信息
    private Map<String,String> cityMap;

    //存储厂家信息
    private Map<String,String> vendorMap;

    private UdpTask udpTask = null;
    private List<String> udpfilePaths = new ArrayList();

    private List<File> udpFiles = new ArrayList<>();
    //ManagedElement网元文件
    private List<File> mngdFiles = new ArrayList<>();
    private Map<String,Map<String,UdpNrm>> nrmMap;

    private Map<String,Map<String,UdpPm>> pmMap;

    //豁免项，key是子网元类型，value是豁免指标集
    private Map<String,Set<String>> udpExceMap;

    //存储ENB豁免指标集
    private Map<String,Map<String,String>> enbExcepMap;

    FileUtil fileUtil = new FileUtil();
    UdpLogUtil udpLogUtil = new UdpLogUtil();

    public void setParas(List<String> udpFilePaths,UdpTask udpTask){
        this.udpfilePaths = udpFilePaths;
        this.udpTask = udpTask;
    }

    public void startBusiness(){
        //存在文件
        UdpTask udptskin = udpTaskService.selectByParas(udpTask);
        //不存在，生产任务插入数据库
        if(udptskin==null){
            udpTaskService.insert(udpTask);

            //插入后将该实例clone给udptask
            udptskin = udpTask.clone();
        }//任务状态1，表示解析已成功，不再执行
        else if( udptskin.getTaskstatus()!=null && udptskin.getTaskstatus().equals("1")){
            Log.info("任务已解析，任务号："+udptskin.getIntid());
            return;
        }

        //查询udp文件
        boolean isexist = checkUdpFiles(udpTask.getType());
        if(isexist){
            //如果是ENB网元，查询ENB豁免指标集
            if(udptskin.getNetype().equalsIgnoreCase("ENB")){
                enbExcepMap = udpExempEnbService.getEnbExcepMap(udptskin.getType(),udptskin.getVendor(),udptskin.getVersion());
            }

            String netype = udptskin.getNetype();
            //规范查询中特殊处理这四类网元
            if(netype.equalsIgnoreCase("SCSCF")){
                netype = "CSCF";
            }else if(netype.equalsIgnoreCase("VOLTESBC")){
                netype = "PSBC";
            }else if(netype.equalsIgnoreCase("HDRA") || netype.equalsIgnoreCase("LDRA")){
                netype = "DRA";
            }else if(netype.equalsIgnoreCase("MSC")){
                netype = "MSS";
            }

            //查询省份、地市、厂家信息
            provinceMap = provinceService.selectAll();
            vendorMap = vendorService.selectAll();
            cityMap = cityInfoService.getCityInfo();

            //查询0值豁免项,0值豁免只有SCSCF特殊处理
            if(udptskin.getNetype().equalsIgnoreCase("SCSCF")){
                udpExceMap = udpExeSetService.selectByNetype(netype);
            }else {
                udpExceMap = udpExeSetService.selectByNetype(udptskin.getNetype());
            }

            String udplogfile = ContentInfo.FILE_TEMP_PATH+File.separator+udptskin.getProvince()+ File.separator+"UDP"+File.separator+udptskin.getNetype()
                    +File.separator + udptskin.getVendor()+File.separator + udptskin.getType()+File.separator
                    +udptskin.getDatetime()+File.separator+"udp_log";

            udpLogUtil.initBuffer(udplogfile);
            //处理NRM流程

            if(udptskin.getType().equalsIgnoreCase("NRM")){
                dealNRMProcess(netype,udptskin);
            }else {
                dealPMProcess(netype,udptskin);
            }
            //关闭日志流
            udpLogUtil.closeFile();


        }//不存在，任务设置失败，入库
        else{
            udptskin.setTaskstatus("0");
            StringBuffer failreason = new StringBuffer();
            for(String udpfilePath : udpfilePaths){
                failreason.append(udpfilePath).append(";");
            }
            failreason = failreason.deleteCharAt(failreason.length()-1);
            udptskin.setFailurereason(failreason+" 路径下没有检查到需要的文件");
            udpTaskService.updateByPrimaryKey(udptskin);
        }
    }

    private void dealNRMProcess(String netype,UdpTask udptskin){
        try {
            Map<String,String> levelMap = udpNrmService.selectLevelInfo(netype,udptskin.getVersion());
            nrmMap  = udpNrmService.selectByParas(netype,udptskin.getVersion());
            //记录网元版本不存在，挑选最低版本使用
            if(nrmMap.size()==0){
                udpLogUtil.loseNetypeVersion("NRM",netype,udptskin.getVersion());
                Log.info("任务："+udptskin.getIntid()+"找不到软件版本");
                String version = udpNrmService.getHigherVersion(netype,udptskin.getVersion());

                //没有规范
                if(null==version){
                    Log.info("任务："+udptskin.getIntid()+"未找到版本高的规范");
                    version = udpNrmService.getVersion(netype);
                    udpLogUtil.write("使用软件版本："+version);
                    if(null==version){
                        udpLogUtil.closeFile();
                        udptskin.setTaskstatus("0");
                        udptskin.setFailurereason("未找到任何版本的规范");
                        udpTaskService.updateByPrimaryKey(udptskin);
                        return ;
                    }
                }else {
                    udpLogUtil.write("使用软件版本："+version);
                }
                //使用查询到的软件版本号
                nrmMap  = udpNrmService.selectByParas(netype,version);

                //重新加载网元树
                levelMap = udpNrmService.selectLevelInfo(netype,version);
            }
            UdpNrmParse udpNrmParse = IniDomain.ct.getBean(UdpNrmParse.class);
            udpNrmParse.initParas(levelMap,udpLogUtil,cityMap,provinceMap,vendorMap);
            String flag = udpNrmParse.parse(udptskin.getProvince(),udptskin.getNetype(),udpFiles,mngdFiles,nrmMap);

            if(flag.equalsIgnoreCase("sucess")){
                //存储记录到数据库的异常指标，为后续记录规范支持性提供数据
                Map<String,Set<String>> lognrms = new HashMap<>();

                Map<String,Map<String,Map<String,Map<String,UdpCount>>>> resultMap = udpNrmParse.getResultMap();
                //存储结果
                insertLogUdp(udptskin.getIntid(),udptskin.getType(),udptskin.getNetype(),resultMap,lognrms);

                //存储出现过的子网元指标集
                Map<String,Set<String>> objectMapSet = udpNrmParse.getObjectMapSet();
                Set<String> existSwVersion = udpNrmParse.getExistSwVersion();
                Map<String,Map<String,Map<String,Integer>>> excepnum = insertNRMLoseDataInfo(udptskin, objectMapSet, existSwVersion,lognrms);

                //入库各级的总数
                insertTotalNRMNum(udptskin.getIntid(),existSwVersion,objectMapSet,excepnum);

                //入库该资源解析获取的DN和软件版本信息
                Map<String,String> dnSwversion = udpNrmParse.getDnSwversionMap();
                mngdnSwversionService.insertbatchDnSwVersion(udptskin.getProvince(),udptskin.getVendor(),udptskin.getNetype(),udptskin.getVersion(),dnSwversion);

                //入库找不到软件版本的信息
                List<UdpLosedn> losednList = udpNrmParse.getLosednList();
//                insertLoseDn(udptskin.getIntid(),losednList);
                losednList.clear();

                //处理规范支持性Excel写出
                String excelfilename =ContentInfo.FILE_TEMP_PATH + File.separator+"UDPResult"+File.separator
                                        +udptskin.getProvince()+"-"+udptskin.getVendor()+"-"+udptskin.getDatetime()+"-";
                dealNorm(lognrms,excelfilename,udptskin.getType(),udptskin.getNetype(),udptskin.getVersion());

                udptskin.setTaskstatus("1");
                udptskin.setFailurereason("执行完成");
            }else {
                Log.info("执行任务失败："+udptskin.getIntid()+"失败原因："+flag);
                udptskin.setTaskstatus("0");
                udptskin.setFailurereason(flag);
            }
            udpTaskService.updateByPrimaryKey(udptskin);
        }catch (Exception e){
            Log.error("执行任务失败："+udptskin.getIntid(),e);
            udptskin.setTaskstatus("0");
            udptskin.setFailurereason("");
            udpTaskService.updateByPrimaryKey(udptskin);
        }
    }

    private void dealPMProcess(String netype,UdpTask udptskin){
        try{
            //处理PM流程
            //从已存的NRM中获取Dn,软件版本号
            MngdnSwversion mngdnSwversion = new MngdnSwversion(udptskin.getProvince(),udptskin.getVendor(),udptskin.getNetype(),udptskin.getVersion(),"","");
            Map<String,String> dnSwversionMap = mngdnSwversionService.selecAlltByParas(mngdnSwversion);
            //查询指标集
            pmMap = udpPmService.selectByParas(netype,udptskin.getVersion());

            //记录网元版本不存在，挑选最低版本使用
            if(pmMap.size()==0){
                udpLogUtil.loseNetypeVersion("PM",netype,udptskin.getVersion());
                String version = udpPmService.getHigherVersion(netype,udptskin.getVersion());
                Log.info("任务："+udptskin.getIntid()+"找不到软件版本");

                //没有规范
                if(null==version){
                    Log.info("任务："+udptskin.getIntid()+"未找到版本高的规范");
                    udpLogUtil.write("任务："+udptskin.getIntid()+"未找到版本高的规范");
                    version = udpPmService.getVersion(netype);
                    if(null == version){
                        udpLogUtil.closeFile();
                        udptskin.setTaskstatus("0");
                        udptskin.setFailurereason("未找到任何版本的规范");
                        udpTaskService.updateByPrimaryKey(udptskin);
                        return ;
                    }else {
                        udpLogUtil.write("使用软件版本："+version);
                    }
                }else {
                    udpLogUtil.write("使用软件版本："+version);
                }
                //使用查询到的软件版本号
                pmMap  = udpPmService.selectByParas(netype,version);
            }

            //解析记录
            UdpPmParse udpPmParse = IniDomain.ct.getBean(UdpPmParse.class);
            udpPmParse.initParas(udpLogUtil,cityMap,provinceMap,vendorMap);
            String flag= udpPmParse.parse(udptskin.getProvince(),udptskin.getNetype(),udpFiles,dnSwversionMap,pmMap,udpLogUtil);

            if(flag.equalsIgnoreCase("sucess")){
                //存储记录到数据库的异常指标，为后续记录规范支持性提供数据
                Map<String,Set<String>> logpms = new HashMap<>();

                Map<String,Map<String,Map<String,Map<String,UdpCount>>>> resultMap = udpPmParse.getResultMap();
                //将汇总结果入库
                insertLogUdp(udptskin.getIntid(),udptskin.getType(),udptskin.getNetype(),resultMap,logpms);

                //存储缺失的子网元指标集
                Map<String,Set<String>> objectMapSet = udpPmParse.getObjectMapSet();
                Set<String> existSwVersion = udpPmParse.getExistSwVersion();
                Map<String,Map<String,Map<String,Integer>>> excepnum = insertPMLoseDataInfo(udptskin,objectMapSet,existSwVersion,logpms);

                //入库各级的总数
                insertTotalPmNum(udptskin.getIntid(),existSwVersion,objectMapSet,excepnum);

                //入库找不到软件版本的信息
                List<UdpLosedn> losednList = udpPmParse.getLosednList();
//                insertLoseDn(udptskin.getIntid(),losednList);

                //处理规范支持性Excel写出
                String excelfilename =ContentInfo.FILE_TEMP_PATH + File.separator+"UDPResult"+File.separator
                        +udptskin.getProvince()+"-"+udptskin.getVendor()+"-"+udptskin.getDatetime()+"-";
                dealNorm(logpms,excelfilename,udptskin.getType(),udptskin.getNetype(),udptskin.getVersion());

                udptskin.setTaskstatus("1");
                udptskin.setFailurereason("执行完成");
            }else {
                Log.info("执行任务失败："+udptskin.getIntid()+"失败原因："+flag);
                udptskin.setTaskstatus("0");
                udptskin.setFailurereason(flag);
            }
            udpTaskService.updateByPrimaryKey(udptskin);
        }catch (Exception e){
            Log.error("执行任务失败："+udptskin.getIntid(),e);
            udptskin.setTaskstatus("0");
            udptskin.setFailurereason("");
            udpTaskService.updateByPrimaryKey(udptskin);
        }
    }

    /**
     * 处理规范支持性Excel
     * @param logpms 异常的指标
     * @param excelfilename 输出的Excel文件名
     * @param type 文件类型
     * @param netype 网元类型
     * @param version  版本
     */
    public void dealNorm(Map<String,Set<String>> logpms ,String excelfilename,String type ,String netype,String version){
        if(netype.equalsIgnoreCase("scscf"))
            netype="cscf";
        String normfile = IniDomain.udpfileconfig.get(type).get(netype+"-"+version);
        //如果没有找到对应的软件版本，不处理
        if(null == normfile)
            return;

        ExcelUtil excelUtil = new ExcelUtil();
        String nfile = ContentInfo.DQA_VERSION_BASED_PATH+File.separator+"udp"+File.separator+normfile;

        Map<String,String> datamap = null;
        if(type.equalsIgnoreCase("nrm")){
            datamap = excelUtil.readNrmIndex(nfile,"Index");
        }

        //获取规范级别的异常指标项，保留logpms的各个版本的交集
        Set<String> excepresult = getExcepPms(logpms);

        String swversions = "";
        for(String swversion : logpms.keySet()){
            swversions = swversions + swversion+",";
        }
        swversions = swversions.substring(0,swversions.length()-1);
        excelfilename = excelfilename + normfile;
        excelUtil.writeNormSupport(nfile,excelfilename,type,excepresult,datamap,swversions);
    }

    /**
     * 获取规范级别的异常指标
     * @param logpms
     * @return
     */
    private Set<String> getExcepPms(Map<String,Set<String>> logpms){

        Set<String> excepresult = new HashSet<>();
        int i=0;
        for(String swversion : logpms.keySet()){
            if(i==0){
                excepresult.addAll(logpms.get(swversion));
            }else {
                excepresult.retainAll(logpms.get(swversion));
            }
        }
        return excepresult;
    }

    /**
     * 将最终统计结果入库
     * @param taskid 任务id号
     * @param type 数据类型 PM/NRM
     * @param netype 网元类型
     * @param resultMap 最终入库结果
     * @param lognrms 记录存放的log 软件版本，子网元，指标名
     */
    public void insertLogUdp(int taskid,String type, String netype,Map<String,Map<String,Map<String,Map<String,UdpCount>>>> resultMap,Map<String,Set<String>> lognrms){
        //按照任务ID删除
        logPassDetailService.deletebytaskid(taskid);
        logUdpService.deletebytaskid(taskid);
        //按照子网元遍历
        int logcount = 1;
        int passcount = 0;
        List<LogUdp> logList = new ArrayList<>(1600);
        List<LogPassDetail> passList = new ArrayList<>(1600);
        for(String cityname : resultMap.keySet()){
            Map<String,Map<String,Map<String,UdpCount>>> citymap = resultMap.get(cityname);
            for(String datatype : citymap.keySet()){
                //获取该子网元的指标集
                Map<String,UdpNrm> udpNrmMap = null;
                Map<String,UdpPm> udpPmMap = null;
                if(type.equalsIgnoreCase("NRM")){
                    udpNrmMap= nrmMap.get(datatype);
                }else {
                    udpPmMap = pmMap.get(datatype);
                }
                Map<String,Map<String,UdpCount>> swpmMap = citymap.get(datatype);
                //按照软件版本遍历
                for(String swVersion : swpmMap.keySet()){
                    Map<String,UdpCount> pmMap = swpmMap.get(swVersion);
                    //按照指标遍历
                    for(String pmname : pmMap.keySet()){
                        UdpCount udpCount = pmMap.get(pmname);
                        int nexistnum = udpCount.getNexistnum();
                        int nullnum = udpCount.getNullnum();
                        int zeronum = udpCount.getZeronum();
                        int ntypenum = udpCount.getNtypenum();
                        int totalnum = udpCount.getTotalnum();
                        boolean errorflag = false;
                        String errortype="";
                        if(nexistnum==totalnum){
                            //不存在的个数=总个数，记录
                            errorflag = true;
                            errortype="不存在";
                        }else if(nullnum==totalnum){
                            //为空的个数=总个数，记录
                            errorflag = true;
                            errortype="空值";
                        }else if(zeronum == totalnum){
                            //为0的个数=总个数，记录
                            errorflag = true;
                            errortype="0值";
                        }else if(ntypenum == totalnum){
                            //不符合类型的个数=总个数，记录
                            errorflag = true;
                            errortype="类型";
                        } else if(nexistnum+nullnum+zeronum+ntypenum==totalnum){
                            //四个错误类型的和等于总数时
                            errorflag = true;
                            if(nexistnum>0){
                                errortype=errortype+"不存在,";
                            }
                            if(nullnum>0){
                                errortype=errortype+"空值,";
                            }
                            if(zeronum>0){
                                errortype=errortype+"0值,";
                            }
                            if(ntypenum>0){
                                errortype=errortype+"类型";
                            }

                        }
                        if(errortype.endsWith(","))
                            errortype = errortype.substring(0,errortype.length()-1);
                        //记录是否为0值豁免项
                        boolean zeroexem = false;
                        //如果错误，入库
                        try{
                            if(errorflag){
                                //如果是豁免的0值，跳过；如果错误类型是0值，且豁免项中包含该数据类型的该指标，豁免
                                if(errortype.contains("0值") && udpExceMap.get(datatype)!=null && udpExceMap.get(datatype).contains(pmname)){
                                    zeroexem = true;
                                }
                                //如果是ENB网元且被豁免，则指标豁免
                                if(netype.equalsIgnoreCase("ENB") && isExcepEnb(swVersion,datatype,pmname,errortype)){
                                    zeroexem = true;
                                }
                                String grade ="";
                                if(type.equalsIgnoreCase("NRM")){
                                    grade = udpNrmMap.get(pmname).getNrmGrade();
                                }else {
                                    grade = udpPmMap.get(pmname).getPmGrade();
                                }

                                LogUdp logUdp = new LogUdp(taskid,cityname,swVersion,datatype,pmname,grade,errortype);
                                //如果不被豁免存入数据库
                                if(!zeroexem){
                                    logList.add(logUdp);
                                    logcount++;

                                    //将异常数据记录，为后续写规范使用
                                    Set<String> dapmSet = new HashSet<>();
                                    if(lognrms.containsKey(swVersion)){
                                        dapmSet = lognrms.get(swVersion);
                                    }else {
                                        lognrms.put(swVersion,dapmSet);
                                    }
                                    dapmSet.add(datatype+"-"+pmname);
                                }

                                //批量入库一千条
                                if(logcount%1000==0){
                                   logUdpService.batchinsert(logList);
                                    //入库完清空list
                                    logList.clear();
                                }
                            }
                        }catch (Exception e){
                            Log.error("入库日志失败："+ pmname ,e);
                        }

                        //入库通过的详情
                        int passnum = udpCount.getPassNum();
                        LogPassDetail logPassDetail = new LogPassDetail(taskid,cityname,datatype,swVersion,pmname,passnum,totalnum);
                        passcount++;
                        passList.add(logPassDetail);
                        if(passcount%1000==0){
                            logPassDetailService.batchinsert(passList);
                            //入库完清空list
                            passList.clear();
                        }
                    }
                }
            }
        }

        //剩余数据入库
        if(logList.size()>0){
            logUdpService.batchinsert(logList);
            //入库完清空list
            logList.clear();
        }
        if(passList.size()>0){
            logPassDetailService.batchinsert(passList);
            //入库完清空list
            passList.clear();
        }


    }

    /**
     * 判断ENB指标是否被豁免
     * @param swVersion 软件版本
     * @param datatype 子网元类型
     * @param pmname 指标名称
     * @param errortype 错误类型
     * @return 是否被豁免
     */
    public boolean isExcepEnb(String swVersion,String datatype,String pmname,String errortype){
        boolean flag = false;

        if( enbExcepMap.get(swVersion)!=null
                && enbExcepMap.get(swVersion).get(datatype+"-"+pmname) !=null
                && enbExcepMap.get(swVersion).get(datatype+"-"+pmname).contains(errortype) ){
            flag = true;
        }else if(enbExcepMap.get("ALL")!=null
                && enbExcepMap.get("ALL").get(datatype+"-"+pmname) !=null
                && enbExcepMap.get("ALL").get(datatype+"-"+pmname).contains(errortype)){
            flag = true;
        }

        return flag;
    }

    /**
     * 将缺失的网元信息存入数据库
     * @param task 任务信息
     * @param objectMapSet 按照地市缺失的子网元信息
     * @param existSwVersion 软件版本
     * @param lognrms 记录存储的异常指标项
     */
   public Map<String,Map<String,Map<String,Integer>>>  insertNRMLoseDataInfo(UdpTask task,Map<String,Set<String>> objectMapSet,Set<String> existSwVersion, Map<String,Set<String>> lognrms ){
        //删除原有记录
       udpLoseDatatypeService.deletebytaskid(task.getIntid());
       String netype = task.getNetype();
       if(netype.contains("CSCF"))
           netype = "CSCF";
       if(netype.equalsIgnoreCase("PSBC"))
           netype="VOLTESBC";

       Map<String,Set<String>> exemMap = udpExempDtypeService.selectByParas(netype,task.getType(),task.getVersion());

       //通过版本没查到，通过网元再次查找
       if(exemMap.size()==0){
           Log.info("通过网元查询豁免项");
           exemMap = udpExempDtypeService.selectByParas(netype,task.getType(),"");
       }
       Log.info(netype+" 网元查询的豁免类别个数为："+exemMap.size());

       //记录缺少的网元类型
       for(String cityname : objectMapSet.keySet()){
           Set<String> objectSet = objectMapSet.get(cityname);
           String existObjs = "";
           for(String obj : objectSet)
                existObjs += obj +",";
           Log.info("已存在的子网元有："+existObjs);

           for(String datatype : nrmMap.keySet()){
               if(!objectSet.contains(datatype) && isLoseDType(datatype,objectSet,exemMap)){
                   String proCity = provinceMap.get(task.getProvince());
                   udpLogUtil.takeLoseDatatype(proCity+"-"+cityname,datatype);
                   UdpLoseDatatype udpLoseDatatype = new UdpLoseDatatype(task.getIntid(),proCity,cityname,datatype);
                   udpLoseDatatypeService.insert(udpLoseDatatype);
               }
           }
       }

       //取所有地市网元的并集
       Set<String> allObj = new HashSet<>();
       for(String city : objectMapSet.keySet()) {
           Set<String> objectSet = objectMapSet.get(city);
           for(String dataType : objectSet){
               if(!allObj.contains(dataType)){
                   allObj.add(dataType);
               }
           }
       }

       Map<String,Map<String,Map<String,Integer>>> excepmap = new HashMap<>();

       //按地市记录入库没有上报的数据
       for(String cityname : objectMapSet.keySet()){
           for(String datatype : nrmMap.keySet()) {
               //如果不存在且是缺失类型
               if (!allObj.contains(datatype)) {
                   //如果是缺失类型，入库
                   if(isLoseDType(datatype,allObj,exemMap)){
                       Map<String,UdpNrm> udpNrmMap = nrmMap.get(datatype);
                       //每个软件版本都入库
                       for(String swVersion : existSwVersion){
                           for (String pmname : udpNrmMap.keySet()){
                               LogUdp logUdp = new LogUdp(task.getIntid(),cityname,swVersion,datatype,pmname,udpNrmMap.get(pmname).getNrmGrade(),"未上报");
                               logUdpService.insert(logUdp);

                               //将异常数据记录，为后续写规范使用
                               Set<String> dapmSet = new HashSet<>();
                               if(lognrms.containsKey(swVersion)){
                                   dapmSet = lognrms.get(swVersion);
                               }else {
                                   lognrms.put(swVersion,dapmSet);
                               }
                               dapmSet.add(datatype+"-"+pmname);
                           }
                       }
                   }else{ //如果不是，处理不适用的分母个数
                       Map<String,Map<String,Integer>> citymap = new HashMap<>();
                       if(excepmap.containsKey(cityname)){
                           citymap = excepmap.get(cityname);
                       }else {
                           excepmap.put(cityname,citymap);
                       }

                       for(String swVersion : existSwVersion){
                           Map<String,Integer> gradmap = new HashMap<>();
                           if(citymap.containsKey(swVersion)){
                               gradmap = citymap.get(swVersion);
                           }else {
                               citymap.put(swVersion,gradmap);
                           }

                           Map<String,UdpNrm> udpnrmMap = nrmMap.get(datatype);

                           for(String nrmname : udpnrmMap.keySet()){
                               UdpNrm udpNrm = udpnrmMap.get(nrmname);
                               if(gradmap.containsKey(udpNrm.getNrmGrade())){
                                   gradmap.put(udpNrm.getNrmGrade(),gradmap.get(udpNrm.getNrmGrade())+1);
                               }else {
                                   gradmap.put(udpNrm.getNrmGrade(),1);
                               }
                           }
                       }
                   }

               }
           }
       }
       return  excepmap;
    }

    /**
     * 将缺失的网元信息存入数据库
     * @param task 任务信息
     * @param objectMapSet 按照地市缺失的子网元信息
     * @param existSwVersion 软件版本
     */
    public Map<String,Map<String,Map<String,Integer>>> insertPMLoseDataInfo(UdpTask task,Map<String,Set<String>> objectMapSet,Set<String> existSwVersion,Map<String,Set<String>> logpms){
        //删除原有记录
        udpLoseDatatypeService.deletebytaskid(task.getIntid());
        String netype = task.getNetype();
        if(netype.contains("CSCF"))
            netype = "CSCF";
        if(netype.equalsIgnoreCase("PSBC"))
            netype="VOLTESBC";

        Map<String,Set<String>> exemMap = udpExempDtypeService.selectByParas(netype,task.getType(),task.getVersion());

        //通过版本没查到，通过网元再次查找
        if(exemMap.size()==0){
            Log.info("通过网元查询豁免项");
            exemMap = udpExempDtypeService.selectByParas(netype,task.getType(),"");
        }

        Log.info(netype+" 网元查询的豁免类别个数为："+exemMap.size());

        //记录缺少的网元类型
        for(String cityname : objectMapSet.keySet()){
            Set<String> objectSet = objectMapSet.get(cityname);
            String existObjs = "";
            for(String obj : objectSet)
                existObjs += obj +",";
            Log.info("已存在的子网元有："+existObjs);
            for(String datatype : pmMap.keySet()){
                if(!objectSet.contains(datatype)&& isLoseDType(datatype,objectSet,exemMap)){
                    String proCity = provinceMap.get(task.getProvince());
                    udpLogUtil.takeLoseDatatype(proCity+"-"+cityname,datatype);
                    UdpLoseDatatype udpLoseDatatype = new UdpLoseDatatype(task.getIntid(),proCity,cityname,datatype);
                    udpLoseDatatypeService.insert(udpLoseDatatype);


                }
            }
        }

        //取所有地市网元的并集
        Set<String> allObj = new HashSet<>();
        for(String city : objectMapSet.keySet()) {
            Set<String> objectSet = objectMapSet.get(city);
            for(String dataType : objectSet){
                if(!allObj.contains(dataType)){
                    allObj.add(dataType);
                }
            }
        }

        Map<String,Map<String,Map<String,Integer>>> excepmap = new HashMap<>();

        //入库没有上报的数据
        for(String cityname : objectMapSet.keySet()){
            for(String datatype : pmMap.keySet()) {
                if (!allObj.contains(datatype) ) {
                    if(isLoseDType(datatype,allObj,exemMap)){
                        Map<String,UdpPm> udpPmMap = pmMap.get(datatype);
                        //每个软件版本都入库
                        for(String swVersion : existSwVersion){
                            for (String pmname : udpPmMap.keySet()){
                                LogUdp logUdp = new LogUdp(task.getIntid(),cityname,swVersion,datatype,pmname,udpPmMap.get(pmname).getPmGrade(),"未上报");
                                logUdpService.insert(logUdp);

                                //将异常数据记录，为后续写规范使用
                                Set<String> dapmSet = new HashSet<>();
                                if(logpms.containsKey(swVersion)){
                                    dapmSet = logpms.get(swVersion);
                                }else {
                                    logpms.put(swVersion,dapmSet);
                                }
                                dapmSet.add(datatype+"-"+pmname);
                            }
                        }
                    }else {//如果不是，处理不适用的分母个数
                        Map<String,Map<String,Integer>> citymap = new HashMap<>();
                        if(excepmap.containsKey(cityname)){
                            citymap = excepmap.get(cityname);
                        }else {
                            excepmap.put(cityname,citymap);
                        }

                        for(String swVersion : existSwVersion){
                            Map<String,Integer> gradmap = new HashMap<>();
                            if(citymap.containsKey(swVersion)){
                                gradmap = citymap.get(swVersion);
                            }else {
                                citymap.put(swVersion,gradmap);
                            }

                            Map<String,UdpPm> udpPmMap = pmMap.get(datatype);

                            for(String pmname : udpPmMap.keySet()){
                                UdpPm udpPm = udpPmMap.get(pmname);
                                if(gradmap.containsKey(udpPm.getPmGrade())){
                                    gradmap.put(udpPm.getPmGrade(),gradmap.get(udpPm.getPmGrade())+1);
                                }else {
                                    gradmap.put(udpPm.getPmGrade(), 1);
                                }
                            }
                        }

                    }

                }
            }
        }
        return excepmap;
    }

    /**
     * 判断是不是缺失子网元类型
     * @param datatype 子网元类型
     * @param existSet 存在的所有网元类型
     * @param exemMap 豁免项
     * @return 是否是缺失类型
     */
   public boolean isLoseDType(String datatype,Set<String> existSet,Map<String,Set<String>> exemMap){


       //该网元不存在豁免子网元，返回true
        if(exemMap.size()==0){
          return true;
        }


       //全部豁免
       Set<String> allExem = exemMap.get("all");
       //  如果是全部豁免
       if(allExem != null && allExem.contains(datatype)){
           return false;
       }
       //分布式豁免
       Set<String> distExem = exemMap.get("dist");
       //集中式豁免
       Set<String> centExem = exemMap.get("cent");


       if(null !=distExem && null != centExem){
           //如果是分布式子网元
           if(distExem.contains(datatype)){
               //存在的子网元中包含所有的集中式则返回false，否则返回true
               if(existSet.containsAll(centExem)){
                   return false;
               }
           }

           //如果是集中式子网元
           if(centExem.contains(datatype)){
               //存在的子网元中包含所有的分布式则返回false，否则返回true
               if(existSet.containsAll(distExem)){
                   return false;
               }
           }
       }
       Log.info("子网元："+ datatype +"不在豁免项内");
       return true;
   }


    /**
     * 入库该次任务的各级指标总数
     * @param taskid 任务ID
     * @param existSwVersion 出现的软件版本
     * @param objectMapSet 出现过的地市信息
     * @param excepnum 不适用项，地市、软件版本、级别
     */
    public void insertTotalNRMNum(int taskid,Set<String> existSwVersion,Map<String,Set<String>> objectMapSet,Map<String,Map<String,Map<String,Integer>>> excepnum){
        //删除该任务的记录
        udpGradeTotalService.deletebytaskid(taskid);
        Map<String,Integer> gradetotal = new HashMap<>();
        for(String datatype : nrmMap.keySet()){
            Map<String,UdpNrm> udpNrmMap = nrmMap.get(datatype);
            for(String nrmname : udpNrmMap.keySet()){
                UdpNrm udpNrm = udpNrmMap.get(nrmname);
                int num = 1;
                if(gradetotal.containsKey(udpNrm.getNrmGrade())){
                   num  = gradetotal.get(udpNrm.getNrmGrade())+1;
                }
                gradetotal.put(udpNrm.getNrmGrade(),num);
            }
        }

        for(String cityname : objectMapSet.keySet()){
            for(String swversion : existSwVersion){
                for(String grade : gradetotal.keySet()){
                   int excenum =0;
                    try{
                        excenum=excepnum.get(cityname).get(swversion).get(grade);
                    }catch (Exception e){
                        excenum= 0;
                    }

                    UdpGradeTotal udpGradeTotal = new UdpGradeTotal(taskid,cityname,swversion,grade,gradetotal.get(grade),excenum);
                    udpGradeTotalService.insert(udpGradeTotal);
                }
            }
        }


    }

    /**
     * 入库该次任务的各级指标总数
     * @param taskid 任务号
     * @param existSwVersion 软件版本
     * @param objectMapSet 存在的地市
     * @param excepnum 不适用项，地市、软件版本、级别
     */
    public void insertTotalPmNum(int taskid,Set<String> existSwVersion ,Map<String,Set<String>> objectMapSet,Map<String,Map<String,Map<String,Integer>>> excepnum){
        //删除该任务的记录
        udpGradeTotalService.deletebytaskid(taskid);
        Map<String,Integer> gradetotal = new HashMap<>();
        for(String datatype : pmMap.keySet()){
            Map<String,UdpPm> udpPmMap = pmMap.get(datatype);
            for(String pmname : udpPmMap.keySet()){
                UdpPm udpPm = udpPmMap.get(pmname);
                int num = 1;
                if(gradetotal.containsKey(udpPm.getPmGrade())){
                    num  = gradetotal.get(udpPm.getPmGrade())+1;
                }
                gradetotal.put(udpPm.getPmGrade(),num);
            }
        }

        for(String cityname : objectMapSet.keySet()){
            for(String swversion : existSwVersion){
                for(String grade : gradetotal.keySet()){
                    int excenum =0;
                    try{
                        excenum=excepnum.get(cityname).get(swversion).get(grade);
                    }catch (Exception e){
                        excenum= 0;
                    }
                    UdpGradeTotal udpGradeTotal = new UdpGradeTotal(taskid,cityname,swversion,grade,gradetotal.get(grade),excenum);
                    udpGradeTotalService.insert(udpGradeTotal);
                }
            }
        }


    }

    /**
     * 入库资源获取到的dn信息
     * @param province 省份
     * @param vendor 厂家
     * @param netype 网元
     * @param version 版本
     * @param dnSwversion dn和软件版本号
     */
    private void insertDnSwVersion(String province,String vendor,String netype,String version,Map<String,String> dnSwversion){
        for(String dn : dnSwversion.keySet()){
            MngdnSwversion mngdnSwversion = new MngdnSwversion(province,vendor,netype,version,dn,dnSwversion.get(dn));
            //库里不存在入库
            if(mngdnSwversionService.selectByParas(mngdnSwversion) ==null){
                mngdnSwversionService.insert(mngdnSwversion);
            }
        }
    }


    /**
     * 入库找不到软件版本的Dn信息
     * @param taskid 任务id
     * @param losednList 记录的信息
     */
    public void insertLoseDn (int taskid,List<UdpLosedn> losednList){
        //删除库中已存的信息
        udpLosednService.deletebytaskid(taskid);

        int count=0;
        List<UdpLosedn> batchList = new ArrayList<>(1600);
        for(UdpLosedn udpLosedn : losednList){
            count++;
            UdpLosedn newudp = new UdpLosedn(taskid,udpLosedn.getDatatype(),udpLosedn.getFilename(),udpLosedn.getDn());
            batchList.add(newudp);

            //批量入库一千条
            if(count%1000==0){
                udpLosednService.batchinsert(batchList);
                //入库完清空list
                batchList.clear();
            }
        }
        if(batchList.size()>0){
            udpLosednService.batchinsert(batchList);
        }
    }

    /**
     * 根据文件类型找文件
     * @param fileType 文件类型（NRM/PM）
     * @return 是否存在文件
     */
    private boolean checkUdpFiles(String fileType){
        for(String udpfilePath : udpfilePaths){
            fileUtil.findGivenFiles(udpfilePath,udpTask.getVersion().toLowerCase(),udpFiles);
        }

        if(udpFiles.size()==0){
            return  false;
        }

        //如果是NRM把ManagedElement文件单独拿出来，为了获取软件版本
        if(fileType.equalsIgnoreCase("NRM")){
            Iterator<File> it = udpFiles.iterator();
            while(it.hasNext()){
                File udpfile = it.next();
                //如果文件名包含ManagedElement，将文件加入到ManagedElement网元文件列表
                if(udpfile.getName().contains("ManagedElement")){
                    mngdFiles.add(udpfile);
                }
            }

            //如果不区分网元，managedElement是所有网元
            if(mngdFiles.size()==0){
                mngdFiles.addAll(udpFiles) ;
            }
        }

        return true;
    }

}
