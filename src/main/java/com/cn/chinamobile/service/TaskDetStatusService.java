package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.TaskDetStatusMapper;
import com.cn.chinamobile.pojo.mybatis.TaskDetStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xueweixia
 */
@Service
public class TaskDetStatusService{

    @Resource
    private TaskDetStatusMapper taskDetStatusMapper;

   public int insert(TaskDetStatus record){
       return taskDetStatusMapper.insert(record);
    }

   public TaskDetStatus selectByParas(TaskDetStatus record){
       Map map = new HashMap();
       map.put("subtask",record.getSubtask());
       map.put("province",record.getProvince());
       map.put("netype",record.getNetype());
       map.put("vendor",record.getVendor());
       map.put("version",record.getVersion());
       map.put("datatype",record.getDatatype());
       map.put("datetime",record.getDatetime());
       return taskDetStatusMapper.selectByParas(map);
   }

    public int updateByTaskSubId(TaskDetStatus record){
        return taskDetStatusMapper.updateByTaskSubId(record);
    }

}
