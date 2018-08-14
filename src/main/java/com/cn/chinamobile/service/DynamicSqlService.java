package com.cn.chinamobile.service;

import com.cn.chinamobile.entity.ColType;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.ExcelUtil;
import com.cn.chinamobile.util.Log;
import com.cn.chinamobile.util.TimeUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by xueweixia on 2017/5/9.
 * 动态处理sql类
 */
@Service
public class DynamicSqlService {

    /**
     * 获取Counter汇总PM的sql，15min级别
     *
     * @param table_name  表名
     * @param colMap      列信息
     * @param counterAMap counter算法
     * @param lostcounter 跟模板对比缺失的指标
     * @return
     */
    public Map<String, List<String>> getCounterTPMSql(String table_name, LinkedHashMap<String, ColType> colMap, Map<String, String> counterAMap, List<String> lostcounter) {
        try {
            Map<String, List<String>> map = new HashMap<>();
            //记录查询的指标顺序
            List<String> collectPM = new ArrayList<>();
            StringBuilder sumSql = new StringBuilder("select ");

            //拼接资源字段的查询
            for (Map.Entry entry : colMap.entrySet()) {
                ColType value = (ColType) entry.getValue();
                //拼接资源字段的查询
                if (value.getCls() == String.class) {
                    sumSql.append(value.getColName()).append(",");
                    collectPM.add(value.getOrigColName());
                }
            }

            //拼接指标字段
            for (Map.Entry pmentry : counterAMap.entrySet()) {
                collectPM.add((String) pmentry.getKey());
                String formula = (String) pmentry.getValue();
                if (formula.equals("不支持")) {
                    formula = "-1";
                } else {
                    //以+=*、()分割
                    String[] cs = formula.split("\\+|\\-|\\*|\\/|\\(|\\)|\\s+|\\>|\\%");
                    //依次替换指标
                    for (String c : cs) {
                        if (colMap.containsKey(c.trim())) {
                            ColType cvalue = colMap.get(c);
                            String columnkey = cvalue.getColName();
                            formula = replaceFormula(formula, c, columnkey);
                        } else if (lostcounter.contains(c.trim())) {
                            formula = "0";
                            break;
                        }

                    }
                }
                sumSql.append(formula).append(",");

            }
            sumSql.deleteCharAt(sumSql.length() - 1);
            sumSql.append(" from ").append(table_name);
            map.put(sumSql.toString(), collectPM);
            return map;
        } finally {

        }
    }

    /**
     * 获取分软件版本的Counter汇总PM的sql，15min级别
     *
     * @param table_name  表名
     * @param colMap      列信息
     * @param counterAMap counter算法
     * @param lostcounter 跟模板对比缺失的指标
     * @return
     */
    public Map<String, List<String>> getSwCounterTPMSql(String table_name, LinkedHashMap<String, ColType> colMap, Map<String, Map<String, String>> counterAMap, List<String> lostcounter) {
        try {
            Map<String, List<String>> map = new HashMap<>();
            //记录查询的指标顺序
            List<String> collectPM = new ArrayList<>();
            StringBuilder sumSql = new StringBuilder("select ");

            //记录软件版本的列
            String swverCol = "";

            //拼接资源字段的查询
            for (Map.Entry entry : colMap.entrySet()) {
                ColType value = (ColType) entry.getValue();
                //拼接资源字段的查询
                if (value.getCls() == String.class) {
                    sumSql.append(value.getColName()).append(",");
                    collectPM.add(value.getOrigColName());

                    if (value.getOrigColName().equalsIgnoreCase(ContentInfo.COUNTER_SWVERION))
                        swverCol = value.getColName();
                }
            }

            //拼接指标字段
            for (Map.Entry pmentry : counterAMap.entrySet()) {
                collectPM.add((String) pmentry.getKey());
                Map<String, String> swmap = (Map<String, String>) pmentry.getValue();
                sumSql.append("case");


                for (Map.Entry alentry : swmap.entrySet()) {
                    String swversion = (String) alentry.getKey();
                    String formula = (String) alentry.getValue();
                    sumSql.append(" when ").append(swverCol).append("= '").append(swversion).append("' then ");
                    if (formula.equals("不支持")) {
                        formula = "-1";
                    } else {
                        //以+=*、()分割
                        String[] cs = formula.split("\\+|\\-|\\*|\\/|\\(|\\)|\\s+|\\>|\\%");
                        //依次替换指标
                        for (String c : cs) {
                            if (colMap.containsKey(c.trim())) {
                                ColType cvalue = colMap.get(c);
                                String columnkey = cvalue.getColName();
                                formula = replaceFormula(formula, c, columnkey);
                            } else if (lostcounter.contains(c.trim())) {
                                formula = "0";
                                break;
                            }

                        }
                    }
                    sumSql.append(formula);

                }
                sumSql.append(" end ,");
            }
            sumSql.deleteCharAt(sumSql.length() - 1);
            sumSql.append(" from ").append(table_name);
            map.put(sumSql.toString(), collectPM);
            return map;
        } finally {

        }
    }

    /**
     * 获取PM汇总一小时的PM sql
     *
     * @param table_name
     * @param colMap
     * @param summap
     * @param groupbyColumn
     * @return
     */
    public Map<String, List<String>> getSumPMSql(String table_name, LinkedHashMap<String, ColType> colMap, Map<String, String> summap, String groupbyColumn) {
        Map<String, List<String>> map = new HashMap<>();
        try {
            //记录查询的指标顺序
            List<String> collectPM = new ArrayList<>();
            StringBuilder sumSql = new StringBuilder("select ");
            String groupbycname = "";

            for (Map.Entry entry : colMap.entrySet()) {
                ColType value = (ColType) entry.getValue();
                String column = value.getColName();
                //拼接资源字段的查询
                if (value.getCls() == String.class) {
                    if (groupbyColumn.trim().contains(value.getOrigColName().trim())) {
                        sumSql.append(column).append(",");
                        groupbycname = column;
                    } else {
                        String formula = "min(" + column + ")";
                        sumSql.append(formula).append(",");
                    }
                    collectPM.add(value.getOrigColName());
                } else { //拼接指标字段的查询
                    collectPM.add(value.getOrigColName());
                    String formula = summap.get(value.getOrigColName());
                    //以+=*、()分割
                    String[] cs = formula.split("\\+|\\-|\\*|\\/|\\(|\\)");
                    //依次替换指标
                    for (String c : cs) {
                        if (colMap.containsKey(c.trim())) {
                            ColType cvalue = colMap.get(c);
                            String columnkey = cvalue.getColName();
                            formula = replaceFormula(formula, c, columnkey);
                        }
                    }
                    sumSql.append(formula).append(",");
                }

            }
            sumSql.deleteCharAt(sumSql.length() - 1);
            sumSql.append(" from ").append(table_name);

            //分组字段不为空，拼接分组字段
            if (!groupbyColumn.equals("")) {
                sumSql.append(" group by  ").append(groupbycname);
            }
            Log.info("汇总sql:" + sumSql.toString());
            map.put(sumSql.toString(), collectPM);

        } catch (Exception e) {
            Log.error("获取汇总表sql错误：" + table_name, e);
        }
        return map;
    }

    /**
     * 获取kpi sql
     *
     * @param table_name 1h汇总pm的表名
     * @param colMap     PM指标
     * @param kPIAlMap   计算KPI的公式
     * @return 汇总KPI的sql，KPI指标的顺序
     */
    public Map<String, List<String>> getKPISql(String table_name, LinkedHashMap<String, ColType> colMap, Map<String, String> kPIAlMap) {
        Map<String, List<String>> map = new HashMap<>();
        try {

            //记录查询的指标顺序
            List<String> collectKPI = new ArrayList<>();
            StringBuilder sumSql = new StringBuilder("select ");

            for (Map.Entry entry : colMap.entrySet()) {

                ColType value = (ColType) entry.getValue();
                String column = value.getColName();
                //拼接资源字段的查询
                if (value.getCls() == String.class) {
                    sumSql.append(column).append(",");
                    collectKPI.add(value.getOrigColName());
                }
            }
            for (Map.Entry entry : kPIAlMap.entrySet()) {
                String name = (String) entry.getKey();
                String formula = (String) entry.getValue();
                //以+=*、()分割
                String[] cs = formula.split("\\+|\\-|\\*|\\/|\\(|\\)");
                //依次替换指标
                ReplaceFormula:
                for (String c : cs) {
                    if (colMap.containsKey(c.trim())) {
                        ColType cvalue = colMap.get(c);
                        String columnkey = cvalue.getColName();
                        formula = replaceFormula(formula, c, columnkey);
                    }
                    //假如PM没配置，KPI配置了PM，将公式改成null
                    else if (IniDomain.sumlgorithmMap.containsKey(c.trim())) {
                        formula = "null";
                        break ReplaceFormula;
                    }
                }
                collectKPI.add(name);
                sumSql.append(formula).append(",");
            }
            sumSql.deleteCharAt(sumSql.length() - 1);
            sumSql.append(" from ").append(table_name);

            Log.info("汇总sql:" + sumSql.toString());
            map.put(sumSql.toString(), collectKPI);

        } catch (Exception e) {
            Log.error("获取计算kpi的sql错误：" + table_name, e);
        }
        return map;
    }

    /**
     * 替换公式中的指标
     *
     * @param formula   公式
     * @param c         指标名
     * @param columnkey 替换的字段
     * @return 替换后的公式
     */
    private String replaceFormula(String formula, String c, String columnkey) {
        int midd = formula.indexOf(c) + c.length();
        String pre = formula.substring(0, midd);
        String sub = formula.substring(midd);
        String replaceStr = "";
        //若计算公式中包含+-*/中的任意一个，对指标进行判空
        if (formula.contains("+") || formula.contains("-") || formula.contains("*") || formula.contains("/")) {
            replaceStr = "IFNULL(" + columnkey + ",0)";
        } else {
            replaceStr = columnkey;
        }
        formula = pre.replace(c, replaceStr) + sub;
        return formula;
    }

    /**
     * 将文件中的数据导入数据库
     *
     * @param srcPath    源文件
     * @param table      表名
     * @param colMap     列信息
     * @param connection 连接信息
     * @param splitchar  切割字符
     * @param encode     编码方式
     */
    public boolean importData(String srcPath, String table, LinkedHashMap<String, ColType> colMap, Connection connection, String splitchar, String encode) {
        boolean flag = true;
        PreparedStatement statement = null;
        BufferedReader srcReader = null;
        String line = "";
        try {
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(getPrePareSql(table, colMap.size()));
            srcReader = new BufferedReader(new InputStreamReader(new FileInputStream(srcPath), encode));
            int count = 0;
            String header[] = null;
            while ((line = srcReader.readLine()) != null) {
                if (count == 0) { //第一行记录表头,列名
                    count++;
                    header = line.split(splitchar, -1);
                    continue;
                } else {//获取值
                    String[] ds = line.split(splitchar, -1);
                    //每次循环都要一个values
                    Map<String, String> values = new HashMap<>();
                    for (int i = 0; i < ds.length; i++) {
                        values.put(header[i].trim(), ds[i].trim());
                    }
                    int i = 0;
                    for (Map.Entry<String, ColType> entry : colMap.entrySet()) {
                        i++;
                        if (entry.getValue().getCls() == Double.class) {
                            try {
                                Double value = Double.parseDouble(values.get(entry.getKey()));
                                statement.setDouble(i, value);

                            } catch (Exception e) {
                                statement.setNull(i, Types.DOUBLE);
                            }

                        } else {
                            String va = values.get(entry.getKey());
                            if (va.equals("") || va.equalsIgnoreCase("null")) {
                                statement.setString(i, "");
                            } else {
                                statement.setString(i, va);
                            }

                        }
                    }

                    //ENB counter 最后一列放入映射后的软件版本

                    statement.addBatch();
                    if (++count % 1000 == 0) {
                        statement.executeBatch();
                        connection.commit();
                    }
                }

            }
            statement.executeBatch();
            connection.commit();
        } catch (Exception e) {
            Log.error("import data error:" + srcPath + ":" + line, e);
            flag = false;
        } finally {
            try {
                if (srcReader != null) {
                    srcReader.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
        return flag;
    }


    /**
     * 功能描述: 对入库的数据进行过滤，保证只有五个时间段的数据
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/7/3 17:12
     */
    public boolean importDataAllTime(String srcPath, String table, LinkedHashMap<String, ColType> colMap, Connection connection, String splitchar, String encode, Map<String, ArrayList<String>> userLabelMap,String path) {
        boolean flag = true;
        PreparedStatement statement = null;
        BufferedReader srcReader = null;
        String line = "";

        //用于将不参考数据记录进excel
        ExcelUtil excelUtil = new ExcelUtil();
        //文件的输入输出路径先写死
        String path0= path;
        //sheetname
        String sheetName="OMC_TRASH";
        //产生XSSFWorkbook 列
        String[] columns={"userlabel","startTime","endTime","verder"};
        //产生XSSFWorkbook
        excelUtil.createXSSFWorkbook(path0,sheetName,columns);

        try {
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(getPrePareSql(table, colMap.size()));
            srcReader = new BufferedReader(new InputStreamReader(new FileInputStream(srcPath), encode));
            int count = 0;
            String header[] = null;
            while ((line = srcReader.readLine()) != null) {
                if (count == 0) { //第一行记录表头,列名
                    count++;
                    header = line.split(splitchar, -1);
                    continue;
                } else {//获取值

                    String[] ds = line.split(splitchar, -1);
                    //获取值之后先判断是否需要入库通过userLabel
                    if (userLabelMap.get(ds[9].trim()).size() < 5) {
                        continue;
                    }
                    //每次循环都要一个values
                    Map<String, String> values = new HashMap<>();

                    if(ds.length>header.length){
                        //如果字符串中长度保存excel,先如此处理
                        String str=ds[9]+","+ds[10];
                        excelUtil.createRowData(str,ds[1],ds[2],ds[6]);
                        continue;
                    }
                    for (int i = 0; i < ds.length; i++) {
                        values.put(header[i].trim(), ds[i].trim());
                    }
                    int i = 0;
                    for (Map.Entry<String, ColType> entry : colMap.entrySet()) {
                        i++;
                        if (entry.getValue().getCls() == Double.class) {
                            try {
                                Double value = Double.parseDouble(values.get(entry.getKey()));
                                statement.setDouble(i, value);
                            } catch (Exception e) {
                                statement.setNull(i, Types.DOUBLE);
                            }

                        } else {
                            String va = values.get(entry.getKey());
                            if (va.equals("") || va.equalsIgnoreCase("null")) {
                                statement.setString(i, "");
                            } else {
                                statement.setString(i, va);
                            }

                        }
                    }

                    //ENB counter 最后一列放入映射后的软件版本
                    statement.addBatch();
                    if (++count % 1000 == 0) {
                        statement.executeBatch();
                        connection.commit();
                    }
                }

            }
            //将数据输出到excel
            excelUtil.writeXSSFExcel(path0);
            statement.executeBatch();
            connection.commit();
        } catch (Exception e) {
            Log.error("import data error:" + srcPath + ":" + line, e);
            flag = false;
        } finally {
            try {
                if (srcReader != null) {
                    srcReader.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
        return flag;
    }


    /**
     * 将文件中的数据导入数据库,单独处理ENB文件
     *
     * @param srcPath    源文件
     * @param table      表名
     * @param colMap     列信息
     * @param connection 连接信息
     * @param splitchar  切割字符
     * @param encode     编码方式
     * @param parsetime  解析时间
     */
    public void importENBPMData(String srcPath, String table, LinkedHashMap<String, ColType> colMap, Connection connection, String splitchar, String encode, String parsetime) {
        PreparedStatement statement = null;
        BufferedReader srcReader = null;
        //如果是ENB数据，定义上一个十五分钟的字符串
        String lastFifStr = new TimeUtil().getLastFifStr(parsetime);
        String line = "";
        try {
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(getPrePareSql(table, colMap.size()));
            srcReader = new BufferedReader(new InputStreamReader(new FileInputStream(srcPath), encode));
            int count = 0;
            while ((line = srcReader.readLine()) != null) {
                if (count == 0) { //第一行跳过
                    count++;
                    continue;
                } else {
                    String[] ds = line.split(splitchar, -1);
                    int i = 0;
                    for (Map.Entry<String, ColType> entry : colMap.entrySet()) {
                        i++;
                        if (entry.getValue().getCls() == Double.class) {
                            //ENB的上一个十五分钟数据，这里判断数据是否维护某个字段
                            if (line.contains(lastFifStr.split(",")[0]) || line.contains(lastFifStr.split(",")[1])) {
                                if (IniDomain.leftpms.contains(entry.getValue().getOrigColName())) {
                                    if (ds[i - 1] == null || ds[i - 1].equals("") || ds[i - 1].equalsIgnoreCase("null") || ds[i - 1].equalsIgnoreCase("NA"))
                                        statement.setNull(i, Types.DOUBLE);
                                    else
                                        statement.setDouble(i, Double.parseDouble(ds[i - 1]));
                                } else {
                                        statement.setNull(i, Types.DOUBLE);
                                }
                            } else {
                                //遗留上一十五分钟的指标，本小时的四个时间段设置为空
                                if (IniDomain.leftpms.contains(entry.getValue().getOrigColName())) {
                                    statement.setNull(i, Types.DOUBLE);
                                }
                                else if (ds[i - 1] == null || ds[i - 1].equals("") || ds[i - 1].equalsIgnoreCase("null") || ds[i - 1].equalsIgnoreCase("NA"))
                                    statement.setNull(i, Types.DOUBLE);
                                else
                                    statement.setDouble(i, Double.parseDouble(ds[i - 1]));
                            }
                        } else {
                            if (ds[i - 1].equals("") || ds[i - 1].equalsIgnoreCase("null")) {
                                statement.setString(i, "");
                            } else {
                                statement.setString(i, ds[i - 1]);
                            }

                        }
                    }
                    statement.addBatch();
                    if (++count % 1000 == 0) {
                        statement.executeBatch();
                        connection.commit();
                    }
                }

            }
            statement.executeBatch();
            connection.commit();
        } catch (Exception e) {
            Log.error("import data error:" + srcPath + ":" + line, e);
        } finally {
            try {
                if (srcReader != null) {
                    srcReader.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
    }

    /**
     * 动态创建入库语句 prepare
     *
     * @param table
     * @param colums
     * @return
     */
    public String getPrePareSql(String table, int colums) {
        StringBuilder prepareSql = new StringBuilder(" insert into ").append(table).append(" values (");
        for (int i = 0; i < colums; i++) {
            prepareSql.append(" ?,");
        }
        prepareSql.deleteCharAt(prepareSql.length() - 1);
        prepareSql.append(")");
        return prepareSql.toString();
    }

    /**
     * 返回列信息
     *
     * @param meta      第一行数据
     * @param splitchar 数据分割符
     * @param neType    网元类型，若是ENB，最后一列新增软件版本SwVersion
     * @return 原始指标名，列字段信息
     */
    public LinkedHashMap<String, ColType> getColMap(String[] meta, String splitchar, String neType) {
        LinkedHashMap<String, ColType> colMap = new LinkedHashMap<String, ColType>();
        String[] cols = meta[0].split(splitchar, -1);
        String[] values = meta[1].split(splitchar, -1);
        if (cols.length != values.length) {
            throw new RuntimeException("列头与数据列数不一致");
        }
        List<String> columns = new ArrayList<>(100);
        //将重复表头去重
        for (int i = 0; i < cols.length; i++) {
            if (!columns.contains(cols[i].trim()))
                columns.add(cols[i].trim());
        }
        int i = 0;
        for (; i < columns.size(); i++) {
            String col = "c" + i;
            colMap.put(columns.get(i), new ColType(col, columns.get(i)));
        }

        //如果是ENB，最后加一列软件版本
        if (neType.equalsIgnoreCase("ENB")) {
            String col = "c" + i;
            colMap.put(ContentInfo.COUNTER_SWVERION, new ColType(col, ContentInfo.COUNTER_SWVERION, String.class));
        }
        return colMap;
    }

    /**
     * 获取pm 15min的建表语句
     *
     * @param pms    配置的PM项
     * @param summap 汇总指标集
     * @return
     */
    public LinkedHashMap<String, ColType> getPMColMap(List<String> pms, Map<String, String> summap) {
        LinkedHashMap<String, ColType> colMap = new LinkedHashMap<String, ColType>();

        for (int i = 0; i < pms.size(); i++) {
            String col = "pm" + i;
            //如果包含是业务字段
            if (summap.containsKey(pms.get(i)))
                colMap.put(pms.get(i), new ColType(col, pms.get(i), Double.class));
            else
                colMap.put(pms.get(i), new ColType(col, pms.get(i), String.class));
        }
        return colMap;
    }

    /**
     * 取文件的前两行数据
     *
     * @param srcPath 文件的绝对路径
     * @return 文件的前两行
     */
    public String[] getMetaData(String srcPath, String encode) {
        String[] meta = new String[2];
        BufferedReader srcReader = null;
        try {
            srcReader = new BufferedReader(new InputStreamReader(new FileInputStream(srcPath), encode));
            String line;
            int i = 0;
            while ((line = srcReader.readLine()) != null && i < 2) {
                meta[i++] = line;
            }
            return meta;
        } catch (Exception e) {
            Log.error("read file error :" + srcPath, e);
        } finally {
            if (srcReader != null) {
                try {
                    srcReader.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        }
        return meta;
    }

    /**
     * 动态创建counter建表语句
     *
     * @param table_name 需要创建的表名
     * @param colMap     列头数据<column，实际列名>
     * @param neType     数据类型
     * @return 建表sql
     */
    public String getCreateCounterSql(String table_name, LinkedHashMap<String, ColType> colMap, String neType) {
        StringBuilder createsqlBf = new StringBuilder("create temporary table " + table_name + "(");
        int i = 0;
        //其他网元类型，资源字段9个
        int resNUm = 9;
        if (neType.equalsIgnoreCase("ENB")) {//ENB数据类型，资源字段10个
            resNUm = 11;
        }
        for (ColType colType : colMap.values()) {
            createsqlBf.append(colType.getColName()).append(" ");
            if ((!neType.equalsIgnoreCase("ENB") && i >= resNUm) || (neType.equalsIgnoreCase("ENB") && i >= resNUm && i < colMap.size() - 1)) {
                createsqlBf.append("double(20,3) ");
                colType.setCls(Double.class);
            } else {
                createsqlBf.append("varchar(200) ");
                colType.setCls(String.class);
            }
            createsqlBf.append(",");
            i++;
        }
        createsqlBf.deleteCharAt(createsqlBf.length() - 1);
        //添加最后的)
        createsqlBf.append(" ) DEFAULT CHARSET=utf8 ");
        return createsqlBf.toString();
    }

    /**
     * 动态创建pm建表语句
     *
     * @param table_name 需要创建的表名
     * @param colMap     列头数据<column，实际列名，数据类型>
     * @return 建表sql
     */
    public String getCreatePMSql(String table_name, LinkedHashMap<String, ColType> colMap) {
        StringBuilder createsqlBf = new StringBuilder("create temporary table " + table_name + "(");

        for (ColType colType : colMap.values()) {
            createsqlBf.append(colType.getColName()).append(" ");
            if (colType.getCls() == Double.class) {
                createsqlBf.append("double(20,3) ");
            } else {
                createsqlBf.append("varchar(200) ");
            }
            createsqlBf.append(",");
        }
        createsqlBf.deleteCharAt(createsqlBf.length() - 1);
        //添加最后的)
        createsqlBf.append(" ) DEFAULT CHARSET=utf8 ");
        return createsqlBf.toString();
    }


}
