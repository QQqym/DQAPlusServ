package com.cn.chinamobile.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * @author zhou on 2017/11/7.
 */
public class UdpLogUtil {

    BufferedWriter bufferedWriter = null;

    public void initBuffer(String filename){
        File writeFile = new File(filename);
        //如果父路径不存在，创建
        if(!writeFile.getParentFile().exists())
            writeFile.getParentFile().mkdirs();
        //如果已经解析过删除文件
        if(writeFile.exists())
            writeFile.delete();

        try{
            bufferedWriter =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writeFile),ContentInfo.ENCODING));
        }catch (Exception e){
            Log.error("init udp file error:"+filename,e);
        }

    }

    /**
     * 记录地市缺少的数据类型
     * @param city 省份-地市
     * @param datatype 子网元类型
     */
    public void takeLoseDatatype(String city,String datatype){
        String out="该批数据"+city+"缺少子网元类型：" + datatype;
        write(out);
    }

    /**
     * 记录DN不符合规则
     * @param filename 文件名
     * @param dn DN串
     */
    public void wrongDn(String filename,String dn){
        String out = filename+" 文件中的Dn:"+dn+" 不符合规则";
        write(out);
    }

    /**
     * 记录规范中缺少网元版本
     * @param type 类型 PM/NRM
     * @param neType 网元类型
     * @param version  版本号
     */
    public void loseNetypeVersion(String type,String neType,String version){
        String out = "netruler规范 "+type+" 中缺少网元类型："+neType+"，版本号："+version+" 的规范";
        write(out);
    }

    /**
     * 写一行记录
     * @param out 记录内容
     */
    public void write(String out){
        try{
            bufferedWriter.write(out);
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
