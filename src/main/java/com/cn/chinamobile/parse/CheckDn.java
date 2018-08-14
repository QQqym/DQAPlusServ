package com.cn.chinamobile.parse;

import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.Log;
import com.cn.chinamobile.util.UdpCheck;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * @author zhou on 2017/11/21.
 */
public class CheckDn {

    //检测工具类
    private UdpCheck udpCheck = new UdpCheck();

    //存储省份信息
    private Map<String,String> provinceMap;

    //存储地市信息
    private Map<String,String> cityMap;

    //存储厂家信息
    private Map<String,String> vendorMap;

    public CheckDn(){

    }

    public CheckDn(Map<String,String> cityMap, Map<String,String> provinceMap,Map<String,String> vendorMap){
        this.cityMap = cityMap;
        this.provinceMap = provinceMap;
        this.vendorMap = vendorMap;
    }

    /**
     * 解析xml文件
     * @param file 文件
     * @param delete 解析后是否删除文件
     */
    public void parseFile(File file,boolean delete,String type,Vector<String> datatypes){
        InputStream inputStream = null;
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            inputStream = new FileInputStream(file.getAbsolutePath());
            XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);
            int event = -1;
            //记录开始标签
            String localname = "";

            //记录Dn前缀
            String dnPrefix = "";

            parse:
            while (reader.hasNext()){
                event = reader.next();
                switch (event){
                    //开始标签
                    case XMLStreamConstants.START_ELEMENT:
                        localname = reader.getLocalName();
                        //检测Dn
                        if(localname.equals(ContentInfo.DN_PREFIX) && type.equalsIgnoreCase("dn")){
                            dnPrefix =  reader.getElementText();
                            //Dn前缀的最后一个是地市简称，存储省份-地市简称
                            boolean flag = udpCheck.checkDnprefix(dnPrefix, cityMap, vendorMap, provinceMap);
                            //Dn前缀不合法
                            if(!flag){
                                Log.info(file.getAbsolutePath()+"检测Dn失败：" + dnPrefix);
                            }
                            break parse;
                        }
                        //检测网元类型
                        else if(localname.equals(ContentInfo.OBJECT_TYPE)&& !type.equalsIgnoreCase("dn")) {
                            String netype =  reader.getElementText();
                            if(!datatypes.contains(netype)){
                                datatypes.add(netype);
                            }
                        }
                }
            }
        }catch (Exception e){
            Log.error("parse file error :" + file.getAbsolutePath(), e);
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
}
