package com.cn.chinamobile.pojo.mybatis;

import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.service.DynamicSqlService;
import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.DataUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileCompleteWithBLOBs extends FileComplete {
    private String losepmdetail;

    private String nullpmdetail;

    private String nullpmnum;

    public FileCompleteWithBLOBs(Integer taskid, String subid, String filename, Long factlength, Long checklength, Integer pmnum, Integer losepmnum, Integer parsestatus, String failurereason, String losepmdetail, String nullpmdetail, String nullpmnum) {
        super(taskid, subid, filename, factlength, checklength, pmnum, losepmnum, parsestatus, failurereason);
        this.losepmdetail = losepmdetail;
        this.nullpmdetail = nullpmdetail;
        this.nullpmnum = nullpmnum;
    }

    public FileCompleteWithBLOBs() {
        super();
    }

    public String getLosepmdetail() {
        return losepmdetail;
    }

    public void setLosepmdetail(String losepmdetail) {
        this.losepmdetail = losepmdetail;
    }

    public String getNullpmdetail() {
        return nullpmdetail;
    }

    public void setNullpmdetail(String nullpmdetail) {
        this.nullpmdetail = nullpmdetail;
    }

    public String getNullpmnum() {
        return nullpmnum;
    }

    public void setNullpmnum(String nullpmnum) {
        this.nullpmnum = nullpmnum;
    }

    /**
     * 校验OMC文件的完整性
     * @param parsefile omc文件
     * @param encode 编码方式
     * @param prefix 前缀，omcfile_ 或者wyptfile_  或者 hwwgfile_
     * @param dataType 数据类型
     * @param vendorname 厂家名称
     * @param version 版本号
     * @return 文件完整性实体类
     */
    public FileCompleteWithBLOBs checkComPlete(File parsefile, String encode, String prefix, String dataType, String vendorname, String version, int taskid){
        FileCompleteWithBLOBs fileComplete = new FileCompleteWithBLOBs();
        DynamicSqlService dynamicSqlService = IniDomain.ct.getBean(DynamicSqlService.class);
        String[] metedata = dynamicSqlService.getMetaData(parsefile.getAbsolutePath(),encode);
        String[] titles = metedata[0].split(",");
        List<String> dataTitle = new ArrayList<>();
        for (String title : titles){
            dataTitle.add(title.trim());
        }
        fileComplete.setTaskid(taskid);
        fileComplete.setSubid(prefix+taskid);
        String checkFilePath = "";
        //查找titlecheck文件名，如果是EutranCellTdd通过厂家+版本查找，其他通过厂家查找
        if(prefix.contains("wypt")){ //如果是网优平台的文件，通过 wangyou 查找
            checkFilePath = IniDomain.titleconfig.get(dataType).get("wangyou");
        }else if(prefix.contains("hwwg")){ //如果是话务网管的文件，通过wangguan查找
            checkFilePath = IniDomain.titleconfig.get(dataType).get("wangguan");
        }else if(dataType.equalsIgnoreCase("EutranCellTdd")){ //如果是EutranCellTdd，通过厂家+版本号查找
            checkFilePath = IniDomain.titleconfig.get(dataType).get(vendorname+version);
        }else {//其他通过厂家查找
            checkFilePath = IniDomain.titleconfig.get(dataType).get(vendorname);
        }

        String checkpath = ContentInfo.DQA_VERSION_BASED_PATH+ File.separator+"titleCheck"+File.separator + checkFilePath;
        List<String> configTitle = new DataUtil().readTitle(checkpath);

        StringBuffer loseCounter = new StringBuffer();
        int lostCounternum = 0;

        for(String title : configTitle){
            if(!dataTitle.contains(title)){
                loseCounter.append(title).append(",");
                lostCounternum ++;
            }
        }

        if(loseCounter.length()>0){
            loseCounter.deleteCharAt(loseCounter.length()-1);
        }

        fileComplete.setLosepmnum(lostCounternum);
        fileComplete.setLosepmdetail(loseCounter.toString());

        return fileComplete;
    }
}