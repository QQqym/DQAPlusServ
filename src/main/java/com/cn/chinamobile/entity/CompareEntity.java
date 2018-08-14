package com.cn.chinamobile.entity;

import java.io.File;

/**
 * Created by zh on 2017/6/25.
 * 记录北向、网优平台、Counter对比的内容
 */
public class CompareEntity {

    private File pmfile;

    private File kpiFile;

    public CompareEntity(File pm,File kpi){
        this.pmfile = pm;
        this.kpiFile = kpi;
    }

    public File getPmfile() {
        return pmfile;
    }

    public void setPmfile(File pmfile) {
        this.pmfile = pmfile;
    }

    public File getKpiFile() {
        return kpiFile;
    }

    public void setKpiFile(File kpiFile) {
        this.kpiFile = kpiFile;
    }
}
