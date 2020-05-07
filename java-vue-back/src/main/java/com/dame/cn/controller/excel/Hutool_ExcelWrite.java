package com.dame.cn.controller.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import org.junit.Test;

import java.util.List;

/**
 * @author LYQ
 * @description 使用 Hutool 写入Excel
 * @since 2020/5/4 13:39
 **/
public class Hutool_ExcelWrite {

    /**
     * 将行列对象写出到Excel，以及Map，Bean，流，客户端下载，带样式等，具体参考文档
     */
    @Test
    public void test_list() {
        List<String> row1 = CollUtil.newArrayList("aa", "bb", "cc", "dd");
        List<String> row2 = CollUtil.newArrayList("aa1", "bb1", "cc1", "dd1");
        List<String> row3 = CollUtil.newArrayList("aa2", "bb2", "cc2", "dd2");
        List<String> row4 = CollUtil.newArrayList("aa3", "bb3", "cc3", "dd3");
        List<String> row5 = CollUtil.newArrayList("aa4", "bb4", "cc4", "dd4");

        List<String> row6 = CollUtil.newArrayList("A", "B", "C", "D");

        List<List<String>> rows = CollUtil.newArrayList(row1, row2, row3, row4, row5);

        ExcelWriter writer = ExcelUtil.getWriter("D:\\hutool_list2.xlsx");
        writer.passCurrentRow();
        writer.merge(row1.size() - 1, "测试标题");
        writer.writeHeadRow(row6);
        writer.write(rows, true);
        writer.close();
    }
}
