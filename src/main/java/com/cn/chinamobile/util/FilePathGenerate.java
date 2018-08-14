package com.cn.chinamobile.util;

import java.io.File;

/**
 * 生成各个文件存放的路径
 */
public class FilePathGenerate {
    /**
     * 生成北向PM文件存放的路径
     *
     * @param args 接口调用输入的参数
     * @return 北向文件存放的路径
     */
    public String getBeiXiangPMPath(String datetime, String... args) {
        String bxpath = ContentInfo.SOURCE_FILE_ROOT_PATH + args[0] + "/NBI/" + args[1] + "/" + args[2] + "/PM/" + datetime;
        return bxpath;
    }

    /**
     * 生成OMC文件存放的路径
     *
     * @param args 接口调用输入的参数
     * @return OMC文件的存放路径sheetName
     */
    public String getOMCPath(String datetime, String... args) {
        String version = args[3];
        boolean judegPoint = false;
        int fileCount = 0;
        //判断version args[3]这一层目录是否存在，不存在是否有高版本文件或者其他单个版本
        String omcPathPortion = ContentInfo.SOURCE_FILE_ROOT_PATH + args[0] + "/PCT/" + args[1] + "/" + args[2];
        File root = new File(omcPathPortion);
        if (root.exists()) {
            File[] files = root.listFiles();
            if (null != files) {
                //说明有文件
                judegPoint = true;
                //文件夹数量
                fileCount = files.length;
            }
        }
        if (fileCount != 0) {
            File[] files = root.listFiles();
            if (fileCount == 1) {
                version = files[0].getName();
            } else {
                //截取每个文件名字,并判断较大的版本号作为数据读取目录
                String[] versionPart = version.substring(1).split("\\.");
                //将版本号去标点变成数字
                StringBuilder sb = new StringBuilder();
                //保存初始确定的版本，用于后面判断
                StringBuilder sbArgs = new StringBuilder();
                for (int i = 0; i < versionPart.length; i++) {
                    sb.append(versionPart[i]);
                    sbArgs.append(versionPart[i]);
                }
                //判断版本号较高的文件夹
                for (int i = 0; i < files.length; i++) {
                    String[] strName = files[i].getName().substring(1).split("\\.");
                    //将版本号去标点变成数字
                    StringBuilder sb2 = new StringBuilder();
                    for (int j = 0; j < strName.length; j++) {
                        sb2.append(strName[j]);
                    }
                    //如果发现存在NBI需求的版本直接对应版本
                    if (Integer.parseInt(sbArgs.toString()) == Integer.parseInt(sb2.toString())) {
                        sb = sbArgs;
                        break;
                    }
                    if (Integer.parseInt(sb2.toString()) > Integer.parseInt(sb.toString())) {
                        sb = sb2;
                    }
                }
                version = "V";
                //将较高版本号转换格式eg:123=>1.2.3
                for (int i = 0; i < sb.toString().length(); i++) {
                    version = version + sb.toString().substring(i, i + 1) + ".";
                }
                //去掉最后的小数点
                version = version.substring(0, version.length() - 1);
            }
        }
        String omcPath = ContentInfo.SOURCE_FILE_ROOT_PATH + args[0] + "/PCT/" + args[1] + "/" + args[2] + "/" + version + "/" + datetime;
        return omcPath;
    }

    /**
     * 生成网优平台存放文件的路径
     *
     * @param args 接口调用输入的参数
     * @return 网优平台文件存放的路径
     */
    public String getWyptPath(String datetime, String... args) {
        String wyptPath = ContentInfo.SOURCE_FILE_ROOT_PATH + args[0] + "/WYPT/" + args[1] + "/" + args[2] + "/" + args[3] + "/" + datetime;
        return wyptPath;
    }

    /**
     * 生成话务网管存放文件的路径
     *
     * @param args 接口调用输入的参数
     * @return 话务网管文件存放的路径
     */
    public String getHwwgPath(String datetime, String... args) {
        String hwwgPath = ContentInfo.SOURCE_FILE_ROOT_PATH + args[0] + "/HWWG/" + args[1] + "/" + args[2] + "/" + args[3] + "/" + datetime;
        return hwwgPath;
    }

    /**
     * 生产Udp存放文件的路径
     *
     * @param filetype 文件类型
     * @param datetime 时间
     * @param args     参数 0：省份:1：网元类型、2：厂家 、3：类型（NRM/PM）
     * @return UPD存放文件的路径
     */
    public String getUdpPath(String filetype, String datetime, String... args) {
        String udpPath = ContentInfo.SOURCE_FILE_ROOT_PATH + args[0] + "/" + filetype + "/" + args[1] + "/" + args[2] + "/" + args[3] + "/" + datetime;
        return udpPath;
    }

    /**
     * 参数：省份、网元、厂家、类型（PM/NRM）
     * 生产Udp存放文件的路径,时间路径的上一层
     *
     * @param filetype 文件类型
     * @param args     参数 0：省份:1：网元类型、2：厂家 、3：类型（NRM/PM）
     * @return UPD存放文件的路径
     */
    public String getUdpRootPath(String filetype, String... args) {
        String udpPath = ContentInfo.SOURCE_FILE_ROOT_PATH + args[0] + "/" + filetype + "/" + args[1] + "/" + args[2] + "/" + args[3];
        return udpPath;
    }
}
