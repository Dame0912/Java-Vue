package com.dame.cn.controller.excel;

import cn.hutool.core.date.DateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * @author LYQ
 * @description Excel写测试
 * @since 2020/5/1 9:15
 **/
public class Test_ExcelWrite {

    /**
     * 03版本Excel的写
     *  -> 使用 HSSFWorkbook
     *  -> 文件名后缀：.xls
     *
     *  缺点：最多只能处理65536行，否则会抛出异常
     *      java.lang.IllegalArgumentException: Invalid row number (65536) outside allowable range (0..65535)
     *
     *  优点：过程中写入内存，不操作磁盘，最后一次性写入磁盘，速度快
     *
     *  Excel2003 是一个特有的二进制格式，其核心结构是复合文档类型的结构，存储数据量较小
     */
    @Test
    public void write_03() throws IOException {
        // 创建工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 创建Sheet，不传参数，默认sheet0
        Sheet sheet = workbook.createSheet("会员登陆统计");

        // 创建行(row1)，0：代表Excel行的索引，表示从第一行开始写
        Row row1 = sheet.createRow(0);
        // 创建单元格（col 1-1） 0：代表Excel列的索引，表示从第一列开始写
        Cell cell11 = row1.createCell(0);
        cell11.setCellValue("今日人数");
        // 创建单元格（col 1-2）
        Cell cell12 = row1.createCell(1);
        cell12.setCellValue("统计时间");

        // 创建行(row2)
        Row row2 = sheet.createRow(1);
        // 创建单元格（col 2-1）
        Cell cell21 = row2.createCell(0);
        cell21.setCellValue(666);
        //创建单元格（col 2-2）
        Cell cell22 = row2.createCell(1);
        String dateTime = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        cell22.setCellValue(dateTime);

        // 将Excel写入磁盘(指定文件名称和后缀)
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\write03.xls");
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        System.out.println("03版本Excel写结束");
    }

    /**
     * 07版本Excel的写
     *  -> 使用 XSSFWorkbook
     *  -> 文件名后缀：.xlsx
     *
     *  缺点：如果数据量太大，写数据时速度非常慢，非常耗内存，也会发生内存溢出，如100万条。
     *
     *  优点：可以写较大的数据量，如20万条，操作也是在内存中，最后一次写入磁盘。
     *
     *  Excel2007 的核心结构是 XML 类型的结构，采用的是基于 XML 的压缩方式，使其占用的空间更小，操作效率更高
     */
    @Test
    public void write_07() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet();
        Row row1 = sheet.createRow(0);
        Cell cell11 = row1.createCell(0);
        cell11.setCellValue("统计人数");
        Cell cell12 = row1.createCell(1);
        cell12.setCellValue("统计时间");
        Row row2 = sheet.createRow(1);
        Cell cell21 = row2.createCell(0);
        cell21.setCellValue(666);
        Cell cell22 = row2.createCell(1);
        cell22.setCellValue(DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));

        FileOutputStream fileOutputStream = new FileOutputStream("D:\\write07.xlsx");
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        System.out.println("07版本Excel写结束");
    }


}
