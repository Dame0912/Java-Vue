package com.dame.cn.controller.excel;

import cn.hutool.core.util.RandomUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @author LYQ
 * @description Excel 写带样式
 * @since 2020/5/1 15:54
 **/
public class Test_ExcelWriteStyle {

    /**
     * 自定义创建带样式的Excel
     */
    @Test
    public void write_style_customize() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        // 设置指定列宽度
        sheet.setColumnWidth(0, 31 * 256);//设置第一列的宽度是31个字符宽度
        sheet.setColumnWidth(1, 31 * 256);//设置第二列的宽度是31个字符宽度
        // 设置行高
        row.setHeightInPoints(50);//设置行的高度是50个点
        // 创建单元格
        Cell cell1 = row.createCell(0);
        cell1.setCellValue("统计人数");
        CellStyle cellStyle = cellStyle(workbook);
        // 设置单元格样式
        cell1.setCellStyle(cellStyle);

        Cell cell2 = row.createCell(1);
        cell2.setCellValue("统计时间");

        FileOutputStream fileOutputStream = new FileOutputStream("D:\\write07_style.xlsx");
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        System.out.println("07版Excel样式结束");
    }

    /**
     * 通过准备好的带样式的Excel模板，创建Excel
     */
    @Test
    public void write_style_template() throws Exception {
        FileInputStream fileInputStream = new FileInputStream("D:\\2_WorkSpace\\1_Project\\4_GitHub\\Java-Vue\\ExcelUser样式模板.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        Sheet sheet = workbook.getSheetAt(0);
        // 抽取公共样式
        Row rowStyle = sheet.getRow(2);
        CellStyle[] styles = new CellStyle[rowStyle.getLastCellNum()];
        for (int i = 0; i < rowStyle.getLastCellNum(); i++) {
            styles[i] = rowStyle.getCell(i).getCellStyle();
        }

        // 构建数据
        Row row = sheet.createRow(3);
        for (int i = 0; i < rowStyle.getLastCellNum(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(i + ":" + RandomUtil.randomInt(100));
            cell.setCellStyle(styles[i]);
        }

        // 修改表头内容
        Row row0 = sheet.getRow(0);
        Cell cell = row0.getCell(0);
        cell.setCellValue("哈哈哈");

        FileOutputStream fileOutputStream = new FileOutputStream("D:\\write07_style_template.xlsx");
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        fileInputStream.close();
        System.out.println("模板结束");
    }

    private CellStyle cellStyle(Workbook workbook) {
        //创建单元格样式对象
        CellStyle cellStyle = workbook.createCellStyle();

        //设置边框
        cellStyle.setBorderTop(BorderStyle.DASH_DOT);//上边框
        cellStyle.setBorderBottom(BorderStyle.DASH_DOT);//下边框
        cellStyle.setBorderLeft(BorderStyle.DASH_DOT);//左边框
        cellStyle.setBorderRight(BorderStyle.DASH_DOT);//右边框

        //设置字体
        Font font = workbook.createFont();//创建字体对象
        font.setFontName("华文行楷");//设置字体
        font.setFontHeightInPoints((short) 28);//设置字号
        cellStyle.setFont(font);

        //设置居中显示
        cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中

        return cellStyle;
    }
}
