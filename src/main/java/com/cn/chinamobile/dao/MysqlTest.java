package com.cn.chinamobile.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by zh on 2017/5/3.
 */
public class MysqlTest {
    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url="jdbc:mysql://localhost:3306/test?user=root&password=root";
            Connection con = DriverManager.getConnection(url);
            Statement stmt = con.createStatement();
            String createsql = "create temporary table temp_123456(id int,name varchar(32)) ENGINE=MEMORY ";
            stmt.execute(createsql);
            String insertsql = "insert into temp_123456 values(1,'lucy')";
            stmt.execute(insertsql);
            String query = "select * from temp_123456";
            ResultSet rs=stmt.executeQuery(query);
            while(rs.next()) {
                System.out.println(rs.getInt(1)+","+rs.getString(2));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
