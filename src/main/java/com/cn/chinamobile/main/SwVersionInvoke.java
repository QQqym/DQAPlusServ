package com.cn.chinamobile.main;

import com.cn.chinamobile.business.NeVerBusiness;
import com.cn.chinamobile.business.SwVersionBusiness;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.Log;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author xueweixia
 */
public class SwVersionInvoke {
    public static void main(String[] args) {
        //加载spring配置文件
        IniDomain.initSpringCfg(ContentInfo.ROOT_PATH + "conf/springCommon.xml");
        PropertyConfigurator.configure(ContentInfo.ROOT_PATH + "conf/udplog4j.properties");
        if(args.length==0){
            Log.info("请输入要获取的北向文件时间");
            System.exit(0);
        }
        SwVersionBusiness swVersionBusiness = IniDomain.ct.getBean(SwVersionBusiness.class);
        swVersionBusiness.start(args[0]);
    }
}
