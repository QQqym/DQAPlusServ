package com.cn.chinamobile.parse;

import com.cn.chinamobile.pojo.mybatis.FileCompleteWithBLOBs;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.service.FileCompleteService;
import com.cn.chinamobile.util.*;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.*;

/**
 * Created by zh on 2017/4/25.
 * xml解析文件
 */
public class ParseXml {

    private UdpCheck udpCheck = new UdpCheck();

    CommonWrite commonWrite;
    //记录需要的指标集
    List<String> pmNameList = new ArrayList<String>(100);
    FileUtil fileUtil = new FileUtil();
    FileCompleteService fileCompleteService;
    long filelength = 0;

    //小区时间set 为了过滤重复分时间和小区
    private Set<String> userlabelTimeSet;

    //记录小区名和对应的软件版本
    private Map<String, String> userlabelSwMap;

    public File parse(List<File> parseFiles, int taskid, String netype, String dataType, String time, String tmpFilePrefix, Map<String, Long> checkinfo, Map<String, String> provinceMap, Map<String, String> cityMap, Map<String, String> dnSwversionMap, String province,String verdor) {
        userlabelTimeSet = new HashSet<>(10240);
        userlabelSwMap = new HashMap<>(1024);
        fileCompleteService = IniDomain.ct.getBean(FileCompleteService.class);
        File writeFile = fileUtil.initWriteFile(dataType, time, tmpFilePrefix, "bx");
        commonWrite = new CommonWrite(writeFile);
        //通过网元-子网元获取指标列表
        pmNameList = IniDomain.qcimap.get(netype + "-" + dataType);
        commonWrite.initFile(pmNameList);
        for (File file : parseFiles) {
            filelength = file.length();
            //gz压缩文件
            if (file.getName().contains(".gz")) {
                File defile = new GZIPUtil().decompress(file);
                if (defile != null)
                    parseFile(province,verdor, defile, dataType, true, taskid, file.getName(), checkinfo, provinceMap, cityMap, dnSwversionMap);
            } else {
                parseFile(province,verdor, file, dataType, false, taskid, file.getName(), checkinfo, provinceMap, cityMap, dnSwversionMap);
            }
        }
        commonWrite.closeFile();
        return writeFile;
    }

    /**
     * 解析xml文件
     *
     * @param province  省份简称
     * @param file      文件
     * @param dataType  子网元类型
     * @param delete    解析后是否删除文件
     * @param taskid    解析任务的id
     * @param filename  文件名称
     * @param checkinfo check文件的信息 key是文件名，value是文件大小
     */
    public void parseFile(String province,String verdor, File file, String dataType, boolean delete, int taskid, String filename, Map<String, Long> checkinfo, Map<String, String> provinceMap, Map<String, String> cityMap, Map<String, String> dnSwversionMap) {
        InputStream inputStream = null;
        FileCompleteWithBLOBs fileComplete = new FileCompleteWithBLOBs();
        fileComplete.setTaskid(taskid);
        fileComplete.setSubid("bxfile_" + taskid);
        fileComplete.setFilename(filename);
        fileComplete.setFactlength(filelength);
        fileComplete.setChecklength(checkinfo.get(filename));

        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            inputStream = new FileInputStream(file.getAbsolutePath());
            XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);
            Map<String, String> fileHeader = new CaseInsensitiveMap();
            Map<String, String> dataMap = null;
            int event = -1;
            //记录全部指标集,key是指标的序号，value是指标名
            Map<Integer, String> pmNNameMap = null;

            //记录全部指标集
            Set<String> pMnameset = new HashSet<>();

            //记录是不是需要的网元
            Boolean istrueobjectype = false;

            //记录标签sn的指标名
            String sn = "";

            //记录开始标签
            String localname = "";

            //记录pm标签的个数
            int pmnum = 0;

            //记录值为null的指标
            Map<String, Integer> nullpmMap = new HashMap<>();

            //记录是否将dn中的指标放入指标集
            boolean addflag = false;
            String cityname = "";
            //用于写无设备版本号数据进excel
            ExcelUtil excelUtil = new ExcelUtil();
            //文件的输入输出路径先写死
            String path = ContentInfo.FILE_TEMP_PATH+"\\"+province+"_RECORD\\"+verdor+"\\"+province+"_"+verdor+"_"+"NBI_SWVERSION.xlsx";
            //sheetname
            String sheetName = "NBI_SWVERSION";
            //产生XSSFWorkbook 列
            String[] columns={"taskid","userlabel","dn","verder"};
            //产生XSSFWorkbook
            excelUtil.createXSSFWorkbook(path,sheetName,columns);
            //verderName
            String verder="";

            while (reader.hasNext()) {
                event = reader.next();
                switch (event) {
                    //开始标签
                    case XMLStreamConstants.START_ELEMENT:
                        localname = reader.getLocalName();
//                        if(localname.equals(ContentInfo.DN_PREFIX)){
//                            String dnPrefix =  reader.getElementText();
//                            cityname = new UdpCheck().getCityname(dnPrefix,cityMap,provinceMap);
//                            fileHeader.put("city",cityname);
//                        } else
                        if (localname.equalsIgnoreCase(ContentInfo.VENDOR_NAME)) {
                            fileHeader.put(localname, reader.getElementText());
                            verder=fileHeader.get(localname);
                        } else if (localname.equalsIgnoreCase(ContentInfo.BEGIN_TIME) || localname.equalsIgnoreCase(ContentInfo.END_TIME) || localname.equalsIgnoreCase("StartTime")) {
                            //处理开始结束时间
                            String time = reader.getElementText();
                            if (time.indexOf("+") > 0) {
                                time = time.substring(0, time.indexOf("+"));
                            }
                            time = time.replace("T", " ");
                            if (localname.equalsIgnoreCase("StartTime")) {
                                fileHeader.put(ContentInfo.BEGIN_TIME, time);
                            } else {
                                fileHeader.put(localname, time);
                            }

                        } else if (localname.equals(ContentInfo.OBJECT_TYPE)) {
                            String objecttype = reader.getElementText();
                            //判断是不是需要的网元
                            if (objecttype.equalsIgnoreCase(dataType)) {
                                istrueobjectype = true;
                            } else {
                                istrueobjectype = false;
                            }
                        } else if (localname.equalsIgnoreCase(ContentInfo.PMNAME)) {
                            if (istrueobjectype) {
                                pmNNameMap = new HashMap<Integer, String>();
                                pMnameset = new HashSet<>();
                            }
                        } else if (localname.equalsIgnoreCase(ContentInfo.N)) {
                            if (istrueobjectype) {
                                String ivalue = reader.getAttributeValue(0);
                                String nName = reader.getElementText();
                                //全部指标解析，不过滤，为了不区分大小写配置
                                pmNNameMap.put(Integer.parseInt(ivalue), nName);
                                pMnameset.add(nName);
                            }
                        } else if (localname.equalsIgnoreCase(ContentInfo.PM) || localname.equalsIgnoreCase(ContentInfo.OBJECT)) {
                            dataMap = new CaseInsensitiveMap();
                            if (istrueobjectype) {
                                //pm个数+1
                                pmnum++;
                                int attricount = reader.getAttributeCount();
                                String dn = "";
                                String userlabel = "";
                                for (int i = 0; i < attricount; i++) {
                                    String name = reader.getAttributeLocalName(i);
                                    String value = reader.getAttributeValue(i);
                                    if (!addflag) {
                                        pMnameset.add(name);
                                    }
                                    dataMap.put(name, value);
                                    if (name.equalsIgnoreCase("Dn")) {
                                        dn = value;
                                        //只在第一个pm放入
                                        if (!addflag) {
                                            getDnPm(value, pMnameset);
                                        }
                                        getDnMap(pmNameList, value, dataMap);

                                        String dnPrefix = dn.substring(0, dn.indexOf(","));
                                        cityname = udpCheck.getCityname(province, dnPrefix, cityMap, provinceMap);
                                        dataMap.put("city", cityname);
                                    }
                                    if (name.equalsIgnoreCase("UserLabel")) {
                                        userlabel = value;
                                    }
                                    //最后一个指标，将添加flag设置为true
                                    if (i == attricount - 1 && !addflag) {
                                        addflag = true;
                                    }
                                }

                                //如果是ENB网元，记录小区名的软件版本号
                                if (dataType.equalsIgnoreCase("EutranCellTdd")) {
                                    String swversion = udpCheck.getSwversion(dn, dnSwversionMap);
                                    //这里如果是""说明Dn 没有对应的设备软件版本,记录相应信息。
                                    if (swversion == "" || swversion == null) {
                                        excelUtil.createRowData(String.valueOf(taskid),userlabel,dn,verder);
                                    }
                                    //dataMap存入软件版本
                                    dataMap.put(ContentInfo.COUNTER_SWVERION, swversion);
                                    //如果没存放
                                    if (!userlabelSwMap.containsKey(userlabel))
                                        userlabelSwMap.put(userlabel, swversion);
                                }
                            }
                        } else if (localname.equalsIgnoreCase(ContentInfo.V)) {
                            if (istrueobjectype) {
                                int cvivalue = Integer.parseInt(reader.getAttributeValue(0));
                                dataMap.put(pmNNameMap.get(cvivalue), reader.getElementText());
                            }
                        } else if (localname.equalsIgnoreCase(ContentInfo.SN)) {
                            sn = reader.getElementText();
                            //将SN指标加入指标集
                            if (!pMnameset.contains(sn)) {
                                pMnameset.add(sn);
                            }
                        } else if (localname.equalsIgnoreCase(ContentInfo.SV)) {
                            if (istrueobjectype) {
                                dataMap.put(sn, reader.getElementText());
                            }
                        }
                    case XMLStreamConstants.END_ELEMENT:
                        String endname = reader.getLocalName();
                        //文件结束
                        if (endname.equalsIgnoreCase(ContentInfo.PMFILE)) {
                            //为缺少的指标信息赋值
                            int losepmnum = 0;
                            StringBuffer losepmdetail = new StringBuffer();

                            //避免发生空指针异常
                            if (pmNameList == null) {
                                pmNameList = new ArrayList<String>();
                            }
                            for (String spmname : pmNameList) {
                                //如果全部指标集里不包含采集指标，缺失数加1，缺失详情添加该指标名
                                if (!pMnameset.contains(spmname)) {
                                    losepmnum++;
                                    losepmdetail.append(spmname).append(",");
                                }
                            }
                            //如果缺失PM详情长度大于1，删去最后一位,
                            if (losepmdetail.length() > 0) {
                                losepmdetail.deleteCharAt(losepmdetail.length() - 1);
                            }
                            //给实体类赋值
                            fileComplete.setLosepmnum(losepmnum);
                            fileComplete.setLosepmdetail(losepmdetail.toString());
                            //为pm个数赋值
                            fileComplete.setPmnum(pmnum);
                            //为值为null的赋值
                            StringBuffer nullpmdetail = new StringBuffer();
                            StringBuffer nullpmnum = new StringBuffer();
                            for (String keypm : nullpmMap.keySet()) {
                                nullpmdetail.append(keypm).append(",");
                                nullpmnum.append(nullpmMap.get(keypm)).append(",");
                            }
                            //删除最后一位,
                            if (nullpmdetail.length() > 0) {
                                nullpmdetail.deleteCharAt(nullpmdetail.length() - 1);
                            }
                            if (nullpmnum.length() > 0) {
                                nullpmnum.deleteCharAt(nullpmnum.length() - 1);
                            }
                            fileComplete.setNullpmdetail(nullpmdetail.toString());
                            fileComplete.setNullpmnum(nullpmnum.toString());
                        }
                        if ((endname.equalsIgnoreCase(ContentInfo.PM) || endname.equalsIgnoreCase(ContentInfo.OBJECT)) && !endname.equals(localname)) {
                            if (istrueobjectype) {
                                //把开始时间等公共字段放入dataMap
                                dataMap.putAll(fileHeader);

                                //输出数据
                                //ENB数据按照时间和小区名字，没有输出过就输出，去重 小区-时间
                                String userlabetime = dataMap.get("userlabel") + "-" + fileHeader.get(ContentInfo.BEGIN_TIME);
                                if (dataType.equalsIgnoreCase("EutranCellTdd")) {
                                    //如果存在小区-时间跳过，过滤重复的数据，不过滤诺西厂家
                                    if (userlabelTimeSet.contains(userlabetime) && !dataMap.get(ContentInfo.VENDOR_NAME).equalsIgnoreCase("NOKIA")) {
                                        continue;
                                    } else {
                                        userlabelTimeSet.add(userlabetime);
                                        commonWrite.appenLine(pmNameList, dataMap, nullpmMap);
                                    }
                                } else {
                                    commonWrite.appenLine(pmNameList, dataMap, nullpmMap);
                                }

                            }

                        }
                }
            }
            //将数据输出到excel
            excelUtil.writeXSSFExcel(path);

        } catch (Exception e) {
            //解析异常，解析状态设置为0
            fileComplete.setParsestatus(0);
            Log.error("parse file error :" + file.getAbsolutePath(), e);
        } finally {
            try {
                inputStream.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
        //解析完成，解析状态设置为1
        fileComplete.setParsestatus(1);
        // 文件完整性记录插入数据库
        fileCompleteService.insert(fileComplete);

        //如果是解压后的文件，删除解压的文件
        if (delete)
            file.delete();
    }


    /**
     * 将Dn的指标放入指标集
     *
     * @param dn    dn字符串
     * @param pmset pm指标集
     */
    private void getDnPm(String dn, Set<String> pmset) {
        String[] dnList = dn.split(",");
        for (int i = 0; i < dnList.length; i++) {
            if (!dnList[i].equals("")) {
                String[] pv = dnList[i].split("=");
                if (pv.length == 1) {
                    continue;
                }
                pmset.add(pv[0]);
            }
        }
    }

    /**
     * 将Dn的指标加入数据
     *
     * @param pmNameList 采集指标列表
     * @param dn         dn字符串
     * @param dnMap      数据map
     */
    private void getDnMap(List<String> pmNameList, String dn, Map<String, String> dnMap) {

        String[] dnList = dn.split(",");
        for (int i = 0; i < dnList.length; i++) {
            if (!dnList[i].equals("")) {
                String[] pv = dnList[i].split("=");
                int tempNum = pv.length;
                if (tempNum == 1) {
                    continue;
                } else if (dnMap.containsKey(pv[0])) {
                    if (pmNameList.contains(pv[0])) {
                        dnMap.put(pv[0] + "_0", pv[1]);
                    }
                } else {
                    if (pmNameList.contains(pv[0])) {
                        dnMap.put(pv[0], pv[1]);
                    }
                }
            }
        }
    }

    public Map<String, String> getUserSwMap() {
        return userlabelSwMap;
    }

}
