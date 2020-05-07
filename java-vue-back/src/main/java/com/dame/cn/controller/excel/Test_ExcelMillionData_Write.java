package com.dame.cn.controller.excel;

import cn.hutool.core.date.DateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * @author LYQ
 * @description TODO
 * @since 2020/5/1 17:18
 **/
public class Test_ExcelMillionData_Write {
    /**
     * 07版本Excel的 大数据量写
     *  -> 使用 SXSSFWorkbook
     *  -> 文件名后缀：.xlsx
     *
     *  背景：
     *      基于07版本的 XSSFWork 导出Excel报表，是通过将所有单元格对象保存到内存中，当所有的Excel单元格全部创建完成之后一次性写入到Excel并导出。
     *      当百万数据级别的Excel导出时，随着表格的不断创建，内存中对象越来越多，直至内存溢出。
     *
     *  解决：
     *      在实例化 SXSSFWork 这个对象时，可以指定在内存中所产生的POI导出相关对象的数量（默认100），一旦内存中的对象的个数达到这个指定值时，
     *      就将内存中的这些对象的内容写入到磁盘中（XML的文件格式），然后就可以将这些对象从内存中销毁，以此循环，直至Excel导出完成。
     *
     *  优点：可以写非常大的数据量，如100万条甚至更多条，写数据速度快，占用更少的内存
     *
     *  注意：
     *      过程中会产生临时文件，临时文件(C:\Users\迪米\AppData\Local\Temp\poifiles),程序结束自动清理
     *      默认由100条记录被保存在内存中，如果超过这数量，则最前面的数据被写入临时文件
     *      如果想自定义内存中数据的数量，可以使用new SXSSFWorkbook(数量)
     *
     *  官方的解释：
     *      实现“BigGridDemo”策略的流式XSSFWorkbook版本。这允许写入非常大的文件而不会耗尽内存，因为任何时候只有可配置的行部分被保存在内存中。
     *      请注意，仍然可能会消耗大量内存，这些内存基于您正在使用的功能，例如合并区域，注释......仍然只存储在内存中，因此如果广泛使用，可能需要大量内存。
     */
    @Test
    public void write_07_bigData() throws IOException {
        //记录开始时间
        long begin = System.currentTimeMillis();

        SXSSFWorkbook workbook = new SXSSFWorkbook();

        Sheet sheet = workbook.createSheet();
        Row row1 = sheet.createRow(0);
        Cell cell11 = row1.createCell(0);
        cell11.setCellValue("统计人数");
        Cell cell12 = row1.createCell(1);
        cell12.setCellValue("统计时间");

        for (int i = 1; i < 1000000; i++) {
            Row row = sheet.createRow(i);
            Cell cell1 = row.createCell(0);
            cell1.setCellValue(666);
            Cell cell2 = row.createCell(1);
            cell2.setCellValue(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }

        FileOutputStream fileOutputStream = new FileOutputStream("D:\\write07BigData.xlsx");
        workbook.write(fileOutputStream);
        fileOutputStream.close();

        //记录结束时间
        long end = System.currentTimeMillis();
        System.out.println("07版本Excel写结束，耗时：" + (double)(end - begin)/1000);
    }
}
