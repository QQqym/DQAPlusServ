package com.cn.chinamobile.main;

import com.cn.chinamobile.cron.CronTriggerInvoke;
import com.cn.chinamobile.resource.IniDomain;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class T {
    public static void main(String[] args) {

//        String[] arg = {"SD","ENB","DT","V2.6.0","EutranCellTdd","2017-11-24 03:00:00","2017-11-24 03:00:00"};
//        String[] arg = {"SD","MME","ER","V2.0.2","MmeFunction","2017-08-28 00:00:00","2017-08-28 00:00:00"};
//        String[] arg = {"SD","SGW","HW","V2.0.0","EpRpDynS11Sgw","2017-10-23 00:00:00","2017-10-23 00:00:00"};
//        String[] arg = {"TJ","VOLTESBC","HW","V1.1.1","SbcFunction","2017-08-15 05:00:00","2017-08-15 05:00:00"};
//        String[] arg = {"TJ","PGW","HW","V2.0.0","PgwFunction","2017-08-24 00:00:00","2017-08-24 00:00:00"};
//        String[] arg = {"SD","PGW","HW","V2.0.0","PgwFunction","2017-08-08 12:00:00","2017-08-08 12:00:00"};
//        String[] arg = {"TJ","SCSCF","HW","V3.0.3","ScscfFunction","2017-08-24 00:00:00","2017-08-24 00:00:00"};
//        String[] arg = {"TJ","TAS","HW","V1.0.4","SccAsFunction","2017-08-15 04:00:00","2017-08-15 04:00:00"};
//        String[] arg = {"AH","ENB","HW","V2.8.1","EthernetPort","2018-03-30 12:00:00","2018-03-30 12:00:00","no"};
//        String[] arg = {"HA","MME","NS","V2.0.2","EpRpDynS11Mme","2018-02-27 03:00:00","2018-02-27 03:00:00"};


//        String[] arg = {"YN", "ENB", "DT", "V2.6.0", "EutranCellTdd", "2018-05-22 15:00:00", "2018-05-22 15:00:00", "yes"};
////        //String[] arg = {"YN", "MME", "NS", "V2.0.2", "EpRpDynS11Mme", "2018-05-22 03:00:00", "2018-05-22 21:00:00", "yes"};
//        TaskInvoker.main(arg);


//        String[] arg = {"SD","ENB","HW","NRM","V2.8.1","2017-10-20-23"};
//        String[] arg = {"SH","ENB","HW","PM","V2.8.1","2017-10-25-09"};

//        String[] arg = {"AH","PSBC","HW","PM","V1.1.8","2018-04-11,2018-04-12,2018-04-13","IMSUDP"};
//        UdpInvoker.main(arg);

//        String[] arg = {"merge","NRM","2017-10-26,2017-10-27,2017-10-28","NBI"};
//        UdpAllInvoker.main(arg);
//        String[] arg = {"SD","MME","HW","NRM","V2.0.0","2017-10-22-23"};
//        CheckDnInvoker.main(arg);

//        System.out.println("EpRpDynS11Sgw".toLowerCase());

//        String[] arg1 = {"IMSUDP","2018-04-11,2018-04-12,2018-04-13"};
//        NeVerInvoke.main(arg1);


//        String[] arg2 = {"2018-05-22 15:00:00"};
//        SwVersionInvoke.main(arg2);
//
//        String[] arg2 = {"2018-07-17 03:00:00","2018-07-17 03:00:00"};
//        AllTaskInvoke.main(arg2);



//        System.out.print("2018-03-18-03".replaceAll("-",""));
    }

}