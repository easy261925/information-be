package com.th.workbase.common.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author kk
 * AutoGenerator MyBatis-Plus 代码生成器
 */
public class CodeGenerator {


    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void newCode() {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = "D:\\Tools\\pluswork";
        //生成文件输出目录
        gc.setOutputDir(projectPath + "/src/main/java");
        //开发人员
        gc.setAuthor("hut");
        //是否打开输出目录
        gc.setOpen(true);
        gc.setEntityName("%sDto");
        //service命名方式
        gc.setServiceName("%sService");
        //service impl命名方式
        gc.setServiceImplName("%sServiceImpl");
        //自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        gc.setFileOverride(true);
        gc.setActiveRecord(true);
        gc.setEnableCache(false);// XML 二级缓存
        gc.setBaseResultMap(true);// XML ResultMap
        gc.setBaseColumnList(false);// XML columList
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/workbaseplus?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC");
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("root");
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        //父包模块名
        //pc.setModuleName(scanner("kk"));
        //父包名。// 自定义包路径  如果为空，将下面子包名必须写全部， 否则就只需写子包名
        pc.setParent("com.th.workbase");
        pc.setEntity("bean");
        pc.setMapper("mapper");
        pc.setService("service");
        pc.setServiceImpl("service.Impl");
        //设置控制器包名
        pc.setController("controller");
        mpg.setPackageInfo(pc);

        /**
         // 自定义配置
         InjectionConfig cfg = new InjectionConfig() {
        @Override public void initMap() {
        // to do nothing
        }
        };
         List<FileOutConfig> focList = new ArrayList<>();
         focList.add(new FileOutConfig("/templates/mapper.xml.ftl") {

        @Override public String outputFile(TableInfo arg0) {
        // 自定义输入文件名称
        return projectPath + "/src/main/resources/mapper/" + pc.getModuleName()
        + "/" + arg0.getEntityName() + "Mapper" + StringPool.DOT_XML;
        }
        });

         cfg.setFileOutConfigList(focList);
         mpg.setCfg(cfg);
         **/
        mpg.setTemplate(new TemplateConfig().setXml(null));

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        //数据库表映射到实体的命名策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        //数据库表字段映射到实体的命名策略, 未指定按照 naming 执行
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        //自定义继承的Entity类全称，带包名
        strategy.setSuperEntityClass("com.th.workbase.bean.BaseDto");
        //【实体】是否为lombok模型（默认 false）
        strategy.setEntityLombokModel(true);
        //生成 @RestController 控制器
        strategy.setRestControllerStyle(true);
        //自定义继承的Controller类全称，带包名
        //strategy.setSuperControllerClass("com.baomidou.ant.common.BaseController");
        //需要包含的表名，允许正则表达式
        //strategy.setInclude(scanner("user"));
        strategy.setInclude("sys_user", "sys_organization", "sys_dict", "sys_dict_type", "sys_role", "sys_role_right", "sys_user_role");
        //自定义基础的Entity类，公共字段
        strategy.setSuperEntityColumns("id", "dt_crea_date_time", "dt_update_date_time", "isUse", "current", "page_size", "take_num"
                , "skip_num", "dt_begin_date_time", "dt_end_date_time", "limit", "remark", "show_order", "is_use");
        //驼峰转连字符
        strategy.setControllerMappingHyphenStyle(true);
        //表前缀
        strategy.setTablePrefix(pc.getModuleName() + "_");
        mpg.setStrategy(strategy);
        //mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }
//    public static void main(String[] args) {
//        newCode ();
//    }

}

