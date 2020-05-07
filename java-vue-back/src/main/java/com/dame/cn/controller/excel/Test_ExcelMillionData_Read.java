package com.dame.cn.controller.excel;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LYQ
 * @description TODO
 * @since 2020/5/1 17:18
 **/
public class Test_ExcelMillionData_Read {
    @Test
    public void millionData_read() throws Exception {
        String path = "D:\\2_WorkSpace\\1_Project\\4_GitHub\\Java-Vue\\Excel百万数据.xlsx";
        //1.根据Excel获取OPCPackage对象
        OPCPackage opcPackage = OPCPackage.open(path, PackageAccess.READ);
        //2.创建XSSFReader对象
        XSSFReader xssfReader = new XSSFReader(opcPackage);
        //3.获取SharedStringsTable对象
        SharedStringsTable sharedStringsTable = xssfReader.getSharedStringsTable();
        //4.获取StylesTable对象
        StylesTable stylesTable = xssfReader.getStylesTable();
        //5.创建Sax的XmlReader对象
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        //6.设置sheet处理器
        xmlReader.setContentHandler(new XSSFSheetXMLHandler(stylesTable, sharedStringsTable, new SheetHandler(), false));
        //7.逐行读取
        XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator)xssfReader.getSheetsData();
        while (sheets.hasNext()) {
            InputStream inputStream = sheets.next();
            InputSource inputSource = new InputSource(inputStream);
            xmlReader.parse(inputSource);
            inputStream.close();
        }
        opcPackage.close();
    }
}

/**
 * 自定义基于Sax的Sheet解析处理器
 */
class SheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

    // 封装每行对象
    private Map map = null;

    /**
     * 解析行开始
     */
    @Override
    public void startRow(int rowNum) {
        // 第一行是标题，不解析
        if (rowNum > 0) {
            map = new HashMap();
        }
    }

    /**
     * 解析行结束
     */
    @Override
    public void endRow(int rowNum) {
        if (rowNum > 0) {
            System.out.println(map);
        }
    }

    /**
     * 解析行中的每个单元格
     */
    @Override
    public void cell(String cellReference, String formattedValue, XSSFComment comment) {
        if (null != map) {
            switch (cellReference.substring(0, 1)) { // 获取Excel列的头(不是表头，是Excel的)
                case "A":
                    map.put("id", formattedValue);
                    break;
                case "B":
                    map.put("breast", formattedValue);
                    break;
                case "C":
                    map.put("adipocytes", formattedValue);
                    break;
                case "D":
                    map.put("Negative", formattedValue);
                    break;
                case "E":
                    map.put("Staining", formattedValue);
                    break;
                case "F":
                    map.put("Supportive", formattedValue);
                    break;
            }
        }
    }
}
