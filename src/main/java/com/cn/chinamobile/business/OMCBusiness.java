package com.cn.chinamobile.business;

import com.cn.chinamobile.dao.Dao;
import com.cn.chinamobile.entity.ColType;
import com.cn.chinamobile.entity.CompareEntity;
import com.cn.chinamobile.pojo.mybatis.FileCompleteWithBLOBs;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.service.AlgMappingService;
import com.cn.chinamobile.service.DynamicSqlService;
import com.cn.chinamobile.service.FileCompleteService;
import com.cn.chinamobile.util.*;
import org.mozilla.intl.chardet.nsDetector;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by zh on 2017/5/22.
 */
@Service
public class OMCBusiness implements Callable {

    @Resource
    private AlgMappingService algMappingService;

    @Resource
    private Dao dao;
    @Resource
    private DynamicSqlService dynamicSqlService;
    @Resource
    FileCompleteService fileCompleteService;

    private List<File> omcFiles=new ArrayList<File>();
    private List<File> omcFilesHaveBK;
    private List<File> countercheckFiles;
    private String neType;
    private String dataType;
    private String parsetime;
    private String tmpFilePrefix;
    LinkedHashMap<String, ColType> colTypeLinkedHashMap;
    private Map<String, String> counterexpresionMap;
    private String vendorname;
    private String version;
    FileUtil fileUtil = new FileUtil();
    private PMKPIBusiness pmkpiBusiness;
    private List<String> pms = null;
    private int taskid;
    private String province;

    private Map<String, String> userSwMap;
    private Map<String, String> swVersionMap;
    private String vendor;

    public void setParas(List<File> omcFiles, List<File> countercheckFiles, String neType, String dataType, String parsetime, String vendorname, String version, String tmpFilePrefix, int taskid, Map<String, String> userSwMap,String province,String vendor) {
        this.omcFilesHaveBK = omcFiles;
        this.countercheckFiles = countercheckFiles;
        this.neType = neType;
        this.dataType = dataType;
        this.parsetime = parsetime;
        this.vendorname = vendorname;
        this.version = version;
        this.tmpFilePrefix = tmpFilePrefix;
        this.taskid = taskid;
        this.userSwMap = userSwMap;
        this.province=province;
        this.vendor=vendor;
    }

    @Override
    public CompareEntity call() throws Exception {

        DataUtil dataUtil = new DataUtil();
        //定义check文件的map
        Map<String, Long> checkFileMap = new HashMap<>();
        //读取omccheck文件
        for (File checkFile : countercheckFiles) {
            String encode = new FileCharsetDetector().guessFileEncoding(checkFile, new nsDetector());
            checkFileMap.putAll(dataUtil.readCheckFile(checkFile.getAbsolutePath(), encode));
        }
        //去除文件里的_BK结尾文件
        for(int i=0;i<omcFilesHaveBK.size();i++){
            if(omcFilesHaveBK.get(i).getName().endsWith("_BK.csv")){
                omcFilesHaveBK.get(i).delete();
                continue;
            }
            omcFiles.add( omcFilesHaveBK.get(i));
        }

        //如果是没有OMC文件直接返回
        if (omcFiles.size() == 0) {
            return null;
        }
        //创建counter临时表
        String omctable = createCounterTable();

        if (neType.equalsIgnoreCase("ENB")) {
            swVersionMap = algMappingService.selectByParas(neType, vendorname, version);
        }
        //将数据导入临时表
        FileCompleteWithBLOBs fileComplete = null;
        for (File omcfile : omcFiles) {
            File parsefile = null;
            boolean del = false;
            //假如是压缩文件，先解压
            if (omcfile.getName().endsWith(".gz")) {
                parsefile = new GZIPUtil().decompress(omcfile);
                del = true;
            }else {
                parsefile = omcfile;
            }


            //检测omc文件的编码格式
            String encode = new FileCharsetDetector().guessFileEncoding(parsefile, new nsDetector());
            if(!("UTF-8".equals(encode))) {
                //赋予文件路径
                parsefile = copyFileToUTF(parsefile);
                encode="UTF-8";
            }

            //如果是ENB，先回填映射的软件版本
            if (neType.equalsIgnoreCase("ENB")) {
                //修改方法传入taskid
                parsefile = new FileUtil().backCounterSwver(parsefile, encode, userSwMap, swVersionMap, del, taskid,province,vendor);
            }

            //检测文件完整性
            fileComplete = new FileCompleteWithBLOBs().checkComPlete(parsefile, encode, "omcfile_", dataType, vendorname, version, taskid);
            fileComplete.setFactlength(omcfile.length());
            fileComplete.setChecklength(checkFileMap.get(omcfile.getName()));
            fileComplete.setFilename(omcfile.getName());

            //封装方法单独使用
            ExcelUtil excelUtil = new ExcelUtil();
            //文件的输入输出路径先写死
            String path0 = ContentInfo.FILE_TEMP_PATH+"\\"+province+"_RECORD\\"+vendor+"\\"+province+"_"+vendor+"_"+"OMC_TIMEDATA.xlsx";
            String sheetName = "OMC_TIMEDATA";
            //返回不需插入数据库的数据
            Map<String, ArrayList<String>> userLabelMap=excelUtil.handlePartTimeDataExcel(parsefile.getAbsolutePath(), encode, taskid, path0, sheetName,province);

            //垃圾数据导出路径
            String path=ContentInfo.FILE_TEMP_PATH+"\\"+province+"_RECORD\\"+vendor+"\\"+province+"_"+vendor+"_"+"OMC_TRASH.xlsx";
            //将数据过滤一遍
            boolean parseflag = dynamicSqlService.importDataAllTime(parsefile.getAbsolutePath(), omctable, colTypeLinkedHashMap, dao.getConnection(), ",", encode,userLabelMap,path);

            if (parseflag) {
                fileComplete.setParsestatus(1);
            } else {
                fileComplete.setParsestatus(0);
            }
            fileCompleteService.insert(fileComplete);

            //如果是压缩文件，删除解压后的文件
            //如果是ENB文件，删除回填软件版本的文件
            if (omcfile.getName().endsWith(".gz") || neType.equalsIgnoreCase("ENB")) {
                parsefile.delete();
            }
        }
        System.out.println("获取跟模板对比缺失的指标counter");
        //获取跟模板对比缺失的指标counter
        List<String> lostcounter = new ArrayList<>();
        String losecounters = fileComplete.getLosepmdetail();
        if (!losecounters.isEmpty()) {
            for (String losec : losecounters.split(",")) {
                lostcounter.add(losec);
            }
        }


        //获取汇总文件
        File omcpmFile = getcounterTPM(omctable, lostcounter);

        pmkpiBusiness = new PMKPIBusiness(neType, dataType, parsetime, vendorname, version, tmpFilePrefix, dao);

        //创建临时表
        String tablename = pmkpiBusiness.createtmpTable("omcpm_", pms);

        //将数据导入临时表
        if (neType.equalsIgnoreCase("ENB")) {
            //ENB数据单独处理
            pmkpiBusiness.importENBData(omcpmFile, tablename, parsetime);

        } else {
            pmkpiBusiness.importData(omcpmFile, tablename);
        }


        //汇总PM
        String groupbyname = "";
        //如果EutranCellTdd类型，用小区名称分组，其他用设备名称分组
        if (dataType.equalsIgnoreCase("EutranCellTdd")) {
            groupbyname = ContentInfo.COUNTER_GROUPBY_COLUMENAME;
        } else {
            groupbyname = ContentInfo.COUNTER_MME_GCOLUMN;
        }



        //汇总数据
        File pmsumFile = pmkpiBusiness.getSumPM(tablename, "omcsumpm_", groupbyname);

        //创建汇总表
        String sumtable = pmkpiBusiness.createsmTable("omcsumpm_");
        //将汇总数据入库
        pmkpiBusiness.importData(pmsumFile, sumtable);

        //获取KPI计算指标
        File kpiFile = pmkpiBusiness.getKPIs(sumtable, "omckpi_");

        dao.closeConnect();

        CompareEntity compareEntity = new CompareEntity(pmsumFile, kpiFile);
        return compareEntity;
    }

    /**
     *
     * 功能描述: 将gb2312 csv文件转为utf_8的编码
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/7/6 14:20
     */
    public File copyFileToUTF(File parsefile){
        String srcPath= parsefile.getParentFile().getAbsolutePath() + File.separator + parsefile.getName();
        String srcPathBK="";
        //重新转存文件
        try{
            //另存为文件为utf-8
            //将文件转储为utf-8形式，以gbk读取文件保证读取到正确的字节
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(srcPath), "gbk"));
            //更改文件路径
            srcPathBK=srcPath.substring(0,srcPath.length()-4);
            srcPathBK=srcPathBK+"_BK.csv";
            //文件内容输出到新bk文件中，利用底层另存文件为utf-8格式
            BufferedWriter bw = new BufferedWriter(new FileWriter(srcPathBK));
            String line0;
            while((line0 = br.readLine()) != null) {
                bw.write(line0);
                bw.newLine();
                bw.flush();
            }
            bw.close();
            br.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        //赋予文件路径
       return new File(srcPathBK);
    }

    /**
     * 创建counter临时表
     *
     * @return
     */
    private String createCounterTable() {
        String tablename = "omc_" + tmpFilePrefix.replace(File.separator, "_") + "_" + dataType + "_" + parsetime.replace("-", "");
        tablename = tablename.replace(".", "");
        String deletesql = "drop table if exists " + tablename;
        dao.exeSql(deletesql);
        File omcFile = omcFiles.get(0);
        String encode = new FileCharsetDetector().guessFileEncoding(omcFile, new nsDetector());
        String[] metedata = dynamicSqlService.getMetaData(omcFile.getAbsolutePath(), encode);
        colTypeLinkedHashMap = dynamicSqlService.getColMap(metedata, ",", neType);
        String omccreatesql = dynamicSqlService.getCreateCounterSql(tablename, colTypeLinkedHashMap, neType);
        Log.info("create omctable :" + omccreatesql);
        dao.exeSql(omccreatesql);

        return tablename;
    }

    private File getcounterTPM(String tablename, List<String> lostcounter) {
        String counterpath = ContentInfo.DQA_VERSION_BASED_PATH + File.separator + "counter" + File.separator;
        String counterconfig = counterpath + IniDomain.versionconfig.get(dataType).get(vendorname).get(version);
        Map<String, List<String>> counterTPMmap;
        //ENB网元使用区分软件版本算法
        if (neType.equalsIgnoreCase("ENB")) {
            Map<String, Map<String, String>> counterexpreMap = new DataUtil().getSwverExpresMap(counterconfig);
            counterTPMmap = dynamicSqlService.getSwCounterTPMSql(tablename, colTypeLinkedHashMap, counterexpreMap, lostcounter);
        } else {
            counterexpresionMap = new DataUtil().getExpressionMap(counterconfig);
            counterTPMmap = dynamicSqlService.getCounterTPMSql(tablename, colTypeLinkedHashMap, counterexpresionMap, lostcounter);
        }

        String sumcoutersql = "";
        for (Map.Entry entry : counterTPMmap.entrySet()) {
            sumcoutersql = (String) entry.getKey();
            pms = (List<String>) entry.getValue();
        }
        File omcpmFile = fileUtil.initWriteFile(dataType, parsetime, tmpFilePrefix, "omcpm");
        List<Map<String, String>> pmdatas = dao.getResultSetMap(sumcoutersql, pms);
        fileUtil.writedatas(omcpmFile, pmdatas, "|");
        //释放引用对象
        pmdatas.clear();
        return omcpmFile;

    }


}
