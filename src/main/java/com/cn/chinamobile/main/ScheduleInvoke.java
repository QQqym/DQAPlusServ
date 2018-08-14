package com.cn.chinamobile.main;

import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.util.ContentInfo;
import org.apache.log4j.PropertyConfigurator;

/**
 * Created by zh on 2017/7/11.
 */
public class ScheduleInvoke {
    public static void main(String[] args) {
        //加载spring配置文件
        IniDomain.initSpringCfg(ContentInfo.ROOT_PATH +"conf/springSchedule.xml");

        //加载资源文件
        IniDomain.initResource();

        //设置log4j
        PropertyConfigurator.configure(ContentInfo.ROOT_PATH + "conf/log4j.properties");
    }
}
