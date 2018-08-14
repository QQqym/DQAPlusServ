package com.cn.chinamobile.util;

import java.io.*;
import java.util.Properties;

/**
 * Created by zh on 2017/4/25.
 */
public class ConfigUtils {
    public static Properties getConf(String path){
        Properties prop = new Properties();
        BufferedReader bufferedReader = null;
        try{
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)),"GB2312"));
            prop.load(bufferedReader);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                bufferedReader.close();
            }catch (Exception e1){

            }
        }
        return prop;
    }
}
