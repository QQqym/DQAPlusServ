package com.cn.chinamobile.dao;

import com.cn.chinamobile.util.ContentInfo;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by zh on 2017/6/26.
 */
@Repository
public class DataDao {

    Connection con = null;
    /**
     * 获取数据连接
     * @return Connection 数据库连接
     */
    public Connection getConnection(){
        if(con != null){
            return con;
        }
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url= ContentInfo.MYSQL_DATA_URL;
            con = DriverManager.getConnection(url);
        }catch (Exception e){
            e.printStackTrace();
        }
        return  con;
    }
}
