package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.FileCompleteMapper;
import com.cn.chinamobile.pojo.mybatis.FileCompleteWithBLOBs;
import com.cn.chinamobile.util.Log;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author xueweixia
 * 文件完整性业务类
 */
@Service
public class FileCompleteService {
    @Resource
    private FileCompleteMapper fileCompleteMapper;

    public int insert(FileCompleteWithBLOBs record){
        //补采的时候文件完整性入过的文件不再入库
        FileCompleteWithBLOBs result = fileCompleteMapper.selectByParas(record);
        if(result== null){
            try{
                int code = fileCompleteMapper.insert(record);
                return code;
            }catch (Exception e){
                Log.error("入库完整性记录失败",e);
                return 0;
            }

        }
       return 1;
    }


    public int insertSelective(FileCompleteWithBLOBs record){
        return fileCompleteMapper.insertSelective(record);
    }

}
