package com.cn.chinamobile.util;

import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zh on 2017/5/9.
 */
public class FileUtil {

    public void writedatas(File outPath, List<Map<String, String>> pmdatas, String split) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPath), ContentInfo.ENCODING));
            int i = 0;
            for (Map<String, String> datamap : pmdatas) {
                StringBuffer bf = new StringBuffer();
                if (i == 0) {//写表头，只有第一行时遍历写表头
                    StringBuffer tablefield = new StringBuffer();
                    for (String columnkey : datamap.keySet()) {
                        tablefield.append(columnkey).append(split);
                    }
                    tablefield.deleteCharAt(tablefield.length() - 1);
                    out.write(tablefield.toString());
                    out.newLine();
                    i++;
                }

                //写数据
                for (Map.Entry entry : datamap.entrySet()) {
                    bf.append(entry.getValue()).append(split);
                }

                bf.deleteCharAt(bf.length() - 1);
                out.write(bf.toString());
                out.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public File initWriteFile(String dataType, String time, String tmpFilePrefix, String prefix) {
        String filename = ContentInfo.FILE_TEMP_PATH + File.separator + tmpFilePrefix + File.separator + dataType.toLowerCase()
                + File.separator + time + File.separator + prefix + "_" + dataType.toLowerCase();
        File writeFile = new File(filename);
        //如果父路径不存在，创建
        if (!writeFile.getParentFile().exists())
            writeFile.getParentFile().mkdirs();
        //如果已经解析过删除文件
        if (writeFile.exists())
            writeFile.delete();
        return writeFile;

    }

    /**
     * 回填OMC 数据的软件版本
     *
     * @param file         omc文件
     * @param encode       编码方式
     * @param userSwMap    小区名软件版本
     * @param swVersionMap 映射的版本
     * @param del          标识是否删除文件
     */
    public File backCounterSwver(File file, String encode, Map<String, String> userSwMap, Map<String, String> swVersionMap, boolean del, int taskid,String province,String vendor) {
        BufferedReader br = null;
        BufferedWriter bw = null;
        String tempFileName = file.getParentFile().getAbsolutePath() + File.separator + "backsw_" + file.getName();
        try {
            //用于写无设备版本号数据进excel
            ExcelUtil excelUtil = new ExcelUtil();
            //文件的输入输出路径先写死
            String path = ContentInfo.FILE_TEMP_PATH+"\\"+province+"_RECORD\\"+vendor+"\\"+province+"_"+vendor+"_"+"OMC_SWVERSION.xlsx";
            //sheetname
            String sheetName="OMC_SWVERSION";
            //产生XSSFWorkbook 列
            String[] columns={"taskid","userlabel","dn","verder"};
            //产生XSSFWorkbook
            excelUtil.createXSSFWorkbook(path,sheetName,columns);

            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encode));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFileName), encode));
            String readline = null;
            int i = 0;
            while (null != (readline = br.readLine())) {
                StringBuilder sb=new StringBuilder();
                sb.append(readline);
                //第一行加上表头软件版本
                if (i == 0) {
                    sb.append("," + ContentInfo.COUNTER_SWVERION);
                } else {
                    String readlineSplit=readline.split(",")[9];
                    String verder=readline.split(",")[6];
                    String dn = userSwMap.get(readlineSplit);
                    //这里读取的设备软件版本可能为""
                    if (swVersionMap.get(dn) == "" || swVersionMap.get(dn)==null) {
                        excelUtil.createRowData(String.valueOf(taskid),readlineSplit,readlineSplit,verder);
                        continue;
                    }
                    sb.append("," + swVersionMap.get(dn));
                }
                bw.write(sb.toString());
                bw.newLine();
                i++;
            }
            //将数据输出到excel
            excelUtil.writeXSSFExcel(path);
            //如果是解压的文件，删除
            if (del)
                file.delete();
        } catch (Exception e) {
            Log.error("back swversion error:" + file.getAbsolutePath(), e);
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
                if (null != bw) {
                    bw.flush();
                    bw.close();
                }
            } catch (Exception io) {
            }
        }
        return new File(tempFileName);
    }

    /**
     * 递归查找path路径下包含index的文件
     *
     * @param path  路径
     * @param index 包含的条件
     */
    public void findGivenFiles(String path, String index, List<File> localfiles) {
        if (!judgePath(path)) {
            return;
        }
        File[] files = new File(path).listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                findGivenFiles(file.getAbsolutePath(), index, localfiles);
            } else {
                if (file.getName().toLowerCase().contains(index)) {
                    localfiles.add(file);
                }
            }
        }
    }

    /**
     * 递归查找path路径下包含index的文件
     *
     * @param path  路径
     * @param index 包含的数据类型条件
     * @param time  时间
     */
    public void findGivenTimeFiles(String path, String index, String time, List<File> localfiles) {
        if (!judgePath(path)) {
            return;
        }
        File[] files = new File(path).listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                findGivenTimeFiles(file.getAbsolutePath(), index, time, localfiles);
            } else {
                if (file.getName().toLowerCase().contains(index) && (file.getName().contains(time) || file.getName().contains(time.replace("-", "")))) {
                    localfiles.add(file);
                }
            }
        }
    }

    /**
     * 递归查找路径下的所有文件
     *
     * @param path       路径
     * @param localfiles 查找的文件存放list
     */
    public void findAllFiles(String path, List<File> localfiles) {
        if (!judgePath(path)) {
            return;
        }
        File[] files = new File(path).listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                findAllFiles(file.getAbsolutePath(), localfiles);
            } else {
                //如果是check文件，跳过
                if (file.getName().toLowerCase().contains("check"))
                    continue;
                localfiles.add(file);
            }
        }
    }

    /**
     * 通过时间找到Udp文件的所有路径
     *
     * @param path 路径
     * @param time 时间
     * @return
     */
    public List<String> findUdpDatePath(String path, String time) {
        List<String> dates = new ArrayList<>();
        if (!judgePath(path)) {
            return dates;
        }
        File[] files = new File(path).listFiles();
        for (File file : files) {
            if (file.isDirectory() && (file.getName().contains(time) || file.getName().contains(time.replaceAll("-", "")))) {
                dates.add(file.getName());
            }
        }
        return dates;
    }

    /**
     * 判断路径是否存在以及是否包含文件
     *
     * @param path
     * @return
     */
    private boolean judgePath(String path) {
        Log.info("检测" + path + "路径下的文件");
        File root = new File(path);
        if (!root.exists()) {
            Log.info(path + " is not exist");
            return false;
        }
        File[] files = root.listFiles();
        if (null == files) {
            Log.info("there is no files :" + path);
            return false;
        }
        return true;
    }


    /**
     * 返回各省Udp的路径
     *
     * @param province 省份
     * @param type     UDP / NBI/ PCT
     * @return
     */
    public String getProUdpPath(String province, String type) {
        return ContentInfo.SOURCE_FILE_ROOT_PATH + province + File.separator + type;
    }

    //匹配版本的正则
    public boolean match(String str, String reg, PatternMatcher matcher) {
        boolean returnValue;
        try {
            PatternCompiler compiler = new Perl5Compiler();
            org.apache.oro.text.regex.Pattern pattern = compiler.compile(reg);
            returnValue = matcher.matches(str, pattern);
        } catch (Exception e) {
            returnValue = false;
        }
        return returnValue;
    }
}
