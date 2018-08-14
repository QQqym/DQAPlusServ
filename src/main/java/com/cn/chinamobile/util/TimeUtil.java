package com.cn.chinamobile.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zh on 2017/5/18.
 * 处理时间的工具类
 */
public class TimeUtil {
    SimpleDateFormat sourceFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat destFormatter = new SimpleDateFormat("yyyy-MM-dd-HH");
    SimpleDateFormat minFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public String getDestTime(String source){
        String destStr = "";
        try{
            destStr = destFormatter.format(sourceFormatter.parse(source));
        }catch (Exception e){
            Log.error("format timestr " + source + "error",e);
        }
        return destStr;
    }

    /**
     * 返回日期yyyy-mm-dd
     * @param date  时间
     * @return  日期字符串 格式yyyy-mm-dd
     */
    public String getDataTime(Date date){
        String destStr = "";
        try{
            destStr = destFormatter.format(date);
            destStr = destStr.substring(0,destStr.lastIndexOf("-"));
        }catch (Exception e){
            Log.error("format timestr " + date.getTime() + "error",e);
        }
        return destStr;
    }

    public int getHour(Date date){
        int hour = 0;
        try{
            String destStr = destFormatter.format(date);
            hour = Integer.parseInt(destStr.split("-")[3]);
        }catch (Exception e){
            Log.error("format timestr " + date.getTime() + "error",e);
        }
        return hour;
    }
    public Date getNextHour(Date prehour){
        try{
            Date d = new Date(prehour.getTime()+6*3600000L);
            return d;
        }catch (Exception e){
            Log.error("处理时间异常，时间"+prehour.getTime()+"加1小时",e);
            return new Date();
        }
    }

    /**
     * 获取上一个十五分钟的字符串
     * @param parsetime 解析时间，格式 yyyy-mm-dd-HH
     * @return上一个15min时间，yyyy-mm-dd HH:mm，yyyy/mm/dd HH:mm
     */
    public String getLastFifStr(String parsetime){
        String destStr = "";
        try{
            destStr = minFormatter.format(destFormatter.parse(parsetime).getTime()-900000L);
        }catch (Exception e){
            Log.error("获取上一个十五分钟时间失败 " + parsetime + "error",e);
        }
        destStr = destStr + "," + destStr.replace("-","/");
        return destStr;
    }

}
