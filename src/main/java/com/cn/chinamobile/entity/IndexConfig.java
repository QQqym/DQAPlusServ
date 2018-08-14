package com.cn.chinamobile.entity;

/**
 * Created by zh on 2017/6/26.
 * 记录OMC与北向key的下标，与数据开始下标
 */
public class IndexConfig {
    //omckey字段的下标
    private int omckeyid;
    private int omcvaluestart;
    private int bxkeyid;
    private int bxvaluestart;
    private int wyptkeyid;
    private int wyptvaluestart;

    public int getOmckeyid() {
        return omckeyid;
    }

    public void setOmckeyid(int omckeyid) {
        this.omckeyid = omckeyid;
    }

    public int getOmcvaluestart() {
        return omcvaluestart;
    }

    public void setOmcvaluestart(int omcvaluestart) {
        this.omcvaluestart = omcvaluestart;
    }

    public int getBxkeyid() {
        return bxkeyid;
    }

    public void setBxkeyid(int bxkeyid) {
        this.bxkeyid = bxkeyid;
    }

    public int getBxvaluestart() {
        return bxvaluestart;
    }

    public void setBxvaluestart(int bxvaluestart) {
        this.bxvaluestart = bxvaluestart;
    }

    public int getWyptkeyid() {
        return wyptkeyid;
    }

    public void setWyptkeyid(int wyptkeyid) {
        this.wyptkeyid = wyptkeyid;
    }

    public int getWyptvaluestart() {
        return wyptvaluestart;
    }

    public void setWyptvaluestart(int wyptvaluestart) {
        this.wyptvaluestart = wyptvaluestart;
    }
}
