package com.dame.cn.controller.excel;


import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import cn.hutool.poi.excel.style.StyleUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dame.cn.beans.entities.ExcelUser;
import com.dame.cn.beans.response.BizException;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.service.excel.ExcelUserService;
import com.dame.cn.utils.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * excel测试 前端控制器
 * </p>
 *
 * @author LYQ
 * @since 2020-04-30
 */
@RestController
@RequestMapping("/excel/user")
@Slf4j
public class ExcelUserController {

    @Autowired
    private ExcelUserService excelUserService;
    @Autowired
    private IdWorker idWorker;

    /**
     * 普通Excel导出，带简单样式处理
     */
    @GetMapping(value = "/export")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1、获取表数据
        List<ExcelUser> userList = excelUserService.list();

        // 2、生成Excel
        // 2.1 自定义标题别名，顺序就是我们写的这个顺序展示
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.addHeaderAlias("id", "会员号");
        writer.addHeaderAlias("username", "姓名");
        writer.addHeaderAlias("mobile", "手机号");
        writer.addHeaderAlias("departmentName", "部门名称");
        writer.addHeaderAlias("sex", "性别");
        writer.addHeaderAlias("nativePlace", "籍贯");
        writer.addHeaderAlias("dateOfBirth", "出生日期");
        // 2.2 合并单元格后的标题行
        writer.merge(6, "会员数据统计");
        // 2.3 一次性写出内容，强制输出标题
        writer.write(userList, true);
        // 2.4 样式设置
        handleStyle(writer);

        // 3、完成下载
        String fileName = URLEncoder.encode("会员统计数据.xlsx", "UTF-8");
        response.setContentType("application/octet-stream");
        //保存的文件名,必须和页面编码一致,否则乱码
        response.setHeader("content-disposition", "attachment;filename=" + new String(fileName.getBytes("ISO8859-1")));
        response.setHeader("fileName", fileName);
        ServletOutputStream outputStream = response.getOutputStream();
        writer.flush(outputStream, true);
        writer.close();
        IoUtil.close(outputStream);
    }

    private void handleStyle(ExcelWriter writer) {
        // 1、设置整体样式
        // 1.1 设置整体行高度， writer.setDefaultRowHeight(16) -> 设置不起作用
        for (int i = 2; i < writer.getRowCount(); i++) {
            writer.setRowHeight(i, 16);
        }

        // 1.2 设置列宽
        // writer.autoSizeColumnAll(); // 自适应
        writer.setColumnWidth(0, 23);
        writer.setColumnWidth(1, 19);
        writer.setColumnWidth(2, 19);
        writer.setColumnWidth(3, 19);
        writer.setColumnWidth(4, 14);
        writer.setColumnWidth(5, 16);
        writer.setColumnWidth(6, 25);

        StyleSet styleSet = writer.getStyleSet();

        // 1.3 设置整体的边框，字体，居中
        styleSet.setBorder(BorderStyle.THIN, IndexedColors.BLACK); //边框
        styleSet.setAlign(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);//水平垂直居中
        styleSet.setFont(Font.COLOR_NORMAL, (short) 11, "等线", true);// 默认字体

        // 1.4 设置日期格式
        CellStyle cellStyleForDate = styleSet.getCellStyleForDate();
        cellStyleForDate.setDataFormat(writer.getWorkbook().getCreationHelper().createDataFormat().getFormat("yyyy-MM-dd"));

        // 2、设置表头样式（行高、字体、背景色）
        writer.setRowHeight(1, 20); // 行高
        CellStyle headCellStyle = styleSet.getHeadCellStyle();
        headCellStyle.setFont(StyleUtil.createFont(writer.getWorkbook(), Font.COLOR_NORMAL, (short) 14, "等线"));// 字体样式
        StyleUtil.setColor(headCellStyle, IndexedColors.GREEN, FillPatternType.SOLID_FOREGROUND);// 背景色

        // 3、设置标题样式（行高、字体）
        writer.setRowHeight(0, 30);// 行高
        CellStyle titleCellStyle = StyleUtil.createDefaultCellStyle(writer.getWorkbook());
        titleCellStyle.setFont(StyleUtil.setFontStyle(writer.getWorkbook().createFont(), Font.COLOR_NORMAL, (short) 20, "黑体")); // 字体样式
        writer.getCell(0, 0).setCellStyle(titleCellStyle);
    }

    /**
     * 导入Excel数据
     */
    @PostMapping(value = "/import")
    public Result importExcel(@RequestParam("file") MultipartFile file) {
        try {
            // 1.保存Excel文件到OSS
            // OssUtil.upload(file, "excel");

            // 2.读取Excel内容
            ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
            // 2.1 将标题转换为Bean的字段名
            reader.addHeaderAlias("姓名", "username");
            reader.addHeaderAlias("手机号", "mobile");
            reader.addHeaderAlias("部门名称", "departmentName");
            reader.addHeaderAlias("性别", "sex");
            reader.addHeaderAlias("籍贯", "nativePlace");
            reader.addHeaderAlias("出生日期", "dateOfBirth");
            // 2.2 读取每行内容，封装到Bean中
            List<ExcelUser> userList = reader.read(2, 3, ExcelUser.class);
            for (ExcelUser user : userList) {
                user.setId(String.valueOf(idWorker.nextId()));
            }
            // 2.3 入库
            boolean success = excelUserService.saveBatch(userList);
            if (success) {
                return new Result(ResultCode.SUCCESS);
            } else {
                return new Result(ResultCode.EXCEL_UPLOAD_ERROR);
            }
        } catch (Exception e) {
            throw new BizException(ResultCode.FAIL);
        }
    }

    @GetMapping(value = "/page")
    public Result page(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        Page<ExcelUser> userPage = new Page<>(page, size);
        excelUserService.page(userPage);
        Map<Object, Object> build = MapUtil.builder().put("total", userPage.getTotal()).put("rows", userPage.getRecords()).build();
        return new Result(ResultCode.SUCCESS, build);
    }
}


