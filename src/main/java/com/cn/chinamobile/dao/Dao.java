package com.cn.chinamobile.dao;

import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.Log;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

/**
 * Created by zh on 2017/5/10.
 */
@Repository
public class Dao {

    Connection con = null;

    /**
     * 获取数据连接
     * @return Connection
     */
    public Connection getConnection(){
        try{
            if(con != null){
                return con;
            }
            Class.forName("com.mysql.jdbc.Driver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url= ContentInfo.MYSQL_TEMP_URL;
            con = DriverManager.getConnection(url);
        }catch (Exception e){
            Log.error("链接数据库失败",e);
        }
        return  con;
    }

    /**
     * 执行sql
     * @param sql 要执行的sql语句
     */
    public void exeSql(String sql) {
        Statement stmt = null;
        try {
            Connection conn = getConnection();
            stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConn(stmt,null);
        }
    }

    /**
     * 执行结果
     * @param sql
     * @return
     */
    public List<Map<String ,String>> getResultSet(String sql) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List reList = new ArrayList();
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                Map map = new LinkedHashMap();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    String columnName = rsmd.getColumnName(i).toLowerCase();
                    String columnValue = rs.getString(rsmd.getColumnName(i));
                    if (columnValue == null) {
                        columnValue = "";
                    }
                    map.put(columnName, columnValue);
                }
                reList.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConn(stmt,rs);
        }
        return reList;
    }

    /**
     * 执行结果
     * @param sql
     * @return
     */
    public List<Map<String ,String>> getResultSetMap(String sql,List<String> pms) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Map<String ,String>> reList = new ArrayList();
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            Log.info("执行sql:"+sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                Map map = new LinkedHashMap();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    String columnName = pms.get(i-1);
                    String rr=rsmd.getColumnName(i);
                    String columnValue = rs.getString(rsmd.getColumnName(i));
                    if (columnValue == null) {
                        columnValue = "";
                    }
                    map.put(columnName, columnValue);
                }
                reList.add(map);
            }
        } catch (SQLException e) {
            Log.error("执行sql异常:"+sql,e);
            e.printStackTrace();
        } finally {
            closeConn(stmt,rs);
        }
        return reList;
    }

    private void closeConn(Statement stmt,ResultSet rs){
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭Connection
     */
    public void closeConnect(){
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
