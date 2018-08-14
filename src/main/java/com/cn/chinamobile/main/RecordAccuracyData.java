package com.cn.chinamobile.main;

import com.cn.chinamobile.pojo.mybatis.ScheduleTask;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.service.RecordAccuracyRunnable;
import com.cn.chinamobile.service.ScheduleTaskService;
import com.cn.chinamobile.util.ContentInfo;
import org.apache.log4j.PropertyConfigurator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Auther: qiuyuming
 * @Date: 2018/7/11 16:35
 * @Description:执行生成准确性Excel
 */
public class RecordAccuracyData {

    /**
     *
     * 功能描述:
     *
     * @param: 运行主函数
     * @return:
     * @auther: qiuyuming
     * @date: 2018/7/12 9:16
     */
    public static void main(String[] args) {
        //加载spring配置文件
        IniDomain.initSpringCfg(ContentInfo.ROOT_PATH +"conf/springCommon.xml");
        //加载资源文件
        IniDomain.initResource();
        //设置log4j
        PropertyConfigurator.configure(ContentInfo.ROOT_PATH + "conf/log4j.properties");
        String datetime="2018-05-22";
        //String datetime=args[0];
        //获取成功录入数据的省份，注意非空数据的判断
        ScheduleTaskService scheduleTaskService=IniDomain.ct.getBean(ScheduleTaskService.class);
        List<ScheduleTask>  scheduleTasks=scheduleTaskService.selectByDateAndStatus(datetime);
        //对数据进行省份划分,一个省份下按厂家划分现在只有enb
        Map<String,Map<String,List<ScheduleTask>>> map=new HashMap<String,Map<String,List<ScheduleTask>>>();
        for(ScheduleTask st:scheduleTasks){
            if (map.containsKey(st.getProvince())) {
                Map<String, List<ScheduleTask>> map0 = map.get(st.getProvince());
                List<ScheduleTask> list0 = map0.get(st.getVendor());
                list0.add(st);
            }else{
               List<ScheduleTask> list0=new ArrayList<ScheduleTask>();
               list0.add(st);
               Map<String,List<ScheduleTask>> map0=new HashMap<String,List<ScheduleTask>>();
               map0.put(st.getVendor(),list0);
               map.put(st.getProvince(),map0);
           }
        }
        //利用线程池 处理不同网元的数据
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

        for(Map.Entry<String,Map<String,List<ScheduleTask>>> entry:map.entrySet()){
            String province=entry.getKey();
            Map<String,List<ScheduleTask>> map1=entry.getValue();
           for(Map.Entry<String,List<ScheduleTask>> entry1:map1.entrySet()){
               String vendor=entry1.getKey();
               List<ScheduleTask> list=entry1.getValue();
               //加入线程
               RecordAccuracyRunnable rar=IniDomain.ct.getBean(RecordAccuracyRunnable.class);
               rar.setPara(list,province,vendor);
               cachedThreadPool.execute(rar);
           }
        }
        cachedThreadPool.shutdown();
    }
}
