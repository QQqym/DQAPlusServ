package com.cn.chinamobile.util;

import com.cn.chinamobile.main.TaskInvoker;
import org.apache.log4j.Logger;

/**
 * Created by zh on 2017/5/18.
 */
public class Log {
    public static Logger logger = Logger.getLogger(TaskInvoker.class.getName());

    public static void info(String info){
        logger.info(info);
    }

    public static void error(String error,Exception e){
        logger.error(error,e);
    }
}
