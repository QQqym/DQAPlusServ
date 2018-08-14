package com.cn.chinamobile.util;


import java.io.*;
import java.util.*;

public class DataUtil {

	/**
	 * 读取算法配置文件
	 * @param path 算法文件路径
	 * @return 指标名,算法
     */
	public Map<String, String> getExpressionMap(String path) {
		Map<String, String> algorithmMap = new LinkedHashMap<String, String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)),"utf8"));
			String temp =  br.readLine();
			while (temp != null) {
				if (!temp.equals("")) {
					String[] pmAlgorithm = temp.split("#");
					algorithmMap.put(pmAlgorithm[0], pmAlgorithm[1]);
				}
				temp = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(null != br){
				try {
					br.close();
				}catch (Exception e1){
					e1.printStackTrace();
				}

			}
		}
		return algorithmMap;
	}

	/**
	 * 获取区分软件版本的算法
	 * @param path 算法文件路径
	 * @return 分软件版本的算法配置，PM项，软件版本，算法
	 */
	public Map<String, Map<String,String>> getSwverExpresMap(String path) {
		Map<String, Map<String,String>> algorithmMap = new LinkedHashMap();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)),"utf8"));
			String temp =  br.readLine();
			while (temp != null) {
				if (!temp.equals("")) {
					String[] pmAlgorithm = temp.split("#");
					Map<String,String> swmap = new LinkedHashMap<>();
					//如果不包含该指标项，放入
					if(!algorithmMap.containsKey(pmAlgorithm[0])){
						algorithmMap.put(pmAlgorithm[0],swmap);
					}else {
						swmap = algorithmMap.get(pmAlgorithm[0]);
					}
					swmap.put(pmAlgorithm[1], pmAlgorithm[2]);
				}
				temp = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(null != br){
				try {
					br.close();
				}catch (Exception e1){
					e1.printStackTrace();
				}

			}
		}
		return algorithmMap;
	}

	/**
	 * 读取title配置文件
	 * @param path title文件的路径
	 * @return	指标list
     */
	public List<String> readTitle(String path){
		List<String> configTile = new ArrayList<>(30);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "utf8"));
			String temp = null;
			while((temp = br.readLine()) !=null ){
				configTile.add(temp.trim());
			}
		}catch (Exception e){
			Log.error("读取title配置文件失败："+path,e);
		}finally {
			if(null != br){
				try {
					br.close();
				}catch (Exception e1){
					e1.printStackTrace();
				}

			}
		}
		return configTile;
	}

	/**
	 * 读取check文件
	 * @param path check文件的路径
	 * @param encode 编码方式
	 * @return <文件名,文件大小>
     */
	public Map<String,Long> readCheckFile(String path,String encode){
		Map<String,Long> checkmap = new HashMap<>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)),encode));
			String temp = null;
			while((temp = br.readLine()) !=null ){
				String[] data = temp.split("\\s+");
				checkmap.put(data[0],Long.parseLong(data[1]));
			}
		}catch (Exception e){
			Log.error("读取check文件失败："+path,e);
		}finally {
			if(null != br){
				try {
					br.close();
				}catch (Exception e1){
					e1.printStackTrace();
				}

			}
		}

		return checkmap;
	}
}
