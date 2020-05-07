package com.dame.cn.controller.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileInputStream;
import java.text.DecimalFormat;

/**
 * @author LYQ
 * @description Excel读测试
 * @since 2020/5/1 13:54
 **/
public class Test_ExcelRead {

    /**
     * 03版本Excel的读
     * -> 使用 HSSFWorkbook
     * -> 文件名后缀：.xls
     */
    @Test
    public void read_03() throws Exception {
        FileInputStream inputStream = new FileInputStream("D:\\2_WorkSpace\\1_Project\\4_GitHub\\Java-Vue\\ExcelUser导入数据-03.xls");
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
        handle(workbook);
        inputStream.close();
    }

    /**
     * 07版本Excel的读
     * -> 使用 XSSFWorkbook
     * -> 文件名后缀：.xlsx
     */
    @Test
    public void read_07() throws Exception {
        FileInputStream inputStream = new FileInputStream("D:\\2_WorkSpace\\1_Project\\4_GitHub\\Java-Vue\\ExcelUser导入数据.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        handle(workbook);
        inputStream.close();
    }

    /**
     * 循环每行，以及每行中的单元格，处理数据
     */
    private void handle(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        for (int i = 2; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            StringBuilder sb = new StringBuilder();
            for (int j = 1; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                Object cellValue = getValue(cell);
                sb.append(cellValue).append("-");
            }
            System.out.println(sb.toString());
        }
    }

    private Object getValue(Cell cell) {
        Object cellValue = null;
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING: // 字符串类型
                cellValue = cell.getStringCellValue();
                break;
            case NUMERIC: // 数字类型（包含日期和普通数字）
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = cn.hutool.core.date.DateUtil.format(cell.getDateCellValue(), "yyyy-MM-dd HH:mm:ss");
                } else {
                    // 格式化下，不然长数字如手机号，会被转为 科学计数法
                    DecimalFormat df = new DecimalFormat("#");
                    cellValue = df.format(cell.getNumericCellValue());
                }
                break;
            case BOOLEAN: // boolean类型
                cellValue = cell.getBooleanCellValue();
                break;
            case FORMULA: // 公式类型
                cellValue = cell.getCellFormula();
                break;
            default:
                cellValue = "";
                break;
        }
        return cellValue;
    }
}
