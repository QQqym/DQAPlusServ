package com.cn.chinamobile.dao;

import com.cn.chinamobile.pojo.mybatis.GlobalField;
import com.cn.chinamobile.service.DynamicImportService;
import com.cn.chinamobile.service.GlobalFieldService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.Connection;
import java.util.List;

/**
 * Created by zh on 2017/6/26.
 */

@Repository
public class ImportBXData {
    @Resource
    private GlobalFieldService globalFieldService;

    @Resource
    private DynamicImportService dynamicImportService;

    @Resource
    private DataDao dataDao;

    Connection conn;

    /**
     * 正式数据入库
     * @param filename 文件名
     * @param tableprefix 表前缀
     * @param parsetime 解析时间
     * @param taskid 任务id
     * @param splitchar 分割符
     * @param encode 编码格式
     * @param neType 网元类型
     * @param dataType 数据类型
     */
   public boolean importData(String filename,String tableprefix,String parsetime,int taskid,String splitchar,String encode,String neType,String dataType){
        conn = dataDao.getConnection();
        List<GlobalField> globalFieldList = getField(tableprefix,neType,dataType);
       //根据taskid删除数据
       dynamicImportService.deleteBytaskid(taskid,globalFieldList,conn);

       String tablename = globalFieldList.get(0).getTableName();
       //将数据导入数据库
       boolean flag = dynamicImportService.importData(taskid,filename,tablename,parsetime,globalFieldList,conn,splitchar,encode,neType,dataType);
       return flag;
    }

    public void closeConnection(){
        try {
            if(null != conn)
                conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    List<GlobalField> getField(String tableName,String netype,String datatype){
        return globalFieldService.selectByparas(tableName,netype,datatype);
    }

}
