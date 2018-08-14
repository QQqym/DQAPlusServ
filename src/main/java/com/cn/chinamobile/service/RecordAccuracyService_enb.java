package com.cn.chinamobile.service;

import com.cn.chinamobile.dao.mybatis.mapper.CounterpmEnbMapper;
import com.cn.chinamobile.dao.mybatis.mapper.NbipmEnbMapper;
import com.cn.chinamobile.dao.mybatis.mapper.Td_lteMapper;
import com.cn.chinamobile.pojo.mybatis.EnbCounterPm;
import com.cn.chinamobile.pojo.mybatis.EnbNbiPm;
import com.cn.chinamobile.pojo.mybatis.Td_lte_pm;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Auther: qiuyuming
 * @Date: 2018/7/12 09:20
 * @Description: 获取相应数据
 */
@Service
public class RecordAccuracyService_enb {

    @Resource
    private CounterpmEnbMapper counterpmEnbMapper;
    @Resource
    private NbipmEnbMapper nbipmEnbMapper;
    @Resource
    private Td_lteMapper td_lteMapper;

    /**
     *
     * 功能描述: 保存counter数据入Td_lte表
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/8/9 11:07
     */
    public void insertByEnbCounterPm(EnbCounterPm ecp,String dataStatus){
        td_lteMapper.insertByEnbCounterPm(ecp,dataStatus);
    }
    /**
     *
     * 功能描述: 保存nbi数据入Td_lte表
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/8/9 11:08
     */
    public void insertByEnbNbiPm(EnbNbiPm enp,String dataStatus){
        td_lteMapper.insertByEnbNbiPm(enp,dataStatus);
    }
    /**
     *
     * 功能描述: 保存计算数据入Td_lte表
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/8/9 11:10
     */
    public void insertByTdLtePm(List<String> tlp){
        td_lteMapper.insertByTdLtePm(tlp);
    }

    /**
     * 功能描述: 返回sum nbi结果
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/7/31 15:13
     */
    public EnbNbiPm selectSumNBIByProAndDate(String pm4,String pm2) {

        return nbipmEnbMapper.selectSumNBIByProAndDate(pm4,pm2);

    }

    /**
     *
     * 功能描述: 从临时表读取 nbi sum数据
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/8/13 14:40
     */
    public EnbNbiPm selectSumNBIByProAndDateFromTmp(String pm4,String pm2,String tableName) {

        return nbipmEnbMapper.selectSumNBIByProAndDateFromTmp(pm4,pm2,tableName);

    }


    /**
     * 功能描述: 返回 counter 结果
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/7/31 15:16
     */
    public EnbCounterPm selectSumCOUNTERByProAndDate(String pm4,String pm2) {

        return counterpmEnbMapper.selectSumCOUNTERByProAndDate(pm4,pm2);

    }

    /**
     *
     * 功能描述:从临时表读取counter sum 数据
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/8/13 14:39
     */
    public EnbCounterPm selectSumCOUNTERByProAndDateFromTmp(String pm4,String pm2,String tableName) {

        return counterpmEnbMapper.selectSumCOUNTERByProAndDateFromTmp(pm4,pm2,tableName);

    }



    /**
     * 功能描述: 查询共有小区详情
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/7/31 15:19
     */
    public List<EnbNbiPm> selectCommonNBIByProAndDate(String pm4,String pm2) {

        return nbipmEnbMapper.selectCommonNBIByProAndDate(pm4,pm2);

    }


    /**
     * 功能描述: 查询共有小区详情
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/7/31 15:19
     */
    public List<EnbCounterPm> selectCommonCOUNTERByProAndDate(String pm4,String pm2) {

        return counterpmEnbMapper.selectCommonCOUNTERByProAndDate(pm4,pm2);

    }

    /**
     *
     * 功能描述: 建立counter 临时表
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/8/13 15:36
     */
    public void createCounterTmpTabel(String sql){
        counterpmEnbMapper.createCounterTmpTabel(sql);
    }

    /**
     *
     * 功能描述:建立nbi 临时表
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/8/13 15:37
     */
    public void createNbiTmpTabel(String sql){
        nbipmEnbMapper.createNbiTmpTabel(sql);
    }
}
