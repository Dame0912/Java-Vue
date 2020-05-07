//package com.dame.cn.code;
//
//import com.baomidou.mybatisplus.annotation.DbType;
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.generator.AutoGenerator;
//import com.baomidou.mybatisplus.generator.InjectionConfig;
//import com.baomidou.mybatisplus.generator.config.*;
//import com.baomidou.mybatisplus.generator.config.po.TableInfo;
//import com.baomidou.mybatisplus.generator.config.rules.DateType;
//import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @Description TODO
// * @Author lyq
// * @Date 2020/3/30 9:32
// **/
//public class MyBatisPlusCodeGenerator {
//
//    @Test
//    public void generator() {
//        //1. 全局配置
//        GlobalConfig config = new GlobalConfig();
//        String projectPath = System.getProperty("user.dir");
//        config.setOutputDir(projectPath + "\\src\\main\\java") // 生成路径
//                .setFileOverride(true)  // 文件覆盖，假如第二次执行，那么则覆盖原来的
//                .setIdType(IdType.INPUT) // 主键策略
//                .setDateType(DateType.ONLY_DATE)//定义生成的实体类中日期类型
//                .setServiceName("%sService")  // 设置生成的service接口的名字的首字母是否为I，默认：IEmployeeService，这样配置则没有I
//                .setMapperName("%sMapper")
//                .setXmlName("%sMapper")
//                .setEnableCache(false)// XML 二级缓存
//                .setBaseResultMap(true) // mapper.xml中的resultMap
//                .setBaseColumnList(true) // mapper.xml中的列片段
//                .setAuthor("LYQ"); // 文件作者
//
//        //2. 数据源配置
//        DataSourceConfig dsConfig = new DataSourceConfig();
//        dsConfig.setDbType(DbType.MYSQL)  // 设置数据库类型
//                .setDriverName("com.mysql.jdbc.Driver")
//                .setUrl("jdbc:mysql://127.0.0.1/java-vue")
//                .setUsername("root")
//                .setPassword("root");
//
//        //3. 策略配置
//        StrategyConfig stConfig = new StrategyConfig();
//        stConfig.setCapitalMode(true) //全局大写命名
//                .setNaming(NamingStrategy.underline_to_camel) // 数据库表映射到实体的命名策略
//                .setTablePrefix("sys_") // 表前缀
//                .setInclude(new String[]{"excel_user","excel_million_data"})  // 需要生成的表，多个：.setInclude(new String[] {"EMP","DEPT"})
//                .setEntityLombokModel(true); // lombok
//        //4. 包名策略配置
//        PackageConfig pkConfig = new PackageConfig();
//        pkConfig.setParent("com.dame.cn")
//                .setMapper("mapper")
//                .setService("service")
//                .setController("controller")
//                .setEntity("beans.entities");
//
//        // 5.修改mapper.xml等生成路径
//        InjectionConfig cfg = new InjectionConfig() {
//            @Override
//            public void initMap() {
//
//            }
//        };
//        List<FileOutConfig> focList = new ArrayList<>();
//        focList.add(new FileOutConfig("/templates/mapper.xml.vm") {
//            @Override
//            public String outputFile(TableInfo tableInfo) {
//                return projectPath + "\\src\\main\\resources" + "\\mybatis\\" + tableInfo.getEntityName() + "Mapper.xml";
//            }
//        });
//        cfg.setFileOutConfigList(focList);
//
//        // 6.关闭默认 xml 生成，调整生成 至 根目录
//        TemplateConfig tc = new TemplateConfig();
//        tc.setXml(null);
//
//        // 7. 整合配置
//        AutoGenerator ag = new AutoGenerator();
//
//        ag.setGlobalConfig(config)
//                .setDataSource(dsConfig)
//                .setStrategy(stConfig)
//                .setPackageInfo(pkConfig)
//                .setCfg(cfg)
//                .setTemplate(tc);
//
//        // 8. 执行
//        ag.execute();
//    }
//
//
//}
