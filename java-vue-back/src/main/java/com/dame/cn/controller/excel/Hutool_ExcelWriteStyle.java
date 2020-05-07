package com.dame.cn.controller.excel;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author LYQ
 * @description Excel 写带样式
 * @since 2020/5/4 14:03
 **/
public class Hutool_ExcelWriteStyle {

    // 使用hutool设置样式，参考 ExcelUserController.exportExcel()


    /**
     * 模板，基本就是按照原生的，
     */
    @Test
    public void test() throws Exception {

        ExcelReader reader = ExcelUtil.getReader("D:\\2_WorkSpace\\1_Project\\4_GitHub\\Java-Vue\\ExcelUser样式模板.xlsx");
        Workbook workbook = reader.getWorkbook();

        // 公共样式
        Row rowStyle = reader.getOrCreateRow(2);
        CellStyle[] styles = new CellStyle[rowStyle.getLastCellNum()];
        for (int i = 0; i < rowStyle.getLastCellNum(); i++) {
            styles[i] = rowStyle.getCell(i).getCellStyle();
        }

        // 数据
        Row row4 = reader.getOrCreateRow(4);
        for (int i = 0; i < rowStyle.getLastCellNum(); i++) {
            Cell cell = row4.createCell(i);
            cell.setCellValue(i + "-" + RandomUtil.randomInt(100));
            cell.setCellStyle(styles[i]);
        }

        // 修改表头内容
        Row row0 = reader.getOrCreateRow(0);
        Cell cell = row0.getCell(0);
        cell.setCellValue("哈哈哈");

        OutputStream outputStream = new FileOutputStream("D:\\hutool_style02.xlsx");
        workbook.write(outputStream);
        outputStream.close();
        reader.close();
    }
}
