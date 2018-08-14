package com.cn.chinamobile.compare;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import com.cn.chinamobile.resource.IniDomain;
import com.cn.chinamobile.util.ContentInfo;
import com.cn.chinamobile.util.FileCharsetDetector;
import com.cn.chinamobile.util.Log;
import org.mozilla.intl.chardet.nsDetector;

/**
 * Created by zh on 2017/5/11.
 */
public class Compare {
	
	String vendorName = "";
	String elementType = "";
	String pmVersion = "";
	String objectType = "";
	String provinceCHName = "";

	public Compare(String vendorName,String elementType ,String pmVersion ,String objectType,String provinceCHName){
		super();
		this.vendorName = vendorName;
		this.elementType = elementType;
		this.pmVersion = pmVersion;
		this.objectType = objectType;
		this.provinceCHName = provinceCHName;
	}


	/**
	 * 两个文件通过关键字段比较
	 * @param file1 北向文件
	 * @param file2 omc文件
	 * @param outPath 比较结果输出路径
	 * @throws Exception 比较异常
     */
	public void compare2File(File file1, File file2, String outPath)
			throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file1), ContentInfo.ENCODING));
		String line = br.readLine();
		int key = IniDomain.indexConfigHashMap.get(objectType).getBxkeyid();
		String[] pmnames = line.split("\\|", -1);
		int pmcount = pmnames.length;
		Map<String, Map<String, String>> bxmap = new HashMap<String, Map<String, String>>();
		while ((line = br.readLine()) != null) {
			String values[] = line.split("\\|", -1);
			Map<String, String> dataMap = new HashMap<String, String>();
			for (int i = 0; i < pmcount; i++) {
				dataMap.put(pmnames[i], values[i]);
			}
			bxmap.put(values[key], dataMap);
		}
		br.close();
		compare(bxmap, file2, outPath);
	}


	private void compare(Map<String, Map<String, String>> bxmap,
			File inFile, String outPath) throws Exception {
		String encode = ContentInfo.ENCODING;
		String splitchar = "\\|";
		int comparekey = IniDomain.indexConfigHashMap.get(objectType).getOmckeyid();;
		//如果是北向与话务网管或者网优平台的对比，检测文件编码，且,分割
		if(outPath.toLowerCase().contains("wypt")||outPath.toLowerCase().contains("hwwg")){
			encode = new FileCharsetDetector().guessFileEncoding(inFile, new nsDetector());
			splitchar = ",";
		}
		//网优平台的keyid 是7
		if(outPath.toLowerCase().contains("wypt")){
			comparekey = IniDomain.indexConfigHashMap.get(objectType).getWyptkeyid();
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), encode));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPath,false),ContentInfo.ENCODING));

		String line = br.readLine();
		String[] pmnames =  line.split(splitchar, -1);
		int pmcount = pmnames.length;
		//网优平台或者话务网管文件最后一位是,的情况，列个数减1
		if(line.endsWith(","))
			pmcount = pmcount-1;
		while ((line = br.readLine()) != null) {
			String values[] = line.split(splitchar, -1);
			Map<String, String> dataMap = new HashMap<String, String>();
			for (int i = 0; i < pmcount; i++) {
				dataMap.put(pmnames[i], values[i]);
			}

			if (bxmap.containsKey(values[comparekey])) {
				begincompare(bxmap.get(values[comparekey]), dataMap, bw,outPath);
			}
		}
		br.close();
		bw.flush();
		bw.close();
	}
	
	private void begincompare(Map<String, String> bxmap,
			Map<String, String> omcmap, BufferedWriter bw, String outPath) {
		//以北向为基准对比
		Iterator<String> ite = bxmap.keySet().iterator();
		String bxvalue ="" ;
		String bxkey = "" ;
		String omcvalue = "";
		try{
			while (ite.hasNext()) {
				bxkey = ite.next();
				bxvalue = bxmap.get(bxkey);
				//如果是业务字段，且OMC计算了该指标，是PM指标名或者KPI指标名，进行对比
				if ((IniDomain.sumlgorithmMap.containsKey(bxkey)
						|| IniDomain.kpiconfigMap.get(objectType).contains(bxkey))
						&& omcmap.containsKey(bxkey)) {
					omcvalue= omcmap.get(bxkey)==null? null:omcmap.get(bxkey).trim();
					BigDecimal diff ;
					BigDecimal diff1 ;
					//OMC值判空
					if(omcvalue==null || omcvalue.equalsIgnoreCase("null") || omcvalue.equalsIgnoreCase("")){
						omcvalue = "";
						if(bxvalue==null || bxvalue.equalsIgnoreCase("null") || bxvalue.equalsIgnoreCase("")){
							bxvalue = "";
							continue;
						}else {
							diff = new BigDecimal(bxvalue);
							diff1 = new BigDecimal(bxvalue);
						}
					}else if(bxvalue==null || bxvalue.equalsIgnoreCase("null") || bxvalue.equalsIgnoreCase("")){
						bxvalue = "";
						diff = new BigDecimal(omcvalue);
						diff1 = new BigDecimal(omcvalue);
					}
					else{
						diff = new BigDecimal(omcvalue).subtract(new BigDecimal(bxvalue));
						//将北向值乘以100，处理网优和话务率值问题
						diff1 = new BigDecimal(omcvalue).subtract(new BigDecimal(bxvalue).multiply(new BigDecimal(100)));
					}


					if(Math.abs(diff.doubleValue()) > 0.01 && Math.abs(diff1.doubleValue()) > 0.01){
						String output ="|"+provinceCHName+"|"+omcmap.get("地市")+"|"  +bxmap.get("begintime")+ "|UTC+8|60|" + this.vendorName
								+ "|" + this.elementType + "|" + this.pmVersion + "|" + omcmap.get("rmUID") + "|" + bxmap.get("Dn")
								+ "|" + bxmap.get("userlabel")+"|"+bxmap.get("begintime")+"|"+ this.objectType
								+ "|" + bxkey + "|" + bxvalue+"|"+omcvalue+"|"+diff.toPlainString();
						//如果是ENB北向与PCT，回填软件版本
						if(outPath.toLowerCase().contains("log_nbi_counter_enb")){
							output = output + "|" + bxmap.get(ContentInfo.COUNTER_SWVERION);
						}
						bw.write(output);
						bw.newLine();
					}
				}
			}
		}catch (Exception e){
			Log.error("bxkey,bxvalue,omcvalue="+bxkey+","+bxvalue+","+omcvalue,e);
		}

	}
}