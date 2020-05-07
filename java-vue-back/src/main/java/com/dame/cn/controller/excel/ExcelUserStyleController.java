package com.dame.cn.controller.excel;


import cn.hutool.core.io.IoUtil;
import com.dame.cn.beans.entities.ExcelUser;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.config.oss.OssProperties;
import com.dame.cn.config.oss.OssUtil;
import com.dame.cn.service.excel.ExcelUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * excel测试 样式 前端控制器
 * </p>
 *
 * @author LYQ
 * @since 2020-04-30
 */
@RestController
@RequestMapping("/excel/user/style")
@Slf4j
public class ExcelUserStyleController {

    @Autowired
    private ExcelUserService excelUserService;
    @Autowired
    private OssProperties ossProperties;

    /**
     * 导出使用模板样式 Excel
     * <p>
     * utils/excel下面有工具类，这里为了复习没有使用
     */
    @GetMapping(value = "/export")
    public void exportStyleExcel(HttpServletResponse response) throws IOException {
        // 1、从OSS下载模板
        InputStream inputStream = OssUtil.downLoad("excel/template/ExcelUser样式模板.xlsx");

        // 2、构建Excel
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Row rowTitle = sheet.getRow(0);
        rowTitle.getCell(0).setCellValue("会员数据统计");

        // 3、获取通用样式
        Row rowStyle = sheet.getRow(2);
        int rowCellNum = rowStyle.getLastCellNum();
        CellStyle[] styles = new CellStyle[rowCellNum];
        for (int i = 0; i < rowCellNum; i++) {
            styles[i] = rowStyle.getCell(i).getCellStyle();
        }

        // 4、查询数据，设置值
        List<ExcelUser> userList = excelUserService.list();
        for (int i = 0; i < userList.size(); i++) {
            Row row = sheet.createRow(i + 2);
            ExcelUser user = userList.get(i);
            for (int j = 0; j < rowCellNum; j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(styles[j]);
                switch (j) {
                    case 0:
                        cell.setCellValue(user.getId());
                        break;
                    case 1:
                        cell.setCellValue(user.getUsername());
                        break;
                    case 2:
                        cell.setCellValue(user.getMobile());
                        break;
                    case 3:
                        cell.setCellValue(user.getDepartmentName());
                        break;
                    case 4:
                        cell.setCellValue(user.getSex());
                        break;
                    case 5:
                        cell.setCellValue(user.getNativePlace());
                        break;
                    case 6:
                        cell.setCellValue(user.getDateOfBirth());
                        break;
                }
            }
        }

        // 5、完成下载
        String fileName = URLEncoder.encode("会员统计数据.xlsx", "UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("content-disposition", "attachment;filename=" + new String(fileName.getBytes("ISO8859-1")));
        response.setHeader("fileName", fileName);
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        IoUtil.close(outputStream);
        IoUtil.close(inputStream);
    }

    /**
     * 前端获取后端服务器生成的传输密钥，然后上传 Excel样式模板
     */
    @PostMapping(value = "/oss/policy")
    public Result upload_oss_excel_policy() {
        OssUtil.OssPolicyResult policy = OssUtil.policy(false, ossProperties.getExcelHost());
        return new Result(ResultCode.SUCCESS, policy);
    }

}


