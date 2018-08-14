package com.cn.chinamobile.pojo.mybatis;

/**
 * @Auther: qiuyuming
 * @Date: 2018/7/12 15:57
 * @Description:
 */
public class VenderInformation {

    //省份
    private String pm2;
    //厂家
    private String pm7;
    //网元
    private String pm8;
    //版本
    private String pm9;

    public String getPm2() {
        return pm2;
    }

    public String getPm7() {
        return pm7;
    }

    public String getPm8() {
        return pm8;
    }

    public String getPm9() {
        return pm9;
    }

    public void setPm2(String pm2) {
        this.pm2 = pm2;
    }

    public void setPm7(String pm7) {
        this.pm7 = pm7;
    }

    public void setPm8(String pm8) {
        this.pm8 = pm8;
    }

    public void setPm9(String pm9) {
        this.pm9 = pm9;
    }

    public String toString(){
        return pm2+","+pm7+","+pm8+","+pm9;
    }
}
