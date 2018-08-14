package com.cn.chinamobile.parse;

import com.cn.chinamobile.util.ContentInfo;
import org.apache.commons.collections.map.CaseInsensitiveMap;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zh on 2017/5/3.
 */
public class ParseCsv {
    public void parseFile(File file, Map<String, List<String>> qcimap, String netype){
        //如果文件包含网元
        if(file.getName().toLowerCase().contains(netype.toLowerCase())){
            //记录需要的指标集
            List<String> pmNameList = qcimap.get(netype);
            BufferedReader br = null;
            String line = "";
            //记录行号
            int linecount = 1;
            String pmnames[] = null;
            //记录指标个数
            int pmcount = 0;
            //记录公共字段
            Map<String,String> commonMap = new CaseInsensitiveMap();
            try{
               br = new BufferedReader(new InputStreamReader(new FileInputStream(file), ContentInfo.ENCODING));
                while ((line=br.readLine()) !=null){
                    if(linecount == 1){ //第一行公共字段
                        String commonvalues[] = line.split("\\|",-1);
                        for (String commonvalue : commonvalues)
                            commonMap.put(commonvalue.split("=")[0],commonvalue.split("=")[1]);
                        linecount++;
                    }else if(linecount == 2){ //第二行指标名称
                        pmnames = line.split("\\|",-1);
                        pmcount = pmnames.length;
                        linecount++;
                    }else if(linecount >= 3){ //第三行开始数据字段
                        String values[] = line.split("\\|",-1);
                        Map<String,String> dataMap = new CaseInsensitiveMap();
                        //放入公共字段
                        dataMap.putAll(commonMap);
                        //放入指标字段
                        for (int i=0;i< pmcount;i++){
                            dataMap.put(pmnames[i],values[i]);
                        }

                        //输出
                        StringBuffer outvalues = new StringBuffer();
                        for(String pmname : pmNameList){
                            outvalues.append(dataMap.get(pmname)).append(",");
                        }
                        String value = new String(outvalues);
                        value = value.substring(0,value.length()-1);
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
