package com.cn.chinamobile.main;

import com.cn.chinamobile.business.UdpAllBusiness;
import com.cn.chinamobile.business.UdpAllTimeBusiness;
import com.cn.chinamobile.business.UdpMergeBusiness;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.Log;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author zhou on 2017/11/11.
 */
public class UdpAllInvoker {

    //参数顺序，类型（NRM/PM）、时间
    public static void main(String[] args) {
        //加载spring配置文件
        IniDomain.initSpringCfg(ContentInfo.ROOT_PATH + "conf/springCommon.xml");
        IniDomain.initUdpConfig();
        PropertyConfigurator.configure(ContentInfo.ROOT_PATH + "conf/udplog4j.properties");

        //三个参数，类型/时间/文件类型
        if(args.length == 3){
            UdpAllBusiness udpAllBusiness = IniDomain.ct.getBean(UdpAllBusiness.class);
            udpAllBusiness.start(args[2],args[0],args[1]);
        }else if(args.length==2){ //两个参数，类型、文件类型
            UdpAllTimeBusiness udpAllTimeBusiness = IniDomain.ct.getBean(UdpAllTimeBusiness.class);
            udpAllTimeBusiness.start(args[1],args[0]);
        }else if(args.length==4) { //四个参数，merge，类型，时间(多个时间,分割)、文件类型
            UdpMergeBusiness udpMergeBusiness = IniDomain.ct.getBean(UdpMergeBusiness.class);
            udpMergeBusiness.start(args[3],args[1],args[2]);
        }
        else {
            Log.info("输入参数个数错误，参数为：类型（NRM/PM）、时间");
            System.exit(0);
        }


    }

}
