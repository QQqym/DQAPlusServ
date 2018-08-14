package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.Dao;
import com.cn.chinamobile.pojo.mybatis.*;
import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.ExcelUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: qiuyuming
 * @Date: 2018/7/12 09:57
 * @Description: 任务调度
 */
@Service
public class RecordAccuracyRunnable implements Runnable {

    //记录网元信息
    private List<ScheduleTask> list;
    //记录省市信息
    private String province;
    //记录网元信息
    private String vendor;
    //记录中英文集合
    private Map<String, String> map;
    //拉数据
    @Resource
    private RecordAccuracyService_enb recordAccuracyService_enb;
    @Resource
    private ProvinceService provinceService;
    @Resource
    private Dao dao;
    //设置参数
    public void setPara(List<ScheduleTask> list, String province, String vendor) {
        this.list = list;
        this.province = province;
        this.vendor = vendor;
    }

    @Override
    public void run() {
        //不同版本存入两个不同的sheet之后在区分
        try {
            //获取中英文对照表
            map = provinceService.selectAll();
            //建立TD-LTE记录表格
            ExcelUtil excelUtil0 = new ExcelUtil();
            //文件的输入输出路径先写死
            String path0 = ContentInfo.FILE_TEMP_PATH + "\\" + province + "_RECORD\\" + vendor + "\\" + province + "_" + vendor + "_TD-LTE.xlsx";
            //sheetname
            String sheetName0 = province + "_TD-LTE";
            //产生XSSFWorkbook 列，第三列日期,读配置文件或者传入
            ArrayList<String> times=new ArrayList<String>();
            for(int i=0;i<list.size();i++){
                String time=list.get(i).getDatetime();
                String[] tim=time.split("-");//2018年05月22日 09：00
                String time2=tim[0]+"年"+tim[1]+"月"+tim[2]+"日 "+tim[3]+":00";
                times.add(time2);
            }
            //产生XSSFWorkbook
            excelUtil0.createXSSFWorkbookTDLTE(sheetName0, times);
            //赋值使用
            int numRow = 3;
            int numCell = 6;
            //Map 记录每个时间点的计算值
            Map<String, List<Integer>> map0 = new HashMap<String, List<Integer>>();
            //List 记录不准确的测试项PM样点数量
            List<String> list3=new ArrayList<String>();
            //List 记录总测试的PM样点数量
            List<String> list4=new ArrayList<String>();
            //List 记录单测量项准确率
            List<String> list5=new ArrayList<String>();
            //记录基本信息
            int num=0;
            List<String> list0=new ArrayList<String>();

            //每次循环时间点，记录nbi ,counter 表
            for (int i = 0; i < list.size(); i++) {

                ScheduleTask st = list.get(i);
                //对st时间数据进行处理
                StringBuilder sb = new StringBuilder(st.getDatetime());
                String pm4 = sb.replace(10, 11, " ").toString() + ":00:00";
                //根据province 表将英文简写查找出汉语
                String pm2 = map.get(st.getProvince());
                //取出值存入临时表，再从临时表读取数据出来，私下认为可删除
//              //@@@@@@@@@@@@@@添加开始
                //创建临时表nbi counter temporary_counterpm_1h_enb,temporary_nbipm_1h_enb
                String tableNameCounter="tmp_counterEnb_"+st.getProvince().trim()+"_"+st.getVendor().trim()+i;
                String tableNameNbi="tmp_nbiEnb_"+st.getProvince().trim()+"_"+st.getVendor().trim()+i;
                createCounterTmpTable(pm4,pm2,tableNameCounter);
                createNbiTmpTable(pm4,pm2,tableNameNbi);
                //从临时表读取数据
                EnbNbiPm enp = recordAccuracyService_enb.selectSumNBIByProAndDateFromTmp(pm4, pm2,tableNameNbi);
                EnbCounterPm ecp = recordAccuracyService_enb.selectSumCOUNTERByProAndDateFromTmp(pm4, pm2,tableNameCounter);
                //@@@@@@@@@@@@@@添加结束

                //sum nbi查询
                //EnbNbiPm enp = recordAccuracyService_enb.selectSumNBIByProAndDate(pm4, pm2);
                //sum counter 查询
                //EnbCounterPm ecp = recordAccuracyService_enb.selectSumCOUNTERByProAndDate(pm4, pm2);

                //将数据存入数据库 状态nbi 原始数据为0,counter 原始数据为1
                recordAccuracyService_enb.insertByEnbNbiPm(enp,"0");
                recordAccuracyService_enb.insertByEnbCounterPm(ecp,"1");
                //将两种数据入表
                String[] nbipms = enp.toString().split(",");
                String[] counterpms = ecp.toString().split(",");

                //计算每次的对比值
                List<Integer> list1 = new ArrayList<Integer>();
                //用于存放单个时间点的计算值仅记录一次即可
                List<String>  list2=new ArrayList<String>();
                if(num==0){
                    list0.add(enp.getProvince());
                    list0.add(enp.getCity());
                    list0.add(enp.getVendor());
                    list0.add(enp.getPmversion());
                    list0.add(enp.getElementtype());
                    list0.add(enp.getDateTime());
                    num++;
                }
                list2.addAll(list0);

                for (int j = 0; j < nbipms.length; j++) {
                    excelUtil0.setExcelData(numRow, 0, String.valueOf(j + 1));
                    excelUtil0.setExcelData(numRow, 1, enp.getProvince());
                    excelUtil0.setExcelData(numRow, 2, enp.getVendor());
                    excelUtil0.setExcelData(numRow, 3, enp.getPmversion());

                    if(nbipms[j].equals("null")){
                        excelUtil0.setExcelData(numRow, numCell, "");
                    }else{
                        excelUtil0.setExcelData(numRow, numCell, nbipms[j]);
                    }

                    if(counterpms[j].equals("null")) {
                        excelUtil0.setExcelData(numRow, numCell + times.size(), "");
                    }else{
                        excelUtil0.setExcelData(numRow, numCell + times.size(), counterpms[j]);
                    }
                    if (nbipms[j].equals("null") || counterpms[j].equals("null")) {
                        excelUtil0.setExcelData(numRow, numCell + times.size() + times.size(), "无效样点");
                        list2.add("无效样点");
                        list1.add(-1);
                        numRow += 1;
                        continue;
                    }
                    Double nbiD = Double.parseDouble(nbipms[j]);
                    Double counterD = Double.parseDouble(counterpms[j]);
                    Double numD = 0.000;
                    if (counterD == 0.000) {
                        //计算错误先记为无效样本
                        if ((nbiD - counterD) != 0.0) {
                            excelUtil0.setExcelData(numRow, numCell + times.size() + times.size(), "无效样点");
                            list1.add(-1);
                            list2.add("无效样点");
                            numRow += 1;
                            continue;
                        } else {
                            excelUtil0.setExcelData(numRow, numCell + times.size() + times.size(), "0.00%");
                            list1.add(0);
                            list2.add("0.00%");
                            numRow += 1;
                            continue;
                        }
                    } else {
                        if(counterD-nbiD>0){
                            numD = (counterD-nbiD) / counterD;
                        }else{
                            numD = (nbiD-counterD) / counterD;
                        }
                    }
                    //使计算值保留两位小数
                    DecimalFormat df = new DecimalFormat("######0.0000");
                    String dff = df.format(numD);
                    Double ddf = Double.parseDouble(dff) * 100;
                    if (ddf >= 1.00) {
                        list1.add(1);
                    } else {
                        list1.add(0);
                    }
                    DecimalFormat df2 = new DecimalFormat("######0.00");
                    String ddfs = df2.format(ddf) + "%";
                    excelUtil0.setExcelData(numRow, numCell + times.size() + times.size(), ddfs);
                    list2.add(ddfs);
                    //行向下添加
                    numRow += 1;
                }
                //添加2状态为单个时间点nbi与counter的计算值
                list2.add("2");
                recordAccuracyService_enb.insertByTdLtePm(list2);
                //储存某一个时间点的数据规范
                map0.put(list.get(i).getDatetime(), list1);
                //保证不同时间点存不同列
                numCell += 1;
            }

            list3.addAll(list0);
            list4.addAll(list0);
            list5.addAll(list0);
            //记录插入数据的行
            int row=3;
            //计算样本点,时间点 选取第一个时间点求出有多少计算的行
            for (int i = 0; i < map0.get(list.get(0).getDatetime()).size(); i++) {

                //记录两个值
                int a = 0;
                int b = list.size();
                int createNum=times.size()*3;
                for (Map.Entry entry : map0.entrySet()) {
                    int nowNum = ((ArrayList<Integer>) entry.getValue()).get(i);
                    if (nowNum == 0) {
                        continue;
                    } else if (nowNum == 1) {
                        a += 1;
                    } else {
                        b -= 1;
                    }
                }
                if(b!=0) {
                    Double c = (b - a) / b + 0.00;
                    list3.add(String.valueOf(a));
                    list4.add(String.valueOf(b));
                    list5.add(String.valueOf(c));
                    excelUtil0.setExcelData(row, 6 + createNum,String.valueOf(a));
                    excelUtil0.setExcelData(row, 7 + createNum,String.valueOf(b));
                    excelUtil0.setExcelData(row, 8 + createNum,String.valueOf(c));
                }else{
                    list3.add(String.valueOf(a));
                    list4.add(String.valueOf(b));
                    list5.add("无效样点");
                    excelUtil0.setExcelData(row, 6 + createNum,String.valueOf(a));
                    excelUtil0.setExcelData(row, 7 + createNum,String.valueOf(b));
                    excelUtil0.setExcelData(row, 8 + createNum,"无效样点");
                }
                row+=1;
            }
            //3，4，5 分别表示 不准确的测试项PM样点数量，总测试的PM样点数量，单测量项准确率
            list3.add("3");
            list4.add("4");
            list5.add("5");
            //将三个list存入数据库
            recordAccuracyService_enb.insertByTdLtePm(list3);
            recordAccuracyService_enb.insertByTdLtePm(list4);
            recordAccuracyService_enb.insertByTdLtePm(list5);

            excelUtil0.writeXSSFExcel(path0);
            System.out.println("查询结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * 功能描述: 生成counter临时表
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/8/13 9:32
     */
    public void createCounterTmpTable(String pm4,String pm2,String tabelName){

        String sql="CREATE TEMPORARY TABLE "+tabelName+"  (SELECT * " +
                "FROM counterpm_1h_enb " +
                "WHERE " +
                "pm4 = '"+pm4+"' " +
                "AND  " +
                "pm2='"+pm2+"' " +
                "AND  " +
                "pm12  IN (SELECT DISTINCT pm12 FROM nbipm_1h_enb c WHERE c.pm4 = '"+pm4+"' AND c.pm2='"+pm2+"' " +
                "AND c.pm12 NOT IN ( " +
                "SELECT DISTINCT c1.pm12  FROM counterpm_1h_enb c1 " +
                "WHERE  " +
                "c1.pm4='"+pm4+"'  " +
                "AND c1.pm2='"+pm2+"' " +
                "AND c1.pm16 IS NULL " +
                "AND c1.pm18 IS NULL  " +
                "AND c1.pm27 IS NULL  " +
                "AND c1.pm30 IS NULL " +
                ") " +
                "AND c.pm12 NOT IN ( " +
                "SELECT DISTINCT n1.pm12  FROM nbipm_1h_enb n1 " +
                "WHERE  " +
                "n1.pm4='"+pm4+"'  " +
                "AND n1.pm2='"+pm2+"' " +
                "AND n1.pm16 IS NULL " +
                "AND n1.pm18 IS NULL  " +
                "AND n1.pm27 IS NULL  " +
                "AND n1.pm30 IS NULL  " +
                ") " +
                "))";
        //创建临时表
        recordAccuracyService_enb.createCounterTmpTabel(sql);
    }

    /**
     *
     * 功能描述:生成nbi临时表
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/8/13 9:32
     */
    public void createNbiTmpTable(String pm4,String pm2,String tabelName){

        String sql="CREATE TEMPORARY TABLE "+tabelName+"  (SELECT * " +
                "FROM nbipm_1h_enb  " +
                "WHERE  " +
                "pm4 = '"+pm4+"' " +
                "AND  " +
                "pm2='"+pm2+"' " +
                "AND  " +
                "pm12  IN (SELECT DISTINCT pm12 FROM counterpm_1h_enb c WHERE c.pm4 = '"+pm4+"' AND pm2='"+pm2+"' " +
                "AND c.pm12 NOT IN ( " +
                "SELECT DISTINCT c1.pm12  FROM counterpm_1h_enb c1 " +
                "WHERE  " +
                "c1.pm4='"+pm4+"'  " +
                "AND c1.pm2='"+pm2+"' " +
                "AND c1.pm16 IS NULL " +
                "AND c1.pm18 IS NULL  " +
                "AND c1.pm27 IS NULL  " +
                "AND c1.pm30 IS NULL " +
                ") " +
                "AND c.pm12 NOT IN ( " +
                "SELECT DISTINCT n1.pm12  FROM nbipm_1h_enb n1 " +
                "WHERE  " +
                "n1.pm4='"+pm4+"'  " +
                "AND n1.pm2='"+pm2+"' " +
                "AND n1.pm16 IS NULL " +
                "AND n1.pm18 IS NULL  " +
                "AND n1.pm27 IS NULL  " +
                "AND n1.pm30 IS NULL  " +
                ") " +
                ") )";
        //创建临时表
        recordAccuracyService_enb.createNbiTmpTabel(sql);
    }

}
