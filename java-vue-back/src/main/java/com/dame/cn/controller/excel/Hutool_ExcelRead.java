package com.dame.cn.controller.excel;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author LYQ
 * @description 使用 Hutool 读取Excel
 * @since 2020/5/4 13:15
 **/
public class Hutool_ExcelRead {

    /**
     * 优势：不用区分03版本还是07版本
     * 结果集多样：1、List<List<>>    2、List<Map<>>   3、List<T>
     * 注意：可用指定行row从什么索引开始，但是列不能指定，默认列就是顶格的。
     *      所以List的结果前面的为null，Map的key为A、B
     */
    @Test
    public void read() {
        ExcelReader reader = ExcelUtil.getReader("D:\\2_WorkSpace\\1_Project\\4_GitHub\\Java-Vue\\ExcelUser导入数据-03.xls");
        // 取Excel中所有行和列，都用列表表示
        List<List<Object>> result = reader.read(2);
        System.out.println(result);
        System.out.println("=============");

        // 读取为Map列表，默认第一行为标题行，Map中的key为标题，value为标题对应的单元格值。
        List<Map<String, Object>> maps = reader.read(2, 3, 20);
        System.out.println(maps);
        System.out.println("=============");

        // 读取为Bean列表，Bean中的字段名为标题，字段值为标题对应的单元格值
        //List<A> aList = reader.read(2, 3, 20, A.Class);
        //System.out.println(aList);
    }


}
