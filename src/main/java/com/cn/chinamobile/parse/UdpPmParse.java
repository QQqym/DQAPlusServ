package com.cn.chinamobile.parse;

import com.cn.chinamobile.entity.UdpCount;
import com.cn.chinamobile.pojo.mybatis.UdpLosedn;
import com.cn.chinamobile.pojo.mybatis.UdpPm;
import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.GZIPUtil;
import com.cn.chinamobile.util.UdpCheck;
import com.cn.chinamobile.util.UdpLogUtil;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @author zhou on 2017/11/1.
 */
@Service
public class UdpPmParse {


    //记录管理网元的DN和软件版本号
    private Map<String,String> dnSwversionMap = new HashMap<>();

    //记录各子网元指标空和0的情况
    //地市，子网元，软件版本，指标
    private Map<String,Map<String,Map<String,Map<String,UdpCount>>>> resultMap ;


    //检测工具类
    private UdpCheck udpCheck = new UdpCheck();

    //记录子网元出现过的类型,分地市记录
    private Map<String,Set<String>> objectMapSet = new HashMap<>();

    //记录出现过的软件版本号
    private Set<String> existSwVersion = new HashSet<>();
    //存储省份信息
    private Map<String,String> provinceMap;

    //存储地市信息
    private Map<String,String> cityMap;

    //存储厂家信息
    private Map<String,String> vendorMap;

    private UdpLogUtil udpLogUtil;

    private List<UdpLosedn> losednList = new ArrayList<>(1000);

    /**
     * @param udpLogUtil 日志记录工具类
     * @param cityMap 地市信息
     * @param provinceMap 省份信息
     * @param vendorMap 厂家信息
     */
    public void initParas(UdpLogUtil udpLogUtil,Map<String,String> cityMap, Map<String,String> provinceMap,Map<String,String> vendorMap){
        this.udpLogUtil = udpLogUtil;
        this.cityMap = cityMap;
        this.provinceMap = provinceMap;
        this.vendorMap = vendorMap;
    }

    /**
     * 解析资源文件
     * @param province 省份信息
     * @param netype 网元类型
     * @param udpFiles udp所有文件
     * @param dnSwversionMap 从已存的库中查询的Dn,软件版本号
     * @param pmMap 指标集
     * @param udpLogUtil 日志记录工具类
     */
    public String parse(String province,String netype,List<File> udpFiles,Map<String,String> dnSwversionMap,Map<String,Map<String,UdpPm>> pmMap,UdpLogUtil udpLogUtil){
        //初始化返回结果
        resultMap = new HashMap<>(2*pmMap.size());

        //给dnmap赋值
        this.dnSwversionMap = dnSwversionMap;

        int gzcount=0;
        int failcount =0;
        String failreasion="文件解压失败";

        //解析全部UDP文件
        for(File file : udpFiles){
            //gz压缩文件
            if(file.getName().contains(".gz")){
                gzcount++;
                File defile = new GZIPUtil().decompress(file);
                if(defile != null){
                    parseFile(province,netype,defile,true,pmMap);
                }else {
                    failcount++;
                    failreasion += ","+file.getAbsolutePath();
                }
            }else {
                parseFile(province,netype,file,false,pmMap);
            }
        }

        if(gzcount>0 && failcount==gzcount)
            return  failreasion;

        return "sucess";
    }

    /**
     * 解析xml文件
     * @param province 省份信息
     * @param netype 网元类型
     * @param file 文件
     * @param delete 解析后是否删除文件
     * @param pmMap 网元版本的指标集 子网元,pm名称，实体类（级别，类型等）
     */
    public void parseFile(String province,String netype,File file,boolean delete, Map<String,Map<String,UdpPm>> pmMap){
        InputStream inputStream = null;
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            inputStream = new FileInputStream(file.getAbsolutePath());
            XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);
            Map<String,String> dataMap = null;
            int event = -1;
            //记录全部指标集,key是指标的序号，value是指标名
            Map<Integer,String> pmNNameMap = null;

            //记录开始标签
            String localname = "";

            //记录对应子网元的指标集
            Map<String,UdpPm> udpPmMap = null;
            //记录子网元类型
            String objecttype = "";

            //记录Dn前缀
            String dnPrefix = "";

            String sn = "";

            //记录地市名称
            String cityname ="";

            while (reader.hasNext()){
                event = reader.next();
                switch (event){
                    //开始标签
                    case XMLStreamConstants.START_ELEMENT:
                        localname = reader.getLocalName();
                        if(localname.equals(ContentInfo.DN_PREFIX)){
                            dnPrefix =  reader.getElementText();
                            cityname = udpCheck.getCityname(province,dnPrefix,cityMap,provinceMap);

                            boolean flag = udpCheck.checkDnprefix(dnPrefix, cityMap, vendorMap, provinceMap);
                            //Dn前缀不合法
                            if(!flag){
                                udpLogUtil.wrongDn(file.getAbsolutePath(),dnPrefix);
                            }
                        }else if(localname.equals(ContentInfo.OBJECT_TYPE)){
                            objecttype = reader.getElementText();

                            //没记录过添加到网元set记录
                            Set<String> objectSet = new HashSet<>(30);
                            if(objectMapSet.containsKey(cityname)){
                                objectSet = objectMapSet.get(cityname);
                            }else {
                                objectMapSet.put(cityname,objectSet);
                            }
                            if(!objectSet.contains(objecttype)){
                                objectSet.add(objecttype);
                            }

                            //获取对应子网元的指标集
                            udpPmMap = pmMap.get(objecttype);

                        }else if(localname.equalsIgnoreCase(ContentInfo.PMNAME)) {
                            pmNNameMap = new HashMap<Integer, String>();
                        }else if(localname.equalsIgnoreCase(ContentInfo.N)) {
                            String ivalue = reader.getAttributeValue(0);
                            String nName = reader.getElementText();
                            //全部指标解析
                            pmNNameMap.put(Integer.parseInt(ivalue), nName);
                        }else if(localname.equalsIgnoreCase(ContentInfo.PM) || localname.equalsIgnoreCase(ContentInfo.OBJECT)) {
                            dataMap = new CaseInsensitiveMap();
                            int attricount = reader.getAttributeCount();
                            for(int i=0;i<attricount;i++){
                                dataMap.put(reader.getAttributeLocalName(i),reader.getAttributeValue(i));
                            }
                        }else if(localname.equalsIgnoreCase(ContentInfo.V)){
                            int cvivalue = Integer.parseInt(reader.getAttributeValue(0));
                            dataMap.put(pmNNameMap.get(cvivalue),reader.getElementText());
                        }else if(localname.equalsIgnoreCase(ContentInfo.CV)){
                            int cvivalue = Integer.parseInt(reader.getAttributeValue(0));
                            dataMap.put(pmNNameMap.get(cvivalue),"CV");
                        }else if(localname.equalsIgnoreCase(ContentInfo.SN)){
                            sn = reader.getElementText();
                        }else if(localname.equalsIgnoreCase(ContentInfo.SV)){
                            dataMap.put(sn,reader.getElementText());
                        }
                    case XMLStreamConstants.END_ELEMENT:
                        String endname = reader.getLocalName();
                        //PM指标结束
                        if((endname.equalsIgnoreCase(ContentInfo.PM)|| endname.equalsIgnoreCase(ContentInfo.OBJECT)) && !endname.equals(localname)){
                            //检测一个PM的指标集
                            checkPms(file.getAbsolutePath(),cityname,netype,objecttype,dataMap,udpPmMap);
                        }
                }
            }
        }catch (Exception e){
            //解析异常，解析状态设置为0
            e.printStackTrace();
            System.out.println("parse file error :" + file.getAbsolutePath());
        }finally {
            try {
                inputStream.close();
            }catch (Exception e1){
                e1.printStackTrace();
            }

        }
        //如果是解压后的文件，删除解压的文件
        if(delete)
            file.delete();
    }

    /**
     * 检测一个PM的指标集
     * @param filename 文件名称
     * @param netype 网元类型
     * @param objtype 子网元类型
     * @param dataMap 一个PM的指标集
     * @param udpPmMap 对应网元的指标集和描述实体类
     * @param cityname 地市名称
     */
    private void checkPms(String filename,String cityname,String netype,String objtype, Map<String,String> dataMap, Map<String,UdpPm> udpPmMap ){
        //处理未获取到指标集的问题
        if(udpPmMap==null || udpPmMap.size()==0){
            return;
        }

        Map<String, Map<String,Map<String,UdpCount>>> cityNrmMap = null;
        //通过子网元从resultMap获取记录的指标集
        if(resultMap.containsKey(cityname)){
            cityNrmMap = resultMap.get(cityname);
        }else {
            cityNrmMap = new HashMap<>(10);
            resultMap.put(cityname,cityNrmMap);
        }

        Map<String,Map<String,UdpCount>> swPmMap = null;
        //通过子网元从resultMap获取记录的指标集
        if(cityNrmMap.containsKey(objtype)){
            swPmMap = cityNrmMap.get(objtype);
        }else {
            swPmMap = new HashMap<>(10);
            cityNrmMap.put(objtype,swPmMap);
        }
        //获取dn的软件版本号
        String swVersion = udpCheck.getSwversion(dataMap.get("Dn"),dnSwversionMap);

        //如果没有查询到dn，记录
        if(swVersion.equals("")){
            UdpLosedn udpLosedn = new UdpLosedn(null,objtype,filename,dataMap.get("Dn"));
            losednList.add(udpLosedn);
        }

        //将出现过的软件版本号存储
        if(!existSwVersion.contains(swVersion)){
            existSwVersion.add(swVersion);
        }

        //通过软件版本号获取对应版本的指标集记录
        Map<String,UdpCount> nrmMap = null;
        if(swPmMap.containsKey(swVersion)){
            nrmMap = swPmMap.get(swVersion);
        }else {
            nrmMap = new HashMap<>();
            swPmMap.put(swVersion,nrmMap);
        }

        //通过指标集循环判断该PM的私有指标
        for(String key: udpPmMap.keySet()){
            UdpPm udpPm = udpPmMap.get(key);
            //判断已存的指标集是否存过该指标，存过取出，没存过new一个存入
            UdpCount udpCount=new UdpCount();
            //指标中有空格，指标名做trim处理
            String trimkey = key.trim();
            if(nrmMap.containsKey(trimkey)){
                udpCount = nrmMap.get(trimkey);
            }else {
                nrmMap.put(trimkey,udpCount);
            }
            String value = dataMap.get(trimkey);
            //判是否存在,不存在的不存在个数+1
            if(!dataMap.containsKey(trimkey)){
                udpCount.addNexistnum();
            }//如果为空，为空的加1
            else if(udpCheck.isEmpty(value)){
                udpCount.addNullnum();
            }//核心网的判断0值，若为0，为0的格式加1
//            else if(!netype.equalsIgnoreCase("ENB") && udpCheck.isZero(value)){
            else if( udpCheck.isZero(value)){
                udpCount.addZeronum();
            }//数据类型不符合要求，类型不符合加1
            else if(!udpCheck.isType(value,udpPm.getDatatype(),udpPm.getPmDefinition())){
                udpCount.addNtypenum();
            }
            //总数加1
            udpCount.addTotalnum();
        }

    }

    /**
     * 获取缺失的子网元类型
     * @return
     */
    public Map<String,Set<String>> getObjectMapSet(){
        return objectMapSet;
    }

    public Set<String> getExistSwVersion(){
        return existSwVersion;
    }

    public List<UdpLosedn> getLosednList(){
        return losednList;
    }

    public Map<String,Map<String,Map<String,Map<String,UdpCount>>>> getResultMap(){
        return  resultMap;
    }
}
