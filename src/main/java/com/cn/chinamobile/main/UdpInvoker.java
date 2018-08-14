package com.cn.chinamobile.main;

import com.cn.chinamobile.business.UdpBusiness;
import com.cn.chinamobile.pojo.mybatis.UdpTask;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.util.*;
import org.apache.log4j.PropertyConfigurator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhou on 2017/10/29.
 * UDP功能的入口
 */
public class UdpInvoker {
    static FilePathGenerate filePathGenerate = new FilePathGenerate();
    static TimeUtil timeUtil = new TimeUtil();

    //参数顺序，省份、网元类型、厂家、类型（NRM/PM）、版本、时间
    public static void main(String[] args) {
        //加载spring配置文件
        IniDomain.initSpringCfg(ContentInfo.ROOT_PATH + "conf/springCommon.xml");
        IniDomain.initUdpConfig();
        PropertyConfigurator.configure(ContentInfo.ROOT_PATH + "conf/udpsinglelog4j.properties");

        if(args.length != 7){
            Log.info("输入参数个数错误，参数为：省份、网元类型、厂家、类型（NRM/PM）、版本、时间、文件类型（NBI/UDP）");
            System.exit(0);
        }

//        String datetime = timeUtil.getDestTime(args[5]);
        String datetime = args[5];
        String filetype = args[6];

        //根据参数，生产UdpTask对象
        UdpTask udpTask = new UdpTask(datetime,args);

        UdpBusiness udpBusiness = IniDomain.ct.getBean(UdpBusiness.class);
        List<String> udppaths = new ArrayList<>();
        String[] times = datetime.split(",");
        //如果时间点是一个，单次任务
        if(times.length==1){
            //获取Udp路径
            String udpPath = filePathGenerate.getUdpPath(filetype,datetime,args);
            udppaths.add(udpPath);
        }else{//如果时间点是多个，多个任务合并
            //将符合条件的路径全部加载到udppaths
            FileUtil fileUtil = new FileUtil();
            String udproot = filePathGenerate.getUdpRootPath(filetype,args[0], args[1], args[2], args[3]);
            for (String time : times) {
                List<String> dates = fileUtil.findUdpDatePath(udproot, time);
                for (String date : dates) {
                    String udpPath = filePathGenerate.getUdpPath(filetype,date, args[0], args[1], args[2], args[3]);
                    udppaths.add(udpPath);
                }
            }
        }

        udpBusiness.setParas(udppaths,udpTask);
        udpBusiness.startBusiness();

    }
}
