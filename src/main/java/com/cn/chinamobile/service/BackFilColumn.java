package com.cn.chinamobile.service;

import com.cn.chinamobile.entity.IndexConfig;
import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.Log;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zh on 2017/6/26.
 * 北向与counter互相回填资源
 */
public class BackFilColumn {

    private ProvinceService provinceService;
    private VendorService vendorService;
    private Map<String,String> provinceMap;
    private Map<String,String> vendorMap ;

    String vendorName = "";
    String elementType = "";
    String pmVersion = "";
    String objectType = "";
    String province = "";

    public BackFilColumn() {
        super();
    }

    public BackFilColumn(String province,String vendorName, String elementType, String pmVersion, String objectType) {
        super();
        this.province = province;
        this.vendorName = vendorName;
        this.elementType = elementType;
        this.pmVersion = pmVersion;
        this.objectType = objectType;
    }

    public void initProvinceVendor(){
        provinceService = IniDomain.ct.getBean(ProvinceService.class);
        vendorService = IniDomain.ct.getBean(VendorService.class);
        provinceMap = provinceService.selectAll();
        vendorMap = vendorService.selectAll();
    }

    public void getPubFileFromOmc(Map<String, PubFieldVo> pubFieldVoMap, String omcFile,int keyid) {
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(omcFile), ContentInfo.ENCODING));
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String values[] = line.split("\\|", -1);
                String key = values[keyid];
                PubFieldVo pubFieldVo = pubFieldVoMap.get(key);
                if (pubFieldVo == null) {
                    pubFieldVo = new PubFieldVo();
                    pubFieldVoMap.put(key, pubFieldVo);
                }
                pubFieldVo.setTimestamp(values[1]);
                pubFieldVo.setStarttime(values[1]);
                pubFieldVo.setProvince(provinceMap.get(province));
                pubFieldVo.setCity(values[5]);
                pubFieldVo.setRmuid(values[0]);
                pubFieldVo.setVendorname(vendorMap.get(vendorName));
                pubFieldVo.setUserlabel(values[keyid]);
            }
            br.close();
        }catch (Exception e){
            Log.error("回填OMC信息失败!",e);
        }

    }

    public void getPubFileFromBx(Map<String, PubFieldVo> pubFieldVoMap, String bxFile,int keyid)
            throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(bxFile), ContentInfo.ENCODING));
        String line = br.readLine();
        while ((line = br.readLine()) != null) {
            String values[] = line.split("\\|", -1);
            String key = values[keyid];
            PubFieldVo pubFieldVo = pubFieldVoMap.get(key);
            if (pubFieldVo == null) {
                pubFieldVo = new PubFieldVo();
                pubFieldVoMap.put(key, pubFieldVo);
                //在PCT中没有该小区，厂家，版本等信息从北向取
                pubFieldVo.setStarttime(values[0]);
                pubFieldVo.setProvince(provinceMap.get(province));
                pubFieldVo.setVendorname(vendorMap.get(vendorName));

                pubFieldVo.setUserlabel(values[keyid]);
            }
            pubFieldVo.setDn(values[2]);
            pubFieldVo.setTimestamp(values[0]);

            if(objectType.equalsIgnoreCase("EutranCellTdd"))
                pubFieldVo.setSwVersion(values[9]);
        }
        br.close();
    }

    public void beginCompare(String omcFile, String bxFile) throws IOException {
        Map<String, PubFieldVo> pubFieldVoMap = new HashMap<String, PubFieldVo>(1024);
        IndexConfig indexConfig = IniDomain.indexConfigHashMap.get(objectType);
        getPubFileFromOmc(pubFieldVoMap, omcFile,indexConfig.getOmckeyid());
        getPubFileFromBx(pubFieldVoMap, bxFile,indexConfig.getBxkeyid());
        outputFile(pubFieldVoMap, indexConfig.getOmckeyid(), indexConfig.getOmcvaluestart(), omcFile);
        //在写北向时改变了地市信息，一定要先写出OMC再写北向数据
        outputFile(pubFieldVoMap, indexConfig.getBxkeyid(), indexConfig.getBxvaluestart(), bxFile);
    }

    public void outputFile(Map<String, PubFieldVo> pubFieldVoMap, int keyIndex, int bizFileIndex,
                           String inputFile){
        BufferedReader br = null;
        BufferedWriter writer = null;
        try {
            File input = new File(inputFile);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(input), ContentInfo.ENCODING));
            writer =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(inputFile + "_dest",false),ContentInfo.ENCODING));
            int i = 1;
            String line;
            int cityindex = 0;
            while ((line = br.readLine()) != null) {
                String values[] = line.split("\\|", -1);
                if (i == 1) {
                    writer.write(getPubFieldsHeader());
                    //如果是北向文件，记录city的下标
                    if(input.getName().startsWith("bx")){
                        for(int index=0;index<values.length;index++){
                            if(values[index].equalsIgnoreCase("city")){
                                cityindex = index;
                                break;
                            }
                        }
                    }
                } else {
                    //如果是北向文件，将city信息设置为北向取到的地市信息
                    if(input.getName().startsWith("bx")){
                        pubFieldVoMap.get(values[keyIndex]).setCity(values[cityindex]);
                    }
                    writer.write(pubFieldVoMap.get(values[keyIndex]).getPubFieldValue());
                }
                writer.write(concatArray(bizFileIndex, values));
                writer.newLine();
                i++;
            }
        } catch (Exception e){
//            e.printStackTrace();
        } finally{
            try{
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
                if (br != null) {
                    br.close();
                }
            }catch (Exception e1){

            }

        }
    }

    private String concatArray(int index, String[] strings) {
        StringBuilder builder = new StringBuilder();
        for (int i = index; i < strings.length; i++) {
            builder.append(strings[i]).append("|");
        }
        return builder.deleteCharAt(builder.length() - 1).toString();
    }


    private String getPubFieldsHeader() {
        return "INT_ID|province|city|TimeStamp|TimeZone|Period|VendorName|ElementType|PmVersion|rmUID|Dn|UserLabel|startTime|ObjectType|swVersion|";
    }

    private class PubFieldVo {

        private Integer intid;

        private String province;

        private String city;

        private String timestamp;

        private String vendorname;

        private String rmuid;

        private String dn;

        private String userlabel;

        private String starttime;

        private String swVersion;


        public Integer getIntid() {
            return intid;
        }

        public void setIntid(Integer intid) {
            this.intid = intid;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getVendorname() {
            return vendorname;
        }

        public void setVendorname(String vendorname) {
            this.vendorname = vendorname;
        }

        public String getRmuid() {
            return rmuid;
        }

        public void setRmuid(String rmuid) {
            this.rmuid = rmuid;
        }

        public String getDn() {
            return dn;
        }

        public void setDn(String dn) {
            this.dn = dn;
        }

        public String getUserlabel() {
            return userlabel;
        }

        public void setUserlabel(String userlabel) {
            this.userlabel = userlabel;
        }

        public String getStarttime() {
            return starttime;
        }

        public void setStarttime(String starttime) {
            this.starttime = starttime;
        }

        public String getSwVersion() {
            return swVersion;
        }

        public void setSwVersion(String swVersion) {
            this.swVersion = swVersion;
        }

        public String getPubFieldValue() {
            StringBuilder valueBuilder = new StringBuilder();
            return valueBuilder.append(getIntid()).append("|")
                    .append(getProvince()).append("|")
                    .append(getCity()).append("|")
                    .append(getTimestamp()).append("|")
                    .append("UTC+8").append("|")
                    .append(60).append("|")
                    .append(getVendorname()).append("|")
                    .append(elementType).append("|")
                    .append(pmVersion).append("|")
                    .append(getRmuid()).append("|")
                    .append(getDn()).append("|")
                    .append(getUserlabel()).append("|")
                    .append(getStarttime()).append("|")
                    .append(objectType).append("|")
                    .append(swVersion).append("|").toString();
        }

    }
}
