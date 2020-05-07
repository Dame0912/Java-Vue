package com.dame.cn.controller.excel;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import org.junit.Test;

import java.util.List;

/**
 * @author LYQ
 * @description 利用 Hutool 完成 大数据量的读
 * @since 2020/5/4 14:49
 **/
public class Hutool_ExcelMillionData_Read {

    @Test
    public void read07(){
        ExcelUtil.read07BySax("D:\\2_WorkSpace\\1_Project\\4_GitHub\\Java-Vue\\Excel百万数据.xlsx", 0, new RowHandler() {
            @Override
            public void handle(int sheetIndex, int rowIndex, List<Object> rowList) {
                System.out.println(rowList);
            }
        });
    }
}
