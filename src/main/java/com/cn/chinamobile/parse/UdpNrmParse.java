package com.cn.chinamobile.parse;

import com.cn.chinamobile.entity.UdpCount;
import com.cn.chinamobile.pojo.mybatis.UdpLosedn;
import com.cn.chinamobile.pojo.mybatis.UdpNrm;
import com.cn.chinamobile.util.*;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @author zhou on 2017/10/29.
 */
@Service
public class UdpNrmParse {

     //存储省份信息
    private Map<String,String> provinceMap;

    //存储地市信息
    private Map<String,String> cityMap;

    //存储厂家信息
    private Map<String,String> vendorMap;

    //记录出现过的软件版本号
    private Set<String> existSwVersion = new HashSet<>();

    //记录管理网元的DN和软件版本号
    private Map<String,String> dnSwversionMap = new HashMap<>();

    //记录各子网元指标空和0的情况
    //地市，子网元，软件版本，指标
    private Map<String,Map<String,Map<String,Map<String,UdpCount>>>> resultMap ;


    //检测工具类
    private UdpCheck udpCheck = new UdpCheck();

    //记录子网元出现过的类型,分地市记录
    private Map<String,Set<String>> objectMapSet = new HashMap<>();

    private Map<String,String> levelMap;

    private UdpLogUtil udpLogUtil;

    private List<UdpLosedn> losednList = new ArrayList<>(1000);

    /**
     * @param levelMap 指标树
     * @param udpLogUtil 日志记录工具类
     * @param cityMap 地市信息
     * @param provinceMap 省份信息
     * @param vendorMap 厂家信息
     */
    public void initParas(Map<String,String> levelMap,UdpLogUtil udpLogUtil,Map<String,String> cityMap, Map<String,String> provinceMap,Map<String,String> vendorMap){
        this.levelMap = levelMap;
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
     * @param mngdFiles managedelement 文件
     * @param nrmMap 指标集
     */
    public  String parse(String province,String netype,List<File> udpFiles,List<File> mngdFiles,Map<String,Map<String,UdpNrm>> nrmMap){

        //初始化返回结果
        resultMap = new HashMap<>(2*nrmMap.size());


        //先解析managedelement获取软件版本
        getNRMSwVersion(mngdFiles);

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
                    parseFile(province,netype,defile,true,nrmMap);
                }else {
                    failcount++;
                    failreasion += ","+file.getAbsolutePath();
                }
            }else {
                parseFile(province,netype,file,false,nrmMap);
            }
        }

        if(gzcount>0 && failcount==gzcount)
            return  failreasion;

        return "sucess";
    }

    public void getNRMSwVersion(List<File> mngdFiles){
        for(File file : mngdFiles){
            //gz压缩文件
            if(file.getName().contains(".gz")){
                File defile = new GZIPUtil().decompress(file);
                if(defile != null){
                    getSwVersion(defile, true);
                }
            }else {
                getSwVersion(file, false);
            }
        }
    }


    /**
     * 解析managedelement 获取软件版本
     * @param file 文件
     * @param delete 是否删除
     */
    public void getSwVersion(File file,boolean delete){
        InputStream inputStream = null;

        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            inputStream = new FileInputStream(file.getAbsolutePath());
            XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);
            int event = -1;
            //记录开始标签
            String localname = "";
            //记录是否是managedelement网元
            boolean ismaned = false;

            //记录dn
            String dn="";

            //记录软件版本的指标编号
            int swnum=0;

            while (reader.hasNext()){
                event = reader.next();
                switch (event){
                    //开始标签
                    case XMLStreamConstants.START_ELEMENT:
                        localname = reader.getLocalName();
                       if(localname.equals(ContentInfo.OBJECT_TYPE)){
                            String objecttype = reader.getElementText();
                           if(objecttype.equalsIgnoreCase("ManagedElement")){
                               ismaned = true;
                           }else {
                               ismaned = false;
                           }

                       }else if(localname.equalsIgnoreCase(ContentInfo.N)) {
                           String ivalue = reader.getAttributeValue(0);
                           String nName = reader.getElementText();
                           if(ismaned && nName.equalsIgnoreCase("SwVersion")){
                               swnum = Integer.valueOf(ivalue);
                           }
                       }else if(localname.equalsIgnoreCase(ContentInfo.CM) || localname.equalsIgnoreCase(ContentInfo.OBJECT)) {
                           if(ismaned){
                               int attricount = reader.getAttributeCount();
                               for(int i=0;i<attricount;i++){
                                   if(reader.getAttributeLocalName(i).equalsIgnoreCase("Dn")){
                                       dn = reader.getAttributeValue(i);
                                       break;
                                   }
                               }
                           }

                       }else if(localname.equalsIgnoreCase(ContentInfo.V)){
                           int cvivalue = Integer.parseInt(reader.getAttributeValue(0));
                           if(ismaned && cvivalue==swnum){
                               String swVersion = reader.getElementText();
                               dnSwversionMap.put(dn,swVersion);
                           }
                       }
                }
            }
        }catch (Exception e){
            //解析异常，解析状态设置为0
            Log.error("parse file error :" + file.getAbsolutePath(),e);
        }finally {
            try {
                if(null != inputStream)
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
     * 解析xml文件
     * @param province 省份信息
     * @param netype 网元类型
     * @param file 文件
     * @param delete 解析后是否删除文件
     * @param nrmMap 网元版本的指标集 子网元,nrm名称，实体类（级别，类型等）
     */
    public void parseFile(String province,String netype,File file,boolean delete, Map<String,Map<String,UdpNrm>> nrmMap){
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

            //记录Dn前缀
            String dnPrefix = "";

            //记录地市名称
            String cityname ="";

            //记录对应子网元的指标集
            Map<String,UdpNrm> udpNrmMap = null;
            //记录子网元类型
            String objecttype = "";

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
                        } else if (localname.equals(ContentInfo.OBJECT_TYPE)){
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
                            udpNrmMap = nrmMap.get(objecttype);

                        }else if(localname.equalsIgnoreCase(ContentInfo.FIELDNAME)) {
                            pmNNameMap = new HashMap<Integer, String>();
                        }else if(localname.equalsIgnoreCase(ContentInfo.N)) {
                            String ivalue = reader.getAttributeValue(0);
                            String nName = reader.getElementText();
                            //全部指标解析
                            pmNNameMap.put(Integer.parseInt(ivalue), nName);
                        }else if(localname.equalsIgnoreCase(ContentInfo.CM) || localname.equalsIgnoreCase(ContentInfo.OBJECT)) {
                            dataMap = new CaseInsensitiveMap();
                            int attricount = reader.getAttributeCount();
                            for(int i=0;i<attricount;i++){
                                String value = reader.getAttributeValue(i);
                                String name = reader.getAttributeLocalName(i);
                                dataMap.put(name,value);

                                //判断Dn串是否合法
                                if(name.equalsIgnoreCase("Dn") && netype.equalsIgnoreCase("ENB") ){
                                    boolean flag = checkDn(value);
                                    //如果DN不合法，记录
                                    if(!flag){
                                        udpLogUtil.wrongDn(file.getAbsolutePath(),value);
                                    }
                                }
                            }
                        }else if(localname.equalsIgnoreCase(ContentInfo.V)){
                            int cvivalue = Integer.parseInt(reader.getAttributeValue(0));
                            dataMap.put(pmNNameMap.get(cvivalue),reader.getElementText());
                        }
                    case XMLStreamConstants.END_ELEMENT:
                        String endname = reader.getLocalName();
                        //CM指标结束
                        if ((endname.equalsIgnoreCase(ContentInfo.CM) || endname.equalsIgnoreCase(ContentInfo.OBJECT) )&& !endname.equals(localname)) {
                            //检测一个CM的指标集
                            checkPms(file.getAbsolutePath(),cityname, objecttype, dataMap, udpNrmMap);
                        }

                }
            }
        }catch (Exception e){
            //解析异常，解析状态设置为0
            e.printStackTrace();
            Log.error("parse file error :" + file.getAbsolutePath(),e);
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
     * 检测一个CM的指标集
     * @param filename 文件名
     * @param objtype 子网元类型
     * @param dataMap 一个CM的指标集
     * @param udpNrmMap 对应网元的指标集和描述实体类
     */
    private void checkPms(String filename,String cityname,String objtype, Map<String,String> dataMap, Map<String,UdpNrm> udpNrmMap ){
        //处理未获取到指标集的问题
        if(udpNrmMap==null || udpNrmMap.size()==0){
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

        Map<String,Map<String,UdpCount>> swNrmMap = null;
        //通过子网元从resultMap获取记录的指标集
        if(cityNrmMap.containsKey(objtype)){
            swNrmMap = cityNrmMap.get(objtype);
        }else {
            swNrmMap = new HashMap<>(10);
            cityNrmMap.put(objtype,swNrmMap);
        }
        //获取dn的软件版本号
        String swVersion = udpCheck.getSwversion(dataMap.get("Dn"),dnSwversionMap);

        //将出现过的软件版本号存储
        if(!existSwVersion.contains(swVersion)){
            existSwVersion.add(swVersion);
        }

        //如果没有查询到dn，记录
        if(swVersion.equals("")){
            UdpLosedn udpLosedn = new UdpLosedn(null,objtype,filename,dataMap.get("Dn"));
            losednList.add(udpLosedn);
        }

        //通过软件版本号获取对应版本的指标集记录
        Map<String,UdpCount> nrmMap = null;
        if(swNrmMap.containsKey(swVersion)){
            nrmMap = swNrmMap.get(swVersion);
        }else {
            nrmMap = new HashMap<>();
            swNrmMap.put(swVersion,nrmMap);
        }

        //通过指标集循环判断该CM的私有指标
        for(String key: udpNrmMap.keySet()){
            UdpNrm udpNrm = udpNrmMap.get(key);
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
            }//数据类型不符合要求，类型不符合加1,不为空且类型不符合
            else if( udpCheck.isZero(value)){
                udpCount.addZeronum();
            }
            else if(!udpCheck.isEmpty(value) && !udpCheck.isType(value,udpNrm.getDatatype(),udpNrm.getNrmDefinition())){
                udpCount.addNtypenum();
            }
            //总数加1
            udpCount.addTotalnum();
        }

    }

    /**
     * 返回获取到的DN和版本信息
     * @return
     */
    public Map<String,String> getDnSwversionMap(){
        return dnSwversionMap;
    }

    /**
     * 获取出现过的类型,分地市记录
     * @return
     */
    public Map<String,Set<String>> getObjectMapSet(){
        return objectMapSet;
    }

    public Set<String> getExistSwVersion(){
        return existSwVersion;
    }

    /**
     * 判断Dn结构树是否合法
     * @param dn
     */
    public boolean checkDn(String dn){
        boolean flag = true;
        String[] dnList = dn.split(",");
        int j=0;
        String[] dnDataNames= new String[dnList.length-1];
        for (int i = 0; i < dnList.length; i++) {
            if (!dnList[i].equals("")) {
                //核心网的Dn前缀含有=，跳过第一个
                if(i==0)
                    continue;

                String[] pv = dnList[i].split("=");
                if (pv.length == 1) {
                    continue;
                }
               dnDataNames[j++] = pv[0];
            }
        }

        for(int t=dnDataNames.length-1;t>0;t--){
            if(levelMap.containsKey(dnDataNames[t])){
                String parentvalue = levelMap.get(dnDataNames[t]);
                if(!parentvalue.equals(dnDataNames[t-1])){
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }

    public List<UdpLosedn> getLosednList(){
        return losednList;
    }

    public Map<String,Map<String,Map<String,Map<String,UdpCount>>>> getResultMap(){
        return  resultMap;
    }

}
