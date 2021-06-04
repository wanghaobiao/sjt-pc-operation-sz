package com.acrabsoft.web.service.sjt.pc.operation.web.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUntil {
    private Logger log = LoggerFactory.getLogger( ExcelUntil.class);
    private static final String EXTENSION_XLS = "xls";
    private static final String EXTENSION_XLSX = "xlsx";


    /**
     * @description  创建excel
     * @param  response
     * @param  heads
     * @param  dictJson
     * @param  jsonArray
     * @return  返回结果
     * @date  20/09/08 19:17
     * @author  wanghb
     * @edit
     */
    public static void createExcel(HttpServletResponse response, String[] heads,JSONObject dictJson, JSONArray jsonArray,String titleName) {
        String filename = titleName + ".xls";
        HSSFWorkbook workbook = null;
        ServletOutputStream outputStream=null;
        try {
            response.setContentType( "application/vnd.ms-excel" );
            response.setHeader( "Content-Disposition", "attachment;filename=" + URLEncoder.encode( filename, "utf-8" ) );
            // 创建一个Excel文件
            workbook =  (HSSFWorkbook) ExcelUntil.getWorkbook(filename);
            // 创建一个Excel的Sheet
            HSSFSheet sheet = workbook.createSheet();
            // sheet.setColumnHidden((short)0, true);
            sheet.setDefaultColumnWidth(20);
            sheet.addMergedRegion(new CellRangeAddress( 0,0,0, heads.length - 1) );

            CellStyle tipStyle = workbook.createCellStyle();
            tipStyle.setFillForegroundColor( IndexedColors.GREY_25_PERCENT.getIndex());
            //设置字体
            HSSFFont redFont = workbook.createFont();
            redFont.setColor( Font.COLOR_RED);
            redFont.setFontHeightInPoints((short) 16);
            redFont.setFontName("宋体");
            tipStyle.setFont(redFont);
            tipStyle.setBorderTop( HSSFCellStyle.BORDER_THIN);
            tipStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            tipStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            tipStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            tipStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
            tipStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            tipStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            //设置垂直对齐的样式为居中对齐;
            tipStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            tipStyle.setWrapText( true );
            //创建提示
            Row promptRow = sheet.createRow( 0 );
            promptRow.createCell( 0 ).setCellValue( titleName);
            promptRow.getCell( 0 ).setCellStyle( tipStyle );
            promptRow.setHeightInPoints(40);

            CellStyle style = workbook.createCellStyle();

            //设置字体
            HSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setFontName("宋体");
            style.setFont(font);
            style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setFillPattern(CellStyle.SOLID_FOREGROUND);
            style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            style.setFillForegroundColor( IndexedColors.WHITE.getIndex());

            //style.setWrapText( true );

            Row titleRow = sheet.createRow( 1 );
            titleRow.setHeightInPoints( 25 );
            //表头赋值
            for (int i = 0; i < heads.length; i++) {
                titleRow.createCell( i ).setCellValue( dictJson.getString(heads[i]));
                redFont.setColor(IndexedColors.BLACK.getIndex());
                redFont.setFontHeightInPoints((short) 12);
                titleRow.getCell( i ).setCellStyle(style);
            }
            //单元格赋值
            if (jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    Row row = sheet.createRow( i + 2 );
                    row.setHeightInPoints( 25 );
                    // 遍历 jsonarray

                    JSONObject json = jsonArray.getJSONObject( i );
                    for (int j = 0; j < heads.length; j++) {
                        //赋值
                        row.createCell( j ).setCellValue( String.valueOf(json.get( heads[j] ) ));
                        row.getCell( j ).setCellStyle(style);
                    }
                }
            }
            outputStream = response.getOutputStream();
            workbook.write( outputStream );
            outputStream.flush();
            outputStream.close();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            //throw new Exception( "导出异常" );
        }finally {
            if(workbook!=null){
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /***
     * <pre>
     * 取得Workbook对象(xls和xlsx对象不同,不过都是Workbook的实现类)
     *   xls:HSSFWorkbook
     *   xlsx：XSSFWorkbook
     * @param filename
     * @return
     * @throws IOException
     * </pre>
     */
    public static Workbook getWorkbook(String filename) {
        Workbook workbook = null;
        if (filename.endsWith( EXTENSION_XLS )) {
            workbook = new HSSFWorkbook();
        } else if (filename.endsWith( EXTENSION_XLSX )) {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
            workbook = new SXSSFWorkbook(xssfWorkbook, 1000);
        }

        return workbook;
    }


    public static void main(String[] args) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();// excel文件对象
        HSSFSheet sheetlist = wb.createSheet("sheetlist");// 工作表对象

        FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\success.xls");
        String[] textlist = { "列表1", "列表2", "列表3", "列表4", "列表5" };

        sheetlist = setHSSFValidation(sheetlist, textlist, 0, 10000, 0, 0);// 第一列的前501行都设置为选择列表形式.
        // sheetlist = setHSSFPrompt(sheetlist, "promt Title", "prompt Content",
        // 0, 500, 1, 1);// 第二列的前501行都设置提示.

        wb.write(out);
        out.close();
    }

    /**
     * 设置某些列的值只能输入预制的数据,显示下拉框.
     *
     * @param sheet
     *            要设置的sheet.
     * @param textlist
     *            下拉框显示的内容
     * @param firstRow
     *            开始行
     * @param endRow
     *            结束行
     * @param firstCol
     *            开始列
     * @param endCol
     *            结束列
     * @return 设置好的sheet.
     */
    public static HSSFSheet setHSSFValidation(HSSFSheet sheet,
                                              String[] textlist, int firstRow, int endRow, int firstCol,
                                              int endCol) {
        // 加载下拉列表内容
        DVConstraint constraint = DVConstraint
                .createExplicitListConstraint(textlist);
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,
                endRow, firstCol, endCol);
        // 数据有效性对象
        HSSFDataValidation data_validation_list = new HSSFDataValidation(
                regions, constraint);
        sheet.addValidationData(data_validation_list);
        return sheet;
    }

    /**
     * 设置单元格上提示
     *
     * @param sheet
     *            要设置的sheet.
     * @param promptTitle
     *            标题
     * @param promptContent
     *            内容
     * @param firstRow
     *            开始行
     * @param endRow
     *            结束行
     * @param firstCol
     *            开始列
     * @param endCol
     *            结束列
     * @return 设置好的sheet.
     */
    public static HSSFSheet setHSSFPrompt(HSSFSheet sheet, String promptTitle,
                                          String promptContent, int firstRow, int endRow, int firstCol,
                                          int endCol) {
        // 构造constraint对象
        DVConstraint constraint = DVConstraint
                .createCustomFormulaConstraint("BB1");
        // 四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,
                endRow, firstCol, endCol);
        // 数据有效性对象
        HSSFDataValidation data_validation_view = new HSSFDataValidation(
                regions, constraint);
        data_validation_view.createPromptBox(promptTitle, promptContent);
        sheet.addValidationData(data_validation_view);
        return sheet;
    }


    /**
     * @description  数据读取
     * @param  file  文件
     * @return  返回结果
     * @date  20/09/09 9:57
     * @author  wanghb
     * @edit
     */
    private List<Map<String, Object>> readExcel(MultipartFile file) throws IOException {
        String name = file.getOriginalFilename();
        InputStream io = file.getInputStream();
        String attribute = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
        Workbook wb = null;
        if ("xls".equals(attribute)) {
            wb = new HSSFWorkbook(io);
        } else if ("xlsx".equals(attribute)) {
            wb = new XSSFWorkbook(io);
        }
        Sheet sheet = wb.getSheetAt(0);

        List<Map<String, Object>> list = new ArrayList<>();
        // 循环行Row
        for (int rowNum = 1; rowNum < sheet.getLastRowNum() + 1; rowNum++) {
            Map<String, Object> temp = new HashMap<>();
            Row row = sheet.getRow(rowNum);
            if (row != null) {
                Cell cell0 = row.getCell( 0 );
                Cell cell1 = row.getCell( 1 );
                Cell cell2 = row.getCell( 2 );
                Cell cell3 = row.getCell( 3 );
                Cell cell4 = row.getCell( 4 );
                Cell cell5 = row.getCell( 5 );
                temp.put("cell0", PowerUtil.getString(cell0.toString()) );
                temp.put("cell1", PowerUtil.getString(cell1.toString()) );
                temp.put("cell2", PowerUtil.getString(cell2.toString()) );
                temp.put("cell3", PowerUtil.getString(cell3.toString()) );
                temp.put("cell4", PowerUtil.getString(cell4.toString()) );
                temp.put("cell5", PowerUtil.getString(cell5.toString()) );
            }
            list.add(temp);
        }
        return list;
    }
}
