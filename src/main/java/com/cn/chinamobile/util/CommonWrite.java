package com.cn.chinamobile.util;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Created by zh on 2017/5/9.
 */
public class CommonWrite {

    BufferedWriter bufferedWriter = null;
    File wfile = null;

    //构造函数，传入要写入数据的文件名
    public CommonWrite(File file){
        this.wfile = file;
    }

    /**
     * 写入表头
     * @param columns 字段顺序
     */
    public void initFile(List<String> columns){
        try {
            bufferedWriter =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(wfile),ContentInfo.ENCODING));
            StringBuffer values = new StringBuffer();
            for(String pmname : columns){
                values.append(pmname).append("|");
            }
            values.deleteCharAt(values.length()-1);
            bufferedWriter.write(values.toString());
            bufferedWriter.newLine();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 输出数据
     * @param pmNameList
     * @param dataMap
     */
    public void appenLine(List<String> pmNameList,Map<String, String> dataMap,Map<String,Integer> nullPmmap){
        try{
            StringBuffer values = new StringBuffer();
            for(String pmname : pmNameList){
                String value = dataMap.get(pmname);
                //如果值是null或者空，空值记录
                if(value == null || value.trim().equals("")){
                    //包含值加1
                    if(nullPmmap.containsKey(pmname)){
                        nullPmmap.put(pmname,nullPmmap.get(pmname)+1);
                    }else{//不包含放入1
                        nullPmmap.put(pmname,1);
                    }
                }
                values.append(value).append("|");
            }
            values.deleteCharAt(values.length()-1);
            bufferedWriter.write(values.toString());
            bufferedWriter.newLine();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void closeFile(){
        try{
            bufferedWriter.flush();
            bufferedWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
