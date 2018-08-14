package com.cn.chinamobile.business;

import com.cn.chinamobile.parse.UdpNrmParse;
import com.cn.chinamobile.service.MngdnSwversionService;
import com.cn.chinamobile.service.ProvinceService;
import com.cn.chinamobile.util.FileUtil;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xueweixia
 */
@Service
public class SwVersionBusiness {
    @Resource
    private ProvinceService provinceService;

    @Resource
    private MngdnSwversionService mngdnSwversionService;

    FileUtil fileUtil = new FileUtil();
    private PatternMatcher matcher = new Perl5Matcher();

    public void start(String timeindex){
        Map<String,String> provinceMap = provinceService.selectAll();
        for(String province : provinceMap.keySet()){
            String proUdpPath =  fileUtil.getProUdpPath(province,"NBI");
            String nepath = proUdpPath + File.separator + "ENB";
            List<String> vendors = fileUtil.findUdpDatePath(nepath,"");
            for(String vendor : vendors){
                String[] timeindexs = timeindex.split(",");
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
                    fileUtil.findGivenFiles(datapath,"managedelement",nrmfiles);
                    if(nrmfiles.size()>0){
                        UdpNrmParse udpNrmParse = new UdpNrmParse();
                        //解析获取软件版本
                        udpNrmParse.getNRMSwVersion(nrmfiles);
                        //入库该资源解析获取的DN和软件版本信息
                        Map<String,String> dnSwversion = udpNrmParse.getDnSwversionMap();

                        //从第一个文件名中获取北向版本
                        String regEx="(.*)(-V[0-9].*)";
                        String version = "";
                        //找到第一个带V的北向版本就结束
                        for(File file : nrmfiles){
                            if (fileUtil.match(file.getName(), regEx,matcher))
                            {
                                MatchResult mr = matcher.getMatch();
                                String matchstr = mr.group(2);
                                int endpos = matchstr.indexOf("-",1);
                                version = matchstr.substring(1, endpos);
                                break;
                            }
                        }

                        mngdnSwversionService.insertbatchDnSwVersion(province,vendor,"ENB",version,dnSwversion);

                    }
                }
            }
        }
    }

}
