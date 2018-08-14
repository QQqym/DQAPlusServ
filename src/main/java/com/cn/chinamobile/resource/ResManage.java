package com.cn.chinamobile.resource;

import com.cn.chinamobile.redis.JedisUtils;
import com.cn.chinamobile.util.ContentInfo;

/**
 * Created by zh on 2017/4/25.
 */
public class ResManage {

    public static void initAllRedisTables() {
        long l = System.currentTimeMillis();
        JedisUtils.initRedisTable("PM-QCI", ContentInfo.RESOURCE_FILE_PATH + "/PM-QCI-config.txt");
        System.out.printf("cost %d second\n",(System.currentTimeMillis() - l) / 1000);
    }
}
