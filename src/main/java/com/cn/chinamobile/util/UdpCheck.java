package com.cn.chinamobile.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhou on 2017/10/30.
 */
public class UdpCheck {
    String digitPattern="\\d+";


    /**
     * 判断Dn前缀
     * @param dnPrefix 前缀值
     * @param cityMap 地市信息
     * @param vendorMap 厂家信息
     * @param provinceMap 省份信息
     * @return
     */
    public boolean checkDnprefix(String dnPrefix,Map<String,String> cityMap,Map<String,String> vendorMap,Map<String,String> provinceMap){
        boolean flag = true;
        try{
            String dns[] = dnPrefix.split("-");
            //厂家不符合
            if(!vendorMap.containsKey(dns[0])){
                flag = false;
            }

            String province = dns[1].substring(2);
            //省份不符合
            if(!provinceMap.containsKey(province)){
                flag = false;
            }

            //地市不符合
            String city = provinceMap.get(province)+"-"+dns[2];
            if(!cityMap.containsKey(city)){
                flag = false;
            }
        }catch (Exception e){
            flag = false;
        }

        return flag;
    }

    /**
     * 获取地市名称
     * @param province 省份简称
     * @param dnPrefix dn前缀
     * @param cityMap 地市信息
     * @param provinceMap 省份信息
     * @return 地市名称
     */
    public String getCityname(String province,String dnPrefix,Map<String,String> cityMap,Map<String,String> provinceMap){
        String cityname = "";
        try{

            String dns[] = dnPrefix.split("-");

            //地市不符合
            String city = provinceMap.get(province)+"-"+dns[2];

            //将数字替换为空
            Pattern p=Pattern.compile(digitPattern);
            Matcher m=p.matcher(city);
            String newStr=m.replaceAll("");
            if(cityMap.containsKey(newStr)){
                cityname = cityMap.get(newStr).split("-")[1];
            }else {
                cityname = dnPrefix;
            }
        }catch (Exception e){
            Log.error("获取地市信息失败："+dnPrefix,e);
            cityname = dnPrefix;
        }
        return cityname;
    }

    /**
     * 返回是否为空
     * @param source 原字符串
     * @return 是否为空
     */
    public boolean isEmpty(String source){
        return source.isEmpty();
    }

    /**
     * 判断是否为0
     * @param source 原字符串
     * @return 是否为0
     */
    public boolean isZero(String source){
        return source.equalsIgnoreCase("0");
    }

    /**
     * 判断类型是不是合法
     * @param source 原字符串
     * @param datatype 数据类型
     * @param defination 指标定义
     * @return 类型是否合法
     */
    public boolean isType(String source,String datatype,String defination){
        boolean flag = true;
        //判断分原因的PM指标
        if(source.equalsIgnoreCase("CV")){
            return true;
        }
        if(datatype.equals("整数") || datatype.equals("整型")){
            flag = isInteger(source);
        }else if(datatype.equals("实型")){
            flag = isDouble(source);
       }
// else if(datatype.equals("枚举") || datatype.equals("枚举值")){
//            flag = isEnum(source,defination);
//        }
       else if(datatype.equals("布尔")){
            flag = isBoolean(source);
        }else if(datatype.equals("日期")){
            flag = isDate(source);
        }else if(datatype.equals("结构列表")){
            flag = isStructList(source);
        }
//        else if(datatype.equals("枚举列表")){
//            flag = isEnumList(source,defination);
//        }

        return  flag;

    }

    private boolean isInteger(String value){
        try {
            Integer.parseInt(value);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    private boolean isDouble(String value){
        try {
            Double.parseDouble(value);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * 判断是否是枚举
     * @param value 值
     * @param defination 定义
     * @return
     */
    private boolean isEnum(String value,String defination){
        try{
            int begin = defination.lastIndexOf("{");
            int end = defination.indexOf("}",begin);
            //定义中缺少}的情况
            if(begin>0 && end<0)
                end = defination.length()-1;
            //如果定义中没有枚举值的定义
            if(begin<0){
                return true;
            }
            String enumValue = defination.substring(begin+1,end);
            if(enumValue.toLowerCase().contains(value.toLowerCase())){
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            System.out.println(defination);
        }
       return true;
    }


    /**
     * 判断是否是结构列表
     * @param value
     * @return
     */
    private boolean isStructList(String value){
        String regular = "\\{\\(.*\\)\\}";
        if(value.equals("{}") || value.matches(regular)){
            return true;
        }else {
            System.out.println(value);
            return false;
        }
    }

    private boolean isDate(String value){
        String regular = ".*\\-.*\\-.*";
        String regular1 = ".*\\/.*\\/.*";
        if(value.matches(regular) || value.matches(regular1)){
            return true;
        }else {
            return false;
        }
    }

    private boolean isBoolean(String value){
        if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")){
            return  true;
        }
        return false;
    }

    /**
     * 判断是否是枚举列表
     * @param value 值
     * @param defination 定义
     * @return
     */
    private boolean isEnumList(String value,String defination){
        String[] cs = value.split("\\{\\(|\\,|\\)\\}");
        int begin = defination.lastIndexOf("{");
        int end = defination.indexOf("}",begin);
        //如果定义中没有枚举值的定义
        if(begin<0){
            return true;
        }
        String enumValue = defination.substring(begin+1,end);
        for(String c : cs){
            if(!enumValue.toLowerCase().contains(c.toLowerCase())){
                return false;
            }
        }
        return true;
    }

    /**
     * 通过dn获取软件版本号
     * @param dn dn的值
     * @param dnSwversionMap 软件版本，key为managedelement的dn，value是版本号
     * @return 软件版本号
     */
    public String getSwversion(String dn,Map<String,String> dnSwversionMap){
        //从ManagedElement后查找第一个,
        int endPos = dn.indexOf(",",dn.indexOf("ManagedElement"));
        String mdn = "";
        if(endPos>0){ //如果ManagedElement后有,截取字符串到ManagedElement后的第一个,
            mdn = dn.substring(0,endPos);
        }else {//如果ManagedElement后没有,mdn的值为dn
            mdn = dn;
        }
        String swVersion = dnSwversionMap.get(mdn);
        if(swVersion==null){
            swVersion="";
        }
        return swVersion;
    }
}
