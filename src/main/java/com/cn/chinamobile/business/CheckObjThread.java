package com.cn.chinamobile.business;

import com.cn.chinamobile.parse.CheckDn;
import com.cn.chinamobile.util.GZIPUtil;

import java.io.File;
import java.util.Vector;

/**
 * @author xueweixia
 */
public class CheckObjThread implements Runnable{

    private File file;
    private Vector<String> datatypes;

    public CheckObjThread(File file,Vector<String> datatypes){
        this.file = file;
        this.datatypes = datatypes;
    }

    @Override
    public void run() {
        CheckDn checkDn = new CheckDn();
        //北向文件获取网元类型
        if(file.getName().contains(".gz")){
            File defile = new GZIPUtil().decompress(file);
            if(defile != null){
                checkDn.parseFile(defile,true,"objecttype",datatypes);
            }
        }else {
            checkDn.parseFile(file,false,"objecttype",datatypes);
        }
    }
}
