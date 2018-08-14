package com.cn.chinamobile.main;

import com.cn.chinamobile.business.NeVerBusiness;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.Log;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author xueweixia
 */
public class NeVerInvoke {

    public static void main(String[] args) {
        //加载spring配置文件
        IniDomain.initSpringCfg(ContentInfo.ROOT_PATH + "conf/springCommon.xml");
        PropertyConfigurator.configure(ContentInfo.ROOT_PATH + "conf/udplog4j.properties");
        if(args.length==0){
            Log.info("请输入参数："+"UDP or NBI or PCT");
            System.exit(0);
        }
        NeVerBusiness neVerBusiness = IniDomain.ct.getBean(NeVerBusiness.class);
        neVerBusiness.start(args[0],args[1]);
    }
}
