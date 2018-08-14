package com.cn.chinamobile.util;

import com.cn.chinamobile.entity.IndexConfig;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.*;

public class QciConfigUtil {

	/**
	 * 读取indexconfig.xml配置文件
	 * @param path 配置文件路径
	 * @return 配置文件信息
     */
	public static Map<String,IndexConfig> getIndexConfigMap(String path){
		Map<String,IndexConfig> indexConfigMap  = new CaseInsensitiveMap();

		try{
			SAXReader reader = new SAXReader();
			Document document = reader.read(new File(path));
			Element rootElm = document.getRootElement();
			for (Object o : rootElm.elements("datatype")) {
				Element ele = (Element)o;
				String datatype = ele.attributeValue("name");
				IndexConfig indexConfig = new IndexConfig();
				indexConfig.setOmckeyid(Integer.parseInt(ele.elementText("omckeyid")));
				indexConfig.setOmcvaluestart(Integer.parseInt(ele.elementText("omcvaluestart")));
				indexConfig.setBxkeyid(Integer.parseInt(ele.elementText("bxkeyid")));
				indexConfig.setBxvaluestart(Integer.parseInt(ele.elementText("bxvaluestart")));
				indexConfig.setWyptkeyid(Integer.parseInt(ele.elementText("wyptkeyid")));
				indexConfig.setWyptvaluestart(Integer.parseInt(ele.elementText("wyptvaluestart")));
				indexConfigMap.put(datatype,indexConfig);
			}

		}catch (Exception e){
			Log.error("read config error :"+path,e);
		}

		return  indexConfigMap;
	}
	
	public static List<String> getLeftPMS(String path){
		List<String> leftpms = null;
		try{
			File file=new File(path);
			if(!file.exists()||file.isDirectory()){
				 System.out.println("文件不存在或者为目录！");
			}else{
				InputStreamReader read = new InputStreamReader(new FileInputStream(file),"utf8"); 
				BufferedReader br=new BufferedReader(read);
				String temp=null;				
				temp=br.readLine();
				while(temp!=null){
					if (temp.indexOf("#pm") > -1){
						leftpms = new ArrayList<String>();
					}else if(temp.indexOf("#end pm") > -1){
						break;
					}else{
						if(leftpms!=null){
							leftpms.add(temp.trim());
						}
					}
					temp=br.readLine();
				}
				br.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}	
		return leftpms;
	}
	
	public static List<String> getLeftKPIS(String path){
		List<String> leftpms = null;
		try{
			File file=new File(path);
			if(!file.exists()||file.isDirectory()){
				 System.out.println("文件不存在或者为目录！");
			}else{
				InputStreamReader read = new InputStreamReader(new FileInputStream(file),"utf8"); 
				BufferedReader br=new BufferedReader(read);
				String temp=null;				
				temp=br.readLine();
				while(temp!=null){
					if (temp.indexOf("#kpi") > -1){
						leftpms = new ArrayList<String>();
					}else if(temp.indexOf("#end kpi") > -1){
						break;
					}else{
						if(leftpms!=null){
							leftpms.add(temp.trim());
						}
					}
					temp=br.readLine();
				}
				br.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}	
		return leftpms;
	}

	
	/**
	 * read pmtype config and contains pmtype description
	 */
	public static Map<String,List<String>> readQciConfig(String path){
		Map<String,List<String>> qciMap =new CaseInsensitiveMap();
		try{
			File file=new File(path);
			if(!file.exists()||file.isDirectory()){
				 System.out.println("文件不存在或者为目录！");
			}else{
				InputStreamReader read = new InputStreamReader(new FileInputStream(file),"utf8"); 
				BufferedReader br=new BufferedReader(read);
				String temp=null;				
				temp=br.readLine();
				String classname = null;
				while(temp!=null){
					if (temp.indexOf("class-") > -1){
						List<String> oneClass = new ArrayList<String>();
						//网元-子网元
						classname = temp.split("-")[2]+"-"+temp.split("-")[1];
						if (!qciMap.containsKey(classname)){
							qciMap.put(classname,oneClass);
						}
					}else{
						if(!temp.equals("")&&!temp.startsWith("#"))
						{
							qciMap.get(classname).add(temp.trim());
						}						
					}
					temp=br.readLine();
				}
				br.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}		
		return qciMap;
	} 

	/**
	 *  read pmtype config and contains pmtype description
	 */
	public static Map<String,List<String>> readQciConfigDesc(String path){
		Map<String,List<String>> qciMap = new LinkedHashMap<String,List<String>>();
		try{
			File file=new File(path);
			if(!file.exists()||file.isDirectory()){
				 System.out.println("文件不存在或者为目录！");
			}else{
				InputStreamReader read = new InputStreamReader(new FileInputStream(file),"utf8"); 
				BufferedReader br=new BufferedReader(read);
				String temp=null;				
				temp=br.readLine();
				String classname = null;
				while(temp!=null){
					if (temp.indexOf("class-") > -1){
						List<String> oneClass = new ArrayList<String>();
						int begin = temp.indexOf("class-")+6; //解决第一个隐藏符的问题
						classname = temp.trim().substring(begin, temp.length());
						if (!qciMap.containsKey(classname)){
							qciMap.put(classname,oneClass);	
						}
					}else{
						if(!temp.equals("")&&!temp.startsWith("#"))
						{
							qciMap.get(classname).add(temp.trim());
						}						
					}
					temp=br.readLine();
				}
				br.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}		
		return qciMap;
	} 
	
	/**
	 * read network contains kpi config
	 */
	public static Map<String,List<String>> readKPIConfig(String path){
		Map<String,List<String>> qciMap =new CaseInsensitiveMap();
		try{
			File file=new File(path);
			if(!file.exists()||file.isDirectory()){
				 System.out.println("文件不存在或者为目录！");
			}else{
				InputStreamReader read = new InputStreamReader(new FileInputStream(file),"utf8"); 
				BufferedReader br=new BufferedReader(read);
				String temp=null;				
				temp=br.readLine();
				String classname = null;
				while(temp!=null){
					if (temp.indexOf("class-") > -1){
						List<String> oneClass = new ArrayList<String>();
						classname = temp.split("-")[1];
						if (!qciMap.containsKey(classname)){
							qciMap.put(classname,oneClass);	
						}
					}else{
						if(!temp.equals("")&&!temp.startsWith("#"))
						{
							qciMap.get(classname).add(temp);
						}						
					}
					temp=br.readLine();
				}
				br.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}		
		return qciMap;
	} 
	
	
	public static Map<String,Map<String,String>> readTitleFileConfig(String path){
		Map<String,Map<String,String>> configMap = new LinkedHashMap<String,Map<String,String>>();
		try{
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader;
			InputStream inputStream = new FileInputStream(new File(path));   
			reader = inputFactory.createXMLStreamReader(inputStream,"utf8");
			Map<String,String> vendorversionfile = null;
			String vendorname = "";
			String pmtype = "";
			String verion = "";
			String wywg = "";
			boolean isENB = false ;
			while(reader.hasNext()){
			    int event = reader.getEventType();
			    if(event==XMLStreamConstants.START_ELEMENT){
			    	String localName = reader.getName().toString();
	    			if(localName.equals("PmType")){
	    				pmtype = reader.getAttributeValue(0);
	    				vendorversionfile = new LinkedHashMap<String,String>(); 
	    				if(pmtype.equals("EutranCellTdd")){
	    					isENB = true;
	    				}else{
	    					isENB = false;
	    				}
	    			}else if(localName.equals("vendorname")){
	    				vendorname = reader.getAttributeValue(0);
	    				if(!isENB){
	    					vendorversionfile.put(vendorname, reader.getAttributeValue(1));
	    				}
	    			}else if(localName.equals("WYWG")){
	    				wywg = reader.getAttributeValue(0);
	    				vendorversionfile.put(wywg, reader.getAttributeValue(1));
	    			}else if(localName.equals("pmversion")){
	    				verion = reader.getAttributeValue(0);
	    				vendorversionfile.put(vendorname+verion, reader.getAttributeValue(1));
	    			}
			    }else if(event==XMLStreamConstants.END_ELEMENT){
			    	String localName = reader.getName().toString();
	    			if(localName.equals("PmType")){
	    				configMap.put(pmtype, vendorversionfile);						   
	    			}
			    }
			    reader.next();
			}
		}catch (XMLStreamException | FileNotFoundException e) {
			e.printStackTrace();
		}
		return configMap;		
	}

	public static  Map<String,Map<String,String>> readTitle(String filepath){
		Map<String,Map<String,String>> configMap = new HashMap<>();
		XMLStreamReader reader = null;
		try{
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			InputStream inputStream = new FileInputStream(new File(filepath));
			reader = inputFactory.createXMLStreamReader(inputStream,"utf8");
			String pmtype = "";
			while(reader.hasNext()){
				int event = reader.next();
				if(event==XMLStreamConstants.START_ELEMENT){
					String localName = reader.getName().toString();
					if(localName.equals("PmType")){
						pmtype = reader.getAttributeValue(0);
						Map<String,String> vendormap = new HashMap<>();
						configMap.put(pmtype,vendormap);
					}else if(localName.equals("vendorname")){
						String vendorname = reader.getAttributeValue(0);
						String filename = reader.getAttributeValue(1);
						Map<String,String> vendormap = configMap.get(pmtype);
						vendormap.put(vendorname,filename);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(reader != null){
				try {
					reader.close();
				}catch (Exception e1){

				}
			}
		}
		return configMap;
	}

	public static  Map<String,Map<String,String>> readUdpConf(String filepath){
		Map<String,Map<String,String>> configMap = new CaseInsensitiveMap();;
		XMLStreamReader reader = null;
		try{
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			InputStream inputStream = new FileInputStream(new File(filepath));
			reader = inputFactory.createXMLStreamReader(inputStream,"utf8");
			String pmtype = "";
			while(reader.hasNext()){
				int event = reader.next();
				if(event==XMLStreamConstants.START_ELEMENT){
					String localName = reader.getName().toString();
					if(localName.equals("Filetype")){
						pmtype = reader.getAttributeValue(0);
						Map<String,String> vendormap = new CaseInsensitiveMap();
						configMap.put(pmtype,vendormap);
					}else if(localName.equals("pmversion")){
						String neversion = reader.getAttributeValue(0);
						String filename = reader.getAttributeValue(1);
						Map<String,String> vendormap = configMap.get(pmtype);
						vendormap.put(neversion,filename);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(reader != null){
				try {
					reader.close();
				}catch (Exception e1){

				}
			}
		}
		return configMap;
	}

	/**
	 * according the filepath to read the config of counter and kpi
	 */
	public static Map<String,Map<String,Map<String,String>>> readVendoraVersionCounterConfig(String path){
		Map<String,Map<String,Map<String,String>>> configMap =  new CaseInsensitiveMap();
		try{
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader;
			InputStream inputStream = new FileInputStream(new File(path));   
			reader = inputFactory.createXMLStreamReader(inputStream,"utf8");
			Map<String,String> versionfile = null;
			String vendorname = "";
			String pmtype = "";
			Map<String,Map<String,String>> vendorversionfile = null;
			while(reader.hasNext()){
			    int event = reader.getEventType();
			    if(event==XMLStreamConstants.START_ELEMENT){
			    	String localName = reader.getName().toString();
	    			if(localName.equals("VendorName")){
	    				vendorname = reader.getAttributeValue(0);
	    				if(vendorversionfile.containsKey(vendorname)){
	    					versionfile = vendorversionfile.get(vendorname);
	    				}else{
	    					versionfile = new HashMap<String,String>(); 
	    				}
	    			}else if(localName.equals("pmversion")){;
	    				versionfile.put(reader.getAttributeValue(0), reader.getAttributeValue(1));
	    			}else if(localName.equals("PmType")){
	    				pmtype = reader.getAttributeValue(0);
	    				if(configMap.containsKey(pmtype)){
	    					vendorversionfile = configMap.get(pmtype);
	    				}else{
	    					vendorversionfile = new HashMap<String,Map<String,String>>();
	    				}
	    			}
			    }else if(event==XMLStreamConstants.END_ELEMENT){
			    	String localName = reader.getName().toString();
	    			if(localName.equals("VendorName")){
	    				vendorversionfile.put(vendorname, versionfile);						   
	    			}else if(localName.endsWith("PmType")){
						pmtype = pmtype.split("-")[0];
	    				configMap.put(pmtype, vendorversionfile);	
	    			}
			    }
			    reader.next();
			}
		}catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return configMap;		
	} 
	public static Map<String,Map<String,Map<String,String>>> readVendoraVersionKPIConfig(String path){
		Map<String,Map<String,Map<String,String>>> configMap = new CaseInsensitiveMap();
		try{
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader;
			InputStream inputStream = new FileInputStream(new File(path));   
			reader = inputFactory.createXMLStreamReader(inputStream);
			Map<String,String> versionfile = null;
			String vendorname = "";
			String pmtype = "";
			Map<String,Map<String,String>> vendorversionfile = null;
			while(reader.hasNext()){
			    int event = reader.getEventType();
			    if(event==XMLStreamConstants.START_ELEMENT){
			    	String localName = reader.getName().toString();
	    			if(localName.equals("VendorName")){
	    				vendorname = reader.getAttributeValue(0);
	    				if(vendorversionfile.containsKey(vendorname)){
	    					versionfile = vendorversionfile.get(vendorname);
	    				}else{
	    					versionfile = new HashMap<String,String>(); 
	    				}
	    			}else if(localName.equals("pmversion")){;
	    				versionfile.put(reader.getAttributeValue(0), reader.getAttributeValue(1));
	    			}else if(localName.equals("PmType")){
	    				pmtype = reader.getAttributeValue(0);
	    				if(configMap.containsKey(pmtype)){
	    					vendorversionfile = configMap.get(pmtype);
	    				}else{
	    					vendorversionfile = new HashMap<String,Map<String,String>>();
	    				}
	    			}
			    }else if(event==XMLStreamConstants.END_ELEMENT){
			    	String localName = reader.getName().toString();
	    			if(localName.equals("VendorName")){
	    				vendorversionfile.put(vendorname, versionfile);						   
	    			}else if(localName.endsWith("PmType")){
						pmtype = pmtype.split("-")[0];
	    				configMap.put(pmtype, vendorversionfile);	
	    			}
			    }
			    reader.next();
			}
		}catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return configMap;			
	} 
	
	public static Map<String,String> getSUMAlgorithmMap(String path){
		Map<String,String> configMap = new HashMap<String,String>();
		try{
			InputStream is=new FileInputStream(new File(path));   
			BufferedReader br=new BufferedReader(new InputStreamReader(is,"utf8"));
			String temp=null;				
			temp=br.readLine();
			while(temp!=null){	
				if(!temp.equals("")){
					String[] pmAlgorithm = temp.split("#");	
					configMap.put(pmAlgorithm[0].trim(), pmAlgorithm[1].trim());
				}					
				temp=br.readLine();
			}
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}		
		return configMap;		
	}
	
	
	public static Map<String,String> readPmDescFileConfig(String path){
		Map<String,String> configMap = new LinkedHashMap<String,String>();
		try{
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader;
			InputStream inputStream = new FileInputStream(new File(path));   
			reader = inputFactory.createXMLStreamReader(inputStream,"utf8");
			String pmtype = "";
			String file = "";
			while(reader.hasNext()){
			    int event = reader.getEventType();
			    if(event==XMLStreamConstants.START_ELEMENT){
			    	String localName = reader.getName().toString();
	    			if(localName.equals("PmType")){
	    				pmtype = reader.getAttributeValue(0);
	    				file = reader.getAttributeValue(1);
	    				configMap.put(pmtype, file);
	    			}
			    }
			    reader.next();
			}
		}catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return configMap;		
	} 
	
	
	public static Map<String,List<String>> getPMvALUESMap(String path){
		Map<String,List<String>> configMap = new HashMap<String,List<String>>();
		try{
			InputStream is= new FileInputStream(new File(path));   
			BufferedReader br=new BufferedReader(new InputStreamReader(is,"utf8"));
			String temp=null;				
			temp=br.readLine();
			while(temp!=null){	
				if(!temp.equals("")){
					List<String> values = new ArrayList<String>();
					String[] pmAlgorithm = temp.split("#");	
					values.add(pmAlgorithm[0].trim());
					values.add(pmAlgorithm[1].trim());
					values.add(pmAlgorithm[2].trim());
					values.add(pmAlgorithm[3].trim());
					configMap.put(pmAlgorithm[2].trim(),values);
				}					
				temp=br.readLine();
			}
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}		
		return configMap;		
	}
	

	public static Map<String,Map<String,String>> readCouterFileConfig(String file){
		Map<String,Map<String,String>> configMap = new LinkedHashMap<String,Map<String,String>>();
		try{
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader;
			InputStream inputStream =  new FileInputStream(file);   
			reader = inputFactory.createXMLStreamReader(inputStream);
			Map<String,String> versionfile = null;
			String vendorname = "";
			while(reader.hasNext()){
			    int event = reader.getEventType();
			    if(event==XMLStreamConstants.START_ELEMENT){
			    	String localName = reader.getName().toString();
	    			if(localName.equals("VendorName")){
	    				vendorname = reader.getAttributeValue(0);
	    				versionfile = new LinkedHashMap<String,String>();  						   
	    			}else if(localName.equals("pmversion")){;
	    				versionfile.put(reader.getAttributeValue(0), reader.getAttributeValue(1));
	    			}
			    }else if(event==XMLStreamConstants.END_ELEMENT){
			    	String localName = reader.getName().toString();
	    			if(localName.equals("VendorName")){
	    				configMap.put(vendorname, versionfile);						   
	    			}
			    }
			    reader.next();
			}
		}catch (XMLStreamException | FileNotFoundException e) {
			e.printStackTrace();
		}
		return configMap;		
	} 
}