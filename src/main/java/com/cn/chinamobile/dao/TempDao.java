package com.cn.chinamobile.dao;

import com.cn.chinamobile.util.Log;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * Created by zh on 2017/6/13.
 * 临时中间数据库连接
 */
@Repository
public class TempDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public void exeSql(String sql){
        Log.info("execute sql ：" + sql);
        jdbcTemplate.execute(sql);
    }

}
