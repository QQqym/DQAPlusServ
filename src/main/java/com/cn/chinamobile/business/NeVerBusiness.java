package com.cn.chinamobile.business;

import com.cn.chinamobile.pojo.mybatis.UdpAll;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.service.DynamicSqlService;
import com.cn.chinamobile.service.ProvinceService;
import com.cn.chinamobile.service.UdpAllService;
import com.cn.chinamobile.util.*;
import org.apache.oro.text.regex.*;
import org.mozilla.intl.chardet.nsDetector;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author xueweixia
 */
@Service
public class NeVerBusiness {
    @Resource
    private ProvinceService provinceService;

    @Resource
    private UdpAllService udpAllService;

    FileUtil fileUtil = new FileUtil();

    private PatternMatcher matcher = new Perl5Matcher();

     public void start(String filetype,String timeindex){
        Map<String,String> provinceMap = provinceService.selectAll();
        IniDomain.initCheck();
        for(String province : provinceMap.keySet()){
            String proUdpPath = fileUtil.getProUdpPath(province,filetype);
            List<String> netypes = fileUtil.findUdpDatePath(proUdpPath,"");
            for(String netype : netypes){
                String index = netype.toLowerCase()+"-";
                //MSC特殊处理
                if(netype.equalsIgnoreCase("SCSCF")){
                    index = "CSCF-";
                }else if(netype.equalsIgnoreCase("HDRA") || netype.equalsIgnoreCase("LDRA")){
                    index = "DRA-";
                }else if(netype.equalsIgnoreCase("MSC")){
                    index = "MSS-";
                }else if (netype.equalsIgnoreCase("PSBC")){
                    index = "VOLTESBC-";
                }

                String nepath = proUdpPath + File.separator + netype;
                List<String> vendors = fileUtil.findUdpDatePath(nepath,"");
                for(String vendor : vendors){
                    String[] timeindexs = timeindex.split(",");
                    if(filetype.equalsIgnoreCase("PCT")){
                        String pctpath = nepath + File.separator + vendor;
                        List<String> versions = fileUtil.findUdpDatePath(pctpath,"");
                        for(String version : versions){
                            List<String> filterdates = new ArrayList<>(64);
                            String timespath = pctpath + File.separator + version;
                            for(String tindex : timeindexs){
                                List<String> nrmdates = fileUtil.findUdpDatePath(timespath,tindex);
                                filterdates.addAll(nrmdates);
                            }

                            for(String datetime : filterdates){
                                //删除已存的数据
                                udpAllService.deletebyparas(province,filetype,netype,vendor,version,"PM",datetime);
                                String filepath = timespath + File.separator + datetime;
                                List<File> pctfiles = new ArrayList<>();
                                fileUtil.findAllFiles(filepath,pctfiles);
                                String fvalue = dealPctFiles(pctfiles,netype,vendor);
                                UdpAll udpAll = new UdpAll(province,filetype,netype,vendor,version,"PM",datetime,"",fvalue);
                                udpAllService.insert(udpAll);
                            }
                        }

                    }else {
                        List<String> filterdates = new ArrayList<>(64);
                        //处理NRM数据
                        String nrmPath = nepath + File.separator + vendor + File.separator+"NRM";

                        for(String tindex : timeindexs){
                            List<String> nrmdates = fileUtil.findUdpDatePath(nrmPath,tindex);
                            filterdates.addAll(nrmdates);
                        }

                        for(String datetime : filterdates){
                            String datapath = nrmPath + File.separator + datetime;
                            List<File> nrmfiles = new ArrayList<>(1024);
                            fileUtil.findGivenFiles(datapath,index.toLowerCase(),nrmfiles);
                            dealfiles(province,filetype,netype,vendor,"NRM",datetime,nrmfiles);
                        }


                        //处理PM数据
                        filterdates.clear();
                        String pmPath = nepath + File.separator + vendor + File.separator+"PM";
                        for(String tindex : timeindexs){
                            List<String> pmdates = fileUtil.findUdpDatePath(pmPath,tindex);
                            filterdates.addAll(pmdates);
                        }

                        for(String datetime : filterdates) {
                            String datapath = pmPath + File.separator + datetime;
                            List<File> pmfiles = new ArrayList<>(1024);
                            fileUtil.findGivenFiles(datapath,index.toLowerCase(),pmfiles);
                            dealfiles(province,filetype,netype,vendor,"PM",datetime,pmfiles);
                        }
                    }
                }
            }
        }
    }

    /**
     * 处理PCT文件模板是否正确
     * @param pctfils PCT文件
     * @param netype 网元类型
     * @param vendor 厂家
     * @return 文件表头是否符合模板
     */
    private String dealPctFiles(List<File> pctfils ,String netype,String vendor){
        boolean flag = true;
        if(pctfils.size()==0)
            return "no file";

        if(netype.contains("CSCF"))
            netype="SCSCF";

        String checkpath = "";
        String[] checktitle;
        DynamicSqlService dynamicSqlService = new DynamicSqlService();
        try{
            checkpath = ContentInfo.DQA_VERSION_BASED_PATH+ File.separator+"title"+File.separator
                    +  IniDomain.title.get(netype).get(vendor);
            String checkencode = new FileCharsetDetector().guessFileEncoding(new File(checkpath), new nsDetector());
            checktitle = dynamicSqlService.getMetaData(checkpath,checkencode)[0].split(",");
        }catch (Exception e){
            Log.error("获取title文件失败：",e);
            return "title error";
        }

        StringBuffer losepm = new StringBuffer();
        for(File file : pctfils){
            File parsefile = null;
            //假如是压缩文件，先解压
            if(file.getName().endsWith(".gz")){
                parsefile  = new GZIPUtil().decompress(file);
            }else{
                parsefile = file;
            }

            try{
                String encode = new FileCharsetDetector().guessFileEncoding(parsefile, new nsDetector());
                String[] metedata = dynamicSqlService.getMetaData(parsefile.getAbsolutePath(),encode);
                String[] titles = metedata[0].split(",");
                List<String> dataTitle = new ArrayList<>();
                for (String title : titles){
                    dataTitle.add(title.trim());
                }

                for(String title : checktitle){
                    if(!dataTitle.contains(title.trim())){
                        flag = false;
                        losepm.append(title.trim()).append(",");
                    }
                }

            }catch (Exception e){
                flag = false;
            }

            if(file.getName().endsWith(".gz") && parsefile !=null){
                parsefile.delete();
            }

            if(!flag)
                break;
        }
        if(!flag){
            if(losepm.length()>0)
                losepm = losepm.deleteCharAt(losepm.length()-1);
            return losepm.toString();
        }else {
            return  "yes";
        }
    }

    /**
     * 处理各省、网元、厂家、类型的文件
     * @param province 省份
     * @param filetype 文件类型 NBI/UDP/PCT
     * @param netype 网元类型
     * @param vendor 厂家
     * @param type 信息模型（NRM/PM）
     * @param datetime 时间
     * @param files 文件
     */
    private void dealfiles(String province,String filetype,String netype,String vendor,String type,String datetime,List<File> files){
        Set<String> versionSet = new HashSet<>();
        //若没有文件直接返回
        if(files.size()==0){
            return;
        }

        for(File file : files){
            String filename = file.getName();
            String regEx="(.*)(-V[0-9].*)";
            if (fileUtil.match(filename, regEx,matcher))
            {
                MatchResult mr = matcher.getMatch();
                String matchstr = mr.group(2);
                int endpos = matchstr.indexOf("-",1);
                String version = matchstr.substring(1, endpos);

                //处理版本信息，如果没记录，放入set
                if(!versionSet.contains(version)){
                    versionSet.add(version);
                }
            }
        }



        Vector<String> datatypes = new Vector<>();
        if(filetype.equalsIgnoreCase("nbi")){
            //定义线程池
            ExecutorService threadPool = Executors.newFixedThreadPool(10);

            for(File file : files){
                CheckObjThread checkObjThread = new CheckObjThread(file,datatypes);
                threadPool.submit(checkObjThread);
            }
            threadPool.shutdown();

            //等待线程池结束
            try {
                while (!threadPool.awaitTermination(10, TimeUnit.SECONDS));
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
            //北向存子网元类型
            for(String version : versionSet) {
                //删除已存的数据
                udpAllService.deletebyparas(province,filetype,netype,vendor,version,type,datetime);
                for (String datatype : datatypes) {
                    UdpAll udpAll = new UdpAll(province, filetype, netype, vendor, version, type, datetime, datatype,"");
                    udpAllService.insert(udpAll);
                }
            }
        }else {
            for(String version : versionSet){
                //删除已存的数据
                udpAllService.deletebyparas(province,filetype,netype,vendor,version,type,datetime);

                UdpAll udpAll = new UdpAll(province,filetype,netype,vendor,version,type,datetime,"","");
                udpAllService.insert(udpAll);
            }
        }


    }


}
