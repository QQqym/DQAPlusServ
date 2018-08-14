package com.cn.chinamobile.main;

import com.cn.chinamobile.business.CheckDnBusiness;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.Log;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author zhou on 2017/11/11.
 */
public class CheckDnInvoker {

    //参数顺序，类型（NRM/PM）、时间
    public static void main(String[] args) {
        //加载spring配置文件
        IniDomain.initSpringCfg(ContentInfo.ROOT_PATH + "conf/springCommon.xml");
        PropertyConfigurator.configure(ContentInfo.ROOT_PATH + "conf/udplog4j.properties");

        if(args.length != 2){
            Log.info("输入参数个数错误，参数为：类型（NRM/PM）、时间");
            System.exit(0);
        }

        CheckDnBusiness checkDnBusiness = IniDomain.ct.getBean(CheckDnBusiness.class);
        checkDnBusiness.start(args[0],args[1]);
    }

}
