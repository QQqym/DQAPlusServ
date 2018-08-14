package com.cn.chinamobile.service;

import com.cn.chinamobile.pojo.mybatis.GlobalField;
import com.cn.chinamobile.util.Log;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.*;

/**
 * Created by zh on 2017/5/9.
 * 动态入库的处理，入到正式库
 */

@Service
public class DynamicImportService {

    /**
     * 通过taskid删除表中的数据
     * @param taskid 任务id号
     * @param globalFields 字段映射
     * @param connection 数据连接
     */
    public  void deleteBytaskid(int taskid, List<GlobalField> globalFields,Connection connection){
        String taskColu = "";
        String tablename = "";
        for(GlobalField globalField : globalFields){
            if(globalField.getColumnName().equalsIgnoreCase("taskid")){
                taskColu = globalField.getFactColumnname();
                tablename = globalField.getTableName();
                break;
            }
        }
        String sql = "delete from " + tablename + " where " + taskColu + "="+taskid;
        Statement statement = null;
        try{
            statement = connection.createStatement();
            statement.execute(sql);
        }catch (Exception e){
            Log.error("执行sql失败："+sql,e);
        }

    }


    /**
     *PM、KPI数据入库
     * @param taskid   任务id
     * @param srcPath  文件路径
     * @param table 表名
     * @param parsetime 解析时间，处理ENB数据入库
     * @param globalFields 列信息
     * @param connection  连接信息
     * @param splitchar 分割符
     * @param encode   文件编码
     * @param neType 网元类型
     * @param dataType 数据类型
     */
    public boolean importData(int taskid,String srcPath, String table,String parsetime, List<GlobalField> globalFields, Connection connection, String splitchar,String encode,String neType,String dataType) {
        boolean flag = true;
        PreparedStatement statement = null;
        BufferedReader srcReader = null;
        //记录表头
        String[] heads = null;
        String starttime = parsetime.substring(0,parsetime.lastIndexOf("-"))+" " + parsetime.substring(parsetime.lastIndexOf("-")+1)+":00:00";
        try {
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(getPrePareSql(table, globalFields));
            srcReader = new BufferedReader(new InputStreamReader(new FileInputStream(srcPath), encode));
            String line;
            int count = 0;
            while ((line = srcReader.readLine()) != null) {
                if(count ==0 ) { //第一行记录表头
                    count++;
                    heads =line.split(splitchar,-1);
                }else{
                    String[] ds = line.split(splitchar,-1);
                    HashMap<String,String> vmap = new HashMap();
                    for(int j=0;j<ds.length;j++){
                        vmap.put(heads[j],ds[j]);
                    }
                    int i = 0;
                    for (GlobalField globalField : globalFields) {
                        i++;
                        String value = vmap.get(globalField.getColumnName());
                        //假如是intid，设置为空
                        if(globalField.getColumnName().toLowerCase().contains("int")&&globalField.getColumnType().toLowerCase().contains("int")){
                            statement.setNull(i,Types.INTEGER);
                        }
                        //int类型的taskid字段
                        else if(globalField.getColumnName().toLowerCase().contains("task")&&globalField.getColumnType().toLowerCase().contains("int")){
                            statement.setInt(i,taskid);
                        }else if (globalField.getColumnType().toLowerCase().contains("double")) {
                            if(value==null || value.equals("")||value.equalsIgnoreCase("null"))
                                statement.setNull(i,Types.DOUBLE);
                            else
                                statement.setDouble(i, Double.parseDouble(formatData(value)));
                        } else {
                            //如果是网优平台或者话务网管的入库，单独处理资源字段，因资源的名称与数据库不一致
                            if(table.toLowerCase().contains("wypt")||table.toLowerCase().contains("hwwg")){
                                value = getwyptResValue(globalField,ds,neType,dataType,table);
                            }
                            //如果是ENB类型的数据，开始时间直接设置，因为在sql计算时取的是上一个15min数据
                            else if(table.toLowerCase().contains("enb") && (globalField.getColumnName().equalsIgnoreCase("TimeStamp")
                                    || globalField.getColumnName().equalsIgnoreCase("startTime"))){
                                value = starttime;
                            }

                            if(value==null || value.equals("")||value.equalsIgnoreCase("null")){
                                statement.setString(i, "");
                            }else{
                                statement.setString(i, value);
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
        }catch (Exception e){
            Log.error("导入文件失败："+srcPath,e);
            flag = false;
        }finally {
            try {
                if (srcReader != null) {
                    srcReader.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }catch (Exception e1){
                e1.printStackTrace();
            }

        }
        return flag;
    }

    /**
     * 处理小数点位数
     * @param value 原始数据
     * @return 保留三位小数
     */
    private String formatData(String value) {
        BigDecimal bd = new BigDecimal(Double.parseDouble(value));
        bd = bd.setScale(3, RoundingMode.HALF_UP);
        return bd.toString();
    }

    /**
     * 单独处理网优平台的资源字段
     * @param globalField 数据库映射字段信息
     * @param values 网优平台数据信息
     * @param neType 网元类型
     * @param dataType 数据类型
     * @param table 表名
     * @return 映射之后的资源值
     */
    private String getwyptResValue(GlobalField globalField,String[] values,String neType,String dataType,String table){
        String returnStr="";
        if(globalField.getColumnName().equalsIgnoreCase("province")){
            returnStr = values[4];
        }else if(globalField.getColumnName().equalsIgnoreCase("city")){
            returnStr = values[5];
        }else if(globalField.getColumnName().equalsIgnoreCase("TimeStamp")){
            returnStr = values[1];
        }else if(globalField.getColumnName().equalsIgnoreCase("TimeZone")){
            returnStr = "UTC+8";
        }else if(globalField.getColumnName().equalsIgnoreCase("Period")){
            returnStr = values[3];
        }else if(globalField.getColumnName().equalsIgnoreCase("VendorName")){
            returnStr = values[6];
        }else if(globalField.getColumnName().equalsIgnoreCase("ElementType")){
            returnStr = neType;
        }else if(globalField.getColumnName().equalsIgnoreCase("rmUID")){
            returnStr = values[0];
        }else if(globalField.getColumnName().equalsIgnoreCase("UserLabel")){
            if(table.toLowerCase().contains("wypt")){ //网优平台数据取下标7
                returnStr = values[7];
            }else{//话务网管取下标8
                returnStr = values[8];
            }

        }else if(globalField.getColumnName().equalsIgnoreCase("startTime")){
            returnStr = values[1];
        }else if(globalField.getColumnName().equalsIgnoreCase("ObjectType")){
            returnStr = dataType;
        }

        return returnStr;
    }


    /**
     * 动态创建入库语句 prepare，指定列名
     * @param table
     * @param globalFields
     * @return
     */
    public String getPrePareSql(String table, List<GlobalField> globalFields) {
        StringBuilder prepareSql = new StringBuilder(" insert into ").append(table).append(" (");
        for(int i=0;i<globalFields.size();i++){
            prepareSql.append(globalFields.get(i).getFactColumnname()).append(" ,");
        }
        prepareSql.deleteCharAt(prepareSql.length()-1).append(" ) values (");
        for (int i=0;i<globalFields.size();i++) {
            prepareSql.append(" ?,");
        }
        prepareSql.deleteCharAt(prepareSql.length() - 1);
        prepareSql.append(")");
        return prepareSql.toString();
    }

}
