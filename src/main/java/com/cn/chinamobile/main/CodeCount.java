package com.cn.chinamobile.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class CodeCount {
    static int count = 0;
    static int countFile = 0;
    static int filterFileAmount = 0;

    static String prefixArray[] = { ""};
    static String suffixArray[] = { ".java", ".xml" };
    static String rootDir = "D:\\sjyworkspace\\newtemplet\\DQAPlusServ\\src\\main";

    public static void main(String[] args) throws Exception {
        File dir = new File(rootDir);
        count(dir);
        System.out.println("代码总行数：" + count);
        System.out.println("代码总文件数：" + countFile);
        System.out.println("排除的文件数：" + filterFileAmount);
    }

    private static void count(File dir) throws Exception {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                count(file);
                continue;
            }

            if (verify(file)) {
                countFile++;

                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);

                String s;
                while ((s = br.readLine()) != null) {
                    s = s.trim();
                    if (!s.equals("")) {
                        //						if (!s.startsWith("package") && !s.startsWith("import")) {
                        count++;
                        //						}
                    }
                }
                br.close();
            } else {
                filterFileAmount++;
            }
        }
    }

    private static boolean verify(File file) {
        Boolean isAssignPrefix = false;
        for (String prefix : prefixArray) {
            if (file.getName().toLowerCase().contains(prefix)) {
                isAssignPrefix = true;
                break;
            }
        }
        if (isAssignPrefix.equals(false)) {
            return false;
        }
        for (String suffix : suffixArray) {
            if (file.getName().endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }
}