package com.cn.chinamobile.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * Created by xueweixia
 * 压缩解压类
 */
public class GZIPUtil {
    public static Logger logger = Logger.getLogger(GZIPUtil.class.getName());
    public static final int BUFFER = 1024;

    public File decompress(File gzfile){
        String entryFilePath = gzfile.getAbsolutePath().replace(".gz","");
        File deFile = new File(entryFilePath);
        OutputStream os = null;
        GZIPInputStream gzinput = null;
        try{
            os = new FileOutputStream(deFile);
            gzinput = new GZIPInputStream(new FileInputStream(gzfile));
            int BUFFER_SIZE = 65534;
            int count = -1;
            byte[] data = new byte[BUFFER_SIZE];
            while ((count = gzinput.read(data, 0, BUFFER_SIZE)) != -1) {
              os.write(data, 0, count);
            }
        }catch (Exception e){
            Log.error("decompress error :" + gzfile.getAbsolutePath(),e);
            deFile = null;
        }finally {
            try{
                if(os != null){
                    os.flush();
                    os.close();
                }
                if(gzinput != null){
                    gzinput.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

       }
        return  deFile;
    }




}
