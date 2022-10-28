package com.atguigu.srb.core;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;


public class TestAutoGenerator {
    public static void main(String[] args) {
        //创建代码生成器
        AutoGenerator autoGenerator = new AutoGenerator();

        //全局属性
        GlobalConfig globalConfig=new GlobalConfig();
        globalConfig.setAuthor("pp");
        globalConfig.setIdType(IdType.AUTO);
        globalConfig.setServiceName("%sService");
        globalConfig.setServiceImplName("%sImplService");
        globalConfig.setXmlName("%sMapper");
        String property = System.getProperty("user.dir");
        globalConfig.setOutputDir(property+"/service-core/src/main/java");
        autoGenerator.setGlobalConfig(globalConfig);

        //数据库来连接
        DataSourceConfig dataSourceConfig=new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL);
        dataSourceConfig.setUsername("root");
        dataSourceConfig.setPassword("123456");
        dataSourceConfig.setDriverName("com.mysql.cj.jdbc.Driver");
        dataSourceConfig.setUrl("jdbc:mysql://localhost:3306/srb_core?characterEncoding=utf-8&serverTimezone=GMT%2B8");
        autoGenerator.setDataSource(dataSourceConfig);
        //代码包路径
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent("com.atguigu.srb.core");
        packageConfig.setEntity("pojo.entity");
        autoGenerator.setPackageInfo(packageConfig);

        //其他配置
        StrategyConfig strategyConfig= new StrategyConfig();
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
        strategyConfig.setEntityLombokModel(true); // lombok
        strategyConfig.setLogicDeleteFieldName("is_deleted");//逻辑删除字段名
        strategyConfig.setEntityBooleanColumnRemoveIsPrefix(true);//去掉布尔值的is_前缀（确保tinyint(1)）
        strategyConfig.setRestControllerStyle(true); //restful api风格控制器
        autoGenerator.setStrategy(strategyConfig);

        //执行
        autoGenerator.execute();

    }
}
