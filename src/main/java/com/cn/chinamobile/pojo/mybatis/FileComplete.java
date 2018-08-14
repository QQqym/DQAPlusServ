package com.cn.chinamobile.pojo.mybatis;

public class FileComplete {
    private Integer taskid;

    private String subid;

    private String filename;

    private Long factlength;

    private Long checklength;

    private Integer pmnum;

    private Integer losepmnum;

    private Integer parsestatus;

    private String failurereason;

    public FileComplete(Integer taskid, String subid, String filename, Long factlength, Long checklength, Integer pmnum, Integer losepmnum, Integer parsestatus, String failurereason) {
        this.taskid = taskid;
        this.subid = subid;
        this.filename = filename;
        this.factlength = factlength;
        this.checklength = checklength;
        this.pmnum = pmnum;
        this.losepmnum = losepmnum;
        this.parsestatus = parsestatus;
        this.failurereason = failurereason;
    }

    public FileComplete() {
        super();
    }

    public Integer getTaskid() {
        return taskid;
    }

    public void setTaskid(Integer taskid) {
        this.taskid = taskid;
    }

    public String getSubid() {
        return subid;
    }

    public void setSubid(String subid) {
        this.subid = subid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getFactlength() {
        return factlength;
    }

    public void setFactlength(Long factlength) {
        this.factlength = factlength;
    }

    public Long getChecklength() {
        return checklength;
    }

    public void setChecklength(Long checklength) {
        this.checklength = checklength;
    }

    public Integer getPmnum() {
        return pmnum;
    }

    public void setPmnum(Integer pmnum) {
        this.pmnum = pmnum;
    }

    public Integer getLosepmnum() {
        return losepmnum;
    }

    public void setLosepmnum(Integer losepmnum) {
        this.losepmnum = losepmnum;
    }

    public Integer getParsestatus() {
        return parsestatus;
    }

    public void setParsestatus(Integer parsestatus) {
        this.parsestatus = parsestatus;
    }

    public String getFailurereason() {
        return failurereason;
    }

    public void setFailurereason(String failurereason) {
        this.failurereason = failurereason;
    }
}