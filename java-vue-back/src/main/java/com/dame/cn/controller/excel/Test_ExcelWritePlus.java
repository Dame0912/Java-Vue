package com.dame.cn.controller.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @author LYQ
 * @description Excel高级应用
 * @since 2020/5/1 14:44
 **/
public class Test_ExcelWritePlus {

    /**
     * Excel中插入图片
     */
    @Test
    public void write_image() throws Exception {
        //1.创建workbook工作簿
        Workbook wb = new XSSFWorkbook();
        //2.创建表单Sheet
        Sheet sheet = wb.createSheet("test");

        //读取图片流
        FileInputStream stream = new FileInputStream("D:\\2_WorkSpace\\1_Project\\4_GitHub\\Java-Vue\\test.jpg");
        byte[] bytes = IOUtils.toByteArray(stream);
        //读取图片到二进制数组
        stream.read(bytes);
        //向Excel添加一张图片,并返回该图片在Excel中的图片集合中的下标
        int pictureIdx = wb.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
        //绘图工具类
        CreationHelper helper = wb.getCreationHelper();
        //创建一个绘图对象
        Drawing<?> patriarch = sheet.createDrawingPatriarch();
        //创建锚点,设置图片坐标
        ClientAnchor anchor = helper.createClientAnchor();
        anchor.setCol1(0);//从0开始
        anchor.setRow1(0);//从0开始
        //创建图片
        Picture picture = patriarch.createPicture(anchor, pictureIdx);
        picture.resize(5, 7);//x方向(列)占5个格子，y方向(行)占7个格式

        //6.文件流
        FileOutputStream fos = new FileOutputStream("D:\\write07_image.xlsx");
        //7.写入文件
        wb.write(fos);
        fos.close();
    }


}
