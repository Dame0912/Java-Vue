package com.dame.cn;

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LYQ
 * @description TODO
 * @since 2020/4/27 15:32
 **/
public class TTest {

    @Test
    public void test01() {
        List<Map<String, String>> data = getData();
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        data.forEach(map -> {
            sb1.append("UPDATE xinan_hzf.tool_orders SET incoming_id = '")
                    .append(map.get("actId")).append("' WHERE retail_apply_no = '")
                    .append(map.get("applyNo")).append("';").append("\r\n");
            sb2.append("UPDATE xinan_fd.flows SET incoming_id = '")
                    .append(map.get("actId")).append("' WHERE retail_apply_no = '")
                    .append(map.get("applyNo")).append("';").append("\r\n");
        });
        System.out.println(sb1.toString());
        System.out.println();
        System.out.println("============");
        System.out.println();
        System.out.println(sb2.toString());
    }

    private List<Map<String, String>> getData(){
        List<Map<String, String>> list = new ArrayList<>();
        try {
            File file = new File("D:\\1.txt");
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader bf = new BufferedReader(inputReader);
            // 按行读取字符串
            String str;
            while ((str = bf.readLine()) != null) {
                Map<String, String> map = new HashMap<>();
                String[] split = str.split("\t");
                map.put("applyNo", split[0]);
                map.put("actId", split[1]);
                list.add(map);
            }
            bf.close();
            inputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
