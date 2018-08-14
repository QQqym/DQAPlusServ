package com.cn.chinamobile.util;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

/**
 * @author xueweixia
 */
public class ExcelUtil {


    //产生表格需要类
    private int num = 0;
    //用于输出excel文件
    private FileInputStream fileInputStream;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    /**
     * 功能描述: 用于产生既定的XSSFWorkbook
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/6/29 10:55
     */
    public void createXSSFWorkbook(String path, String sheetName, String[] columns) {
        try {
            File excel = new File(path);
            //判断文件是否已经存在
            if (excel.exists()) {
                fileInputStream = new FileInputStream(excel);
                workbook = new XSSFWorkbook(fileInputStream);
                sheet = workbook.getSheetAt(workbook.getSheetIndex(sheetName));
                num = sheet.getLastRowNum() + 1;
            } else {
                //如果文件为空 直接生成文件插入数据
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet(sheetName);
                sheet.setDefaultColumnWidth((short) 25);//设置所有单元格宽度
                //存放列信息
                XSSFRow row0 = sheet.createRow(0);
                row0.setHeightInPoints(20);
                for (int i = 0; i < columns.length; i++) {
                    row0.createCell(i).setCellValue(columns[i]);
                }
                num = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能描述:用于生成TDTLE使用表格
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/8/3 9:59
     */
    public void createXSSFWorkbookTDLTE(String sheetName, List<String> times) {

        try {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet(sheetName);
            sheet.setDefaultColumnWidth((short) 17);//设置所有单元格宽度
            //合并单元格
            CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 0, 30);
            sheet.addMergedRegion(cellRangeAddress);
            //存放列信息
            XSSFRow row0 = sheet.createRow(0);
            row0.setHeightInPoints(40);
            //设置格式
            row0.createCell(0).setCellValue("无线网设备性能测量数据准确性测试数据记录表(北向接口映射算法一致性)-ENB");
            //创建第一行的样式
            XSSFCellStyle style0 = (XSSFCellStyle) workbook.createCellStyle();
            //设置第一行字体
            Font font0 = workbook.createFont();
            font0.setFontHeightInPoints((short) 20);
            font0.setFontName("黑体");
            //设置字体
            style0.setFont(font0);
            row0.getCell(0).setCellStyle(style0);

            //第二行样式处理
            XSSFRow row1 = sheet.createRow(1);
            //当两个时间点以上在合并单元格
            if(times.size()>=2) {
                CellRangeAddress range0 = new CellRangeAddress(1, 1, 6, 5 + times.size());
                CellRangeAddress range1 = new CellRangeAddress(1, 1, 6 + times.size(), 5 + times.size() + times.size());
                CellRangeAddress range2 = new CellRangeAddress(1, 1, 6 + times.size() + times.size(), 5 + times.size() + times.size() + times.size());
                sheet.addMergedRegion(range0);
                sheet.addMergedRegion(range1);
                sheet.addMergedRegion(range2);
            }
            row1.createCell(6).setCellValue("nbi");
            row1.createCell(6 + times.size()).setCellValue("counter");
            //样式设计
            XSSFCellStyle style1 = (XSSFCellStyle) workbook.createCellStyle();
            //设置第二行字体
            Font font1 = workbook.createFont();
            font1.setFontHeightInPoints((short) 16);
            font1.setFontName("宋体");
            //设置字体
            style1.setFont(font1);
            row1.getCell(6).setCellStyle(style1);
            row1.getCell(6+times.size()).setCellStyle(style1);

            //第三行样式处理
            XSSFRow row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("序号");
            row2.createCell(1).setCellValue("省份");
            row2.createCell(2).setCellValue("厂家");
            row2.createCell(3).setCellValue("北向接口版本");
            row2.createCell(4).setCellValue("英文名称");
            row2.createCell(5).setCellValue("中文名称");
            int num0 = 5;
            int num1 = 5 + times.size();
            int num2 = 5 + times.size() + times.size();
            for (int i = 0; i < times.size(); i++) {
                row2.createCell(num0 = num0 + 1).setCellValue(times.get(i));
                row2.createCell(num1 = num1 + 1).setCellValue(times.get(i));
                row2.createCell(num2 = num2 + 1).setCellValue(times.get(i));
            }
            row2.createCell(num2 = num2 + 1).setCellValue("不准确的测试项PM样点数量");
            row2.createCell(num2 = num2 + 1).setCellValue("总测试的PM样点数量");
            row2.createCell(num2 = num2 + 1).setCellValue("单测量项准确率");
            row2.createCell(num2 = num2 + 1).setCellValue("备注");

            //样式设计
            XSSFCellStyle style2 = (XSSFCellStyle) workbook.createCellStyle();
            //设置第二行字体
            Font font2 = workbook.createFont();
            font2.setFontHeightInPoints((short) 13);
            font2.setFontName("宋体");
            //设置字体
            style2.setFont(font2);
            row2.getCell(0).setCellStyle(style2);
            row2.getCell(1).setCellStyle(style2);
            row2.getCell(2).setCellStyle(style2);
            row2.getCell(3).setCellStyle(style2);
            row2.getCell(4).setCellStyle(style2);
            row2.getCell(5).setCellStyle(style2);
            row2.getCell(6+times.size()*3).setCellStyle(style2);
            row2.getCell(7+times.size()*3).setCellStyle(style2);
            row2.getCell(8+times.size()*3).setCellStyle(style2);
            row2.getCell(9+times.size()*3).setCellStyle(style2);

            String EXCEL_COLUMN="conf/excel_column.properties";
            Properties EXCEL_COLUMN_PROP = ConfigUtils.getConf(EXCEL_COLUMN);
            String cloumnNum=EXCEL_COLUMN_PROP.getProperty("columnNum");
            int cn=Integer.parseInt(cloumnNum);

            //将固定列值插入表格
            for (int i = 0; i < cn; i++) {
                XSSFRow row = sheet.createRow(i + 3);
                row.createCell(4).setCellValue(EXCEL_COLUMN_PROP.getProperty("pm"+(i+1)));
                row.createCell(5).setCellValue(EXCEL_COLUMN_PROP.getProperty("ch_pm"+(i+1)));
            }
            num = 3;

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * @param :保存数据
     * @TODO: 给列表行赋值
     */
    public void createRowData(String... args) throws Exception {
        XSSFRow row = this.sheet.createRow(num);
        for (int i = 0; i < args.length; i++) {
            row.createCell(i).setCellValue(args[i]);
        }
        num++;
    }

    /**
     *
     * 功能描述: 在随意一个文本框赋值
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/8/6 16:09
     */
    public void createExcelData(int rowNum,int cellNum,String data) throws Exception{
        XSSFRow row = this.sheet.createRow(rowNum);
        row.createCell(cellNum).setCellValue(data);
    }

    public void setExcelData(int rowNum,int cellNum,String data)throws Exception{
        XSSFRow row = this.sheet.getRow(rowNum);
        row.createCell(cellNum).setCellValue(data);
    }

    /**
     * 功能描述: 输出产生的既定XSSFWorkbook
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/6/29 11:01
     */
    public void writeXSSFExcel(String path) throws Exception {

        File file = new File(path);//Excel文件生成后存储的位置。
        OutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param srcPath
     * @param encode
     * @param taskid
     * @param path
     * @param sheetName
     * @TODO: 将不满足时间点的数据写入excel
     */
    public Map<String, ArrayList<String>> handlePartTimeDataExcel(String srcPath, String encode, int taskid, String path, String sheetName, String provice) throws Exception {
        BufferedReader srcReader = null;
        String line = "";
        //利用map记录每次的userlabel 个数
        Map<String, ArrayList<String>> userLabelMap = new HashMap<String, ArrayList<String>>();
        try {
            //读取
            srcReader = new BufferedReader(new InputStreamReader(new FileInputStream(srcPath), "UTF-8"));
            int count = 0;
            String header[] = null;
            while ((line = srcReader.readLine()) != null) {

                if (count == 0) { //第一行记录表头,列名
                    count++;
                    continue;
                } else {//获取值

                    String[] ds = line.split(",");
                    //获取需要数据
                    //开始时间
                    String startTime = ds[1].trim();
                    //结束时间
                    String endTime = ds[2].trim();
                    //userlabel
                    String userlabel = ds[9].trim();
                    //verder
                    String verder = ds[6].trim();
                    //数据按照taskid,startTime,endTime,userlabel的形式拼字符串便于解析
                    String recordData = taskid + "," + startTime + "," + endTime + "," + userlabel + "," + verder;
                    //判断集合是否存在key
                    boolean userLabelBoolean = userLabelMap.containsKey(userlabel);
                    //遍历获取所有需要信息
                    if (userLabelBoolean) {
                        //如果存在这个userLabel说明之前有数据
                        ArrayList<String> arrayList = userLabelMap.get(userlabel);
                        arrayList.add(recordData);
                        userLabelMap.put(userlabel, arrayList);
                    } else {
                        ArrayList<String> arrayList = new ArrayList<String>();
                        arrayList.add(recordData);
                        userLabelMap.put(userlabel, arrayList);
                    }
                }
            }
            FileInputStream fileInputStream;
            ExcelUtil excelUtil = new ExcelUtil();
            //文件是否存在拿出来判断
            File excel = new File(path);
            if (excel.exists()) {
                fileInputStream = new FileInputStream(excel);
                XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
                XSSFSheet sheet = workbook.getSheetAt(workbook.getSheetIndex(sheetName));
                int num = sheet.getLastRowNum() + 1;
                for (Map.Entry<String, ArrayList<String>> entry : userLabelMap.entrySet()) {
                    //说明数据时间点不全
                    if (entry.getValue().size() < 5) {
                        ArrayList<String> datas = entry.getValue();
                        for (int i = 0; i < entry.getValue().size(); i++) {
                            XSSFRow row = sheet.createRow(num);
                            String[] dataArray = datas.get(i).split(",");
                            //存放taskid
                            row.createCell(0).setCellValue(dataArray[0]);
                            //存放startTime
                            row.createCell(1).setCellValue(dataArray[1]);
                            //存放endTime
                            row.createCell(2).setCellValue(dataArray[2]);
                            //存放userlabel
                            row.createCell(3).setCellValue(dataArray[3]);
                            //verder
                            row.createCell(4).setCellValue(dataArray[4]);
                            //存放省市信息
                            row.createCell(5).setCellValue(provice);

                            num++;
                        }

                    }
                }
                excelUtil.writeExcel(workbook, path);
            } else {
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = workbook.createSheet(sheetName);
                sheet.setDefaultColumnWidth((short) 30);//设置所有单元格宽度
                //存放列信息
                XSSFRow row0 = sheet.createRow(0);
                row0.setHeightInPoints(20);
                //存放taskid
                row0.createCell(0).setCellValue("taskid");
                //存放startTime
                row0.createCell(1).setCellValue("startTime");
                //存放endTime
                row0.createCell(2).setCellValue("endTime");
                //存放userlabel
                row0.createCell(3).setCellValue("userlabel");
                //存放verder
                row0.createCell(4).setCellValue("vender");
                //存放provice
                row0.createCell(5).setCellValue("provice");
                int num = 1;
                for (Map.Entry<String, ArrayList<String>> entry : userLabelMap.entrySet()) {
                    //说明数据时间点不全
                    if (entry.getValue().size() < 5) {
                        ArrayList<String> datas = entry.getValue();
                        for (int i = 0; i < datas.size(); i++) {
                            String[] dataArray = datas.get(i).split(",");
                            XSSFRow row = sheet.createRow(num);
                            //存放taskid
                            row.createCell(0).setCellValue(dataArray[0]);
                            //存放startTime
                            row.createCell(1).setCellValue(dataArray[1]);
                            //存放endTime
                            row.createCell(2).setCellValue(dataArray[2]);
                            //存放userlabel
                            row.createCell(3).setCellValue(dataArray[3]);
                            //verder
                            row.createCell(4).setCellValue(dataArray[4]);
                            //存放省市信息
                            row.createCell(5).setCellValue(provice);
                            num++;
                        }
                    }
                }
                excelUtil.writeExcel(workbook, path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (srcReader != null) {
                    srcReader.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return userLabelMap;
    }

    /**
     * 功能描述: 判断文件夹是否存在
     *
     * @param:
     * @return:
     * @auther: qiuyuming
     * @date: 2018/6/29 16:41
     */
    public static void creatDirectory(String path) {
        File file = new File(path);
        //如果文件夹不存在则创建
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    /**
     * 读取excel数据
     *
     * @param path
     */
    public void readExcelToObj(String path) {

        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(new File(path));
            readExcel(wb, 0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取excel文件
     *
     * @param wb
     * @param sheetIndex    sheet页下标：从0开始
     * @param startReadLine 开始读取的行:从0开始
     * @param tailLine      去除最后读取的行
     */
    private void readExcel(Workbook wb, int sheetIndex, int startReadLine, int tailLine) {
        Sheet sheet = wb.getSheetAt(sheetIndex);

        Row row = null;

        for (int i = startReadLine; i < sheet.getLastRowNum() - tailLine + 1; i++) {
            row = sheet.getRow(i);
            int cnum = 0;
            while (row != null && cnum < row.getLastCellNum()) {
                Cell c = row.getCell(cnum);

                MergeCellInfo isMerge = isMergedRegion(sheet, i, c.getColumnIndex());
                //判断是否具有合并单元格
                if (isMerge != null) {
                    String rs = getMergedRegionValue(sheet, row.getRowNum(), c.getColumnIndex());

                    // 处理列 ，必须重复读取合并单元格的值
                    cnum = isMerge.getLastColumn() + 1;
                } else {
                    String value = getCellValue(c);

                    //列加1
                    cnum += 1;
                }
            }
            System.out.println();
        }

    }

    /**
     * 读取excel文件
     *
     * @param filepath
     * @param indexname sheet页名称
     */
    public Map<String, String> readNrmIndex(String filepath, String indexname) {
        Map<String, String> datamap = new HashMap<>();
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(new File(filepath));
            Sheet sheet = wb.getSheet(indexname);

            Row row = null;
            for (int i = 0; i < sheet.getLastRowNum(); i++) {
                row = sheet.getRow(i);
                int cnum = 0;
                List<String> values = new ArrayList<>();
                while (row != null && cnum < row.getLastCellNum()) {
                    Cell c = row.getCell(cnum);
                    String value = getCellValue(c);
                    if (i > 0) {
                        values.add(value);
                    }
                    //列加1
                    cnum += 1;
                }
                if (i > 0) {
                    datamap.put(values.get(0), values.get(1));
                }

            }
        } catch (Exception e) {
            Log.error("read norm error:" + filepath, e);
        }

        return datamap;
    }


    /**
     * 写规范支持性文件
     *
     * @param src         规范模板
     * @param dest        目标规范文件
     * @param filetype    文件类型
     * @param excepresult 不支持结果集
     * @param datamap     nrm sheet页与子网元对应关系
     * @param swversions  软件版本
     */
    public void writeNormSupport(String src, String dest, String filetype, Set<String> excepresult, Map<String, String> datamap, String swversions) {
        File destfile = new File(dest);
        if (!destfile.getParentFile().exists())
            destfile.getParentFile().mkdirs();

        if (destfile.exists())
            destfile.delete();

        try {
            Workbook wb = WorkbookFactory.create(new File(src));
            Workbook newwb = new XSSFWorkbook();
            ;
            int sheetNum = wb.getNumberOfSheets();
            for (int snum = 0; snum < sheetNum; snum++) {
                Sheet sheet = wb.getSheetAt(snum);
                String sname = wb.getSheetName(snum);
                Sheet sheetCreat = newwb.createSheet(sname);
                sheetCreat.setForceFormulaRecalculation(true);
                // 复制源表中的合并单元格
                MergerRegion(sheetCreat, sheet);
                int firstRow = sheet.getFirstRowNum();
                int lastRow = sheet.getLastRowNum();

                //记录英文名称、支持时间、空间粒度的下标
                int pmnameIndex = -1;
                int supportIndex = -1;
                int datatypeIndex = -1;

                //记录指标名、子网元名
                String pmname = "";
                String datetype = "";

                for (int i = firstRow; i <= lastRow; i++) {
                    // 创建新建excel Sheet的行
                    Row rowCreat = sheetCreat.createRow(i);
                    // 取得源有excel Sheet的行
                    Row row = sheet.getRow(i);

                    //空行不处理
                    if (row != null) {
                        // 单元格式样
                        int firstCell = row.getFirstCellNum();
                        int lastCell = row.getLastCellNum();

                        //处理只有最后一列样式的情况
                        if (firstCell > pmnameIndex)
                            pmname = "";

                        for (int j = firstCell; j < lastCell; j++) {
                            Cell oldcell = row.getCell(j);
                            Cell newcell = rowCreat.createCell(j);

                            //设置cell样式
                            if (oldcell != null) {
                                CellStyle style = newcell.getRow().getSheet().getWorkbook().createCellStyle();
                                style.cloneStyleFrom(oldcell.getCellStyle());
                                newcell.setCellStyle(style);
                            }


                            //从规范sheet页开始
                            if (snum >= 3) {
                                String cellvalue = getCellValue(oldcell);
                                //第一行，获取值判断下标
                                if (i == firstRow) {

                                    if (cellvalue.equalsIgnoreCase("英文名称"))
                                        pmnameIndex = j;
                                    if (cellvalue.equalsIgnoreCase("支持时间"))
                                        supportIndex = j;
                                    if (cellvalue.equalsIgnoreCase("空间粒度"))
                                        datatypeIndex = j;
                                } else { //第二行开始，获取指标名
                                    if (j == pmnameIndex)
                                        pmname = cellvalue;

                                    if (filetype.equalsIgnoreCase("pm") && j == datatypeIndex)
                                        datetype = cellvalue;
                                }


                            }

                            //第一页写软件版本
                            if (snum == 0 && i == 3 && j == 4) {
                                newcell.setCellValue(swversions);
                            }
                            //规范页的最后一列，写支持性
                            else if (snum >= 3 && j == supportIndex) {
                                if (filetype.equalsIgnoreCase("nrm"))
                                    datetype = datamap.get(sname);

                                if (excepresult.contains(datetype + "-" + pmname) && !pmname.equals("")) {
                                    newcell.setCellValue("NS");
                                } else if (!pmname.equals("")) {
                                    newcell.setCellValue("T");
                                }

                            } else { //其他原样输出
                                if (oldcell != null) {
                                    setNewCell(oldcell, newcell);
                                }
                            }

                        }
                    }

                }
            }
            writeExcel(newwb, dest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 　 * 复制原有sheet的合并单元格到新创建的sheet
     * 　 *
     * 　 * @param sheetCreat
     * 　 *　　　　　 新创建sheet
     * 　 * @param sheet
     * 　 *　　　　　 原有的sheet
     */
    private void MergerRegion(Sheet sheetCreat, Sheet sheet) {
        int sheetMergerCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergerCount; i++) {
            CellRangeAddress mergedRegionAt = sheet.getMergedRegion(i);
            sheetCreat.addMergedRegion(mergedRegionAt);
        }
    }

    public String removeInternalBlank(String s) {
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(s);
        char str[] = s.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length; i++) {
            if (str[i] == ' ') {
                sb.append(' ');
            } else {
                break;
            }
        }
        String after = m.replaceAll("");
        return sb.toString() + after;
    }


    public void writeExcel(Workbook wb, String path) throws Exception {

        File file = new File(path);//Excel文件生成后存储的位置。
        OutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            wb.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void copy(File src, File dst) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(src), 1024);
            out = new BufferedOutputStream(new FileOutputStream(dst), 1024);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            Log.error("copy excel error :" + src.getAbsolutePath(), e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }

                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {

            }

        }
    }

    /**
     * 获取合并单元格的值
     *
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    public String getMergedRegionValue(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();

        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();

            if (row >= firstRow && row <= lastRow && column >= firstColumn && column <= lastColumn) {
                if (row == firstRow && column == firstColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell);
                } else {
                    return "";
                }

            }
        }

        return null;
    }

    /**
     * 判断合并了行
     *
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    private boolean isMergedRow(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row == firstRow && row == lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断指定的单元格是否是合并单元格
     *
     * @param sheet
     * @param row    行下标
     * @param column 列下标
     * @return
     */
    private MergeCellInfo isMergedRegion(Sheet sheet, int row, int column) {
        MergeCellInfo mergeCellInfo = null;
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow && column >= firstColumn && column <= lastColumn) {
                mergeCellInfo = new MergeCellInfo();
                mergeCellInfo.setLastrow(lastRow);
                mergeCellInfo.setLastColumn(lastColumn);
                return mergeCellInfo;
            }
        }
        return mergeCellInfo;
    }


    /**
     * 判断sheet页中是否含有合并单元格
     *
     * @param sheet
     * @return
     */
    private boolean hasMerged(Sheet sheet) {
        return sheet.getNumMergedRegions() > 0 ? true : false;
    }

    /**
     * 合并单元格
     *
     * @param sheet
     * @param firstRow 开始行
     * @param lastRow  结束行
     * @param firstCol 开始列
     * @param lastCol  结束列
     */
    private void mergeRegion(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    /**
     * 获取单元格的值
     *
     * @param cell
     * @return
     */
    public String getCellValue(Cell cell) {

        if (cell == null) return "";

        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {

            return cell.getStringCellValue();

        } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {

            return String.valueOf(cell.getBooleanCellValue());

        } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {

            return cell.getCellFormula();

        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {

            return String.valueOf(cell.getNumericCellValue());

        }
        return "";
    }

    /**
     * 获取单元格的值
     *
     * @param oldcell 原文件单元格
     * @param newcell 新文件单元格
     */
    public void setNewCell(Cell oldcell, Cell newcell) {

        if (oldcell.getCellType() == Cell.CELL_TYPE_STRING) {
            String value = oldcell.getStringCellValue();
            newcell.setCellValue(value);
        } else if (oldcell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            Boolean value = oldcell.getBooleanCellValue();
            newcell.setCellValue(value);
        } else if (oldcell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            String fomular = oldcell.getCellFormula();
            newcell.setCellFormula(fomular);
        } else if (oldcell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            newcell.setCellValue(oldcell.getNumericCellValue());
        }
    }

    private class MergeCellInfo {
        private int lastrow;
        private int lastColumn;

        public int getLastrow() {
            return lastrow;
        }

        public void setLastrow(int lastrow) {
            this.lastrow = lastrow;
        }

        public int getLastColumn() {
            return lastColumn;
        }

        public void setLastColumn(int lastColumn) {
            this.lastColumn = lastColumn;
        }
    }


}
