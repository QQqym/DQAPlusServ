package com.cn.chinamobile.entity;

/**
 * @author zhou on 2017/10/30.
 * 记录UDP统计功能的各类计数
 */
public class UdpCount {

    //指标不存在的个数
    private int nexistnum = 0;

    //指标为空的个数
    private int nullnum = 0;

    //指标为0的个数
    private int zeronum = 0;

    //记录类型不符合的个数
    private int ntypenum = 0;


    //指标的总个数
    private int totalnum = 0;

    //为指标不存在的个数加1
    public void addNexistnum(){
        this.nexistnum++;
    }

    //为空的个数加1
    public void addNullnum(){
        this.nullnum++;
    }

    //为0的个数加1
    public void addZeronum(){
        this.zeronum++;
    }

    //总个数加1
    public void addTotalnum(){
        this.totalnum++;
    }
    //类型不符合加1
    public void addNtypenum(){
        this.ntypenum++;
    }

    public int getNexistnum() {
        return nexistnum;
    }

    public void setNexistnum(int nexistnum) {
        this.nexistnum = nexistnum;
    }

    public int getNullnum() {
        return nullnum;
    }

    public void setNullnum(int nullnum) {
        this.nullnum = nullnum;
    }

    public int getZeronum() {
        return zeronum;
    }

    public void setZeronum(int zeronum) {
        this.zeronum = zeronum;
    }

    public int getTotalnum() {
        return totalnum;
    }

    public void setTotalnum(int totalnum) {
        this.totalnum = totalnum;
    }

    public int getNtypenum() {
        return ntypenum;
    }

    public void setNtypenum(int ntypenum) {
        this.ntypenum = ntypenum;
    }

    /**
     * 返回通过的个数
     * @return 总数-不存在个数-空个数-0个数-类型个数
     */
    public int getPassNum(){
      return   this.totalnum-this.nexistnum-this.nullnum-this.zeronum-this.ntypenum;
    }
}
