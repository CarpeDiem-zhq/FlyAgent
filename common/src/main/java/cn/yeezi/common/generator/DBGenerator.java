package cn.yeezi.common.generator;

import cn.hutool.core.collection.CollUtil;
import cn.yeezi.common.generator.engine.CustomTemplateEngine;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.Mapper;
import com.baomidou.mybatisplus.generator.config.builder.Service;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成器
 *
 * @author wanghh
 */
public class DBGenerator {

    @Data
    @Builder
    public static class GenerateSetting {

        //作者
        private String author;

        //数据库链接
        private String dbUrl;
        //数据库用户名
        private String dbUsername;
        //数据库密码
        private String dbPassword;


        //模块
        private String module;
        //父级目录
        private String parent;
        //生成目录
        private String outputDir;
        //模型类父级目录
        private String modelParent;

        //初始化core,service,controller
        @Builder.Default
        private boolean init = true;
        //生成core
        @Builder.Default
        private boolean core = true;
        //仅创建实体类
        private boolean onlyCreateEntity;
        //业务模块
        private List<BusinessModuleSetting> businessModules;
    }

    @Data
    @Builder
    public static class BusinessModuleSetting {

        //名称
        private String name;

        //模块
        private String module;

        //生成目录
        private String parent;
        //父级目录
        private String outputDir;

        @Builder.Default
        private boolean service = true;

        //生成controller
        @Builder.Default
        private boolean controller = true;

        //是否生成查询接口
        @Builder.Default
        private boolean query = true;
        //是否生成新增接口
        @Builder.Default
        private boolean add = true;
        //是否生成修改接口
        @Builder.Default
        private boolean edit = true;
        //是否生成删除接口
        @Builder.Default
        private boolean delete = true;
        //是否需要品牌id
        private boolean needBrandId;
        //是否需要用户id
        private boolean needUserId;
    }


    public static void generateTable(String table, GenerateSetting setting) {
        //数据库配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(setting.getDbUrl(), setting.getDbUsername(), setting.getDbPassword())
                .dbQuery(new MySqlQuery())
                .schema("mybatis-plus")
                .typeConvert(new MySqlTypeConvert())
                .keyWordsHandler(new MySqlKeyWordsHandler())
                .build();

        AutoGenerator basicGenerator = new AutoGenerator(dataSourceConfig);
        //包配置
        String parent = setting.getParent();
        PackageConfig packageConfig = new PackageConfig.Builder()
                .parent(parent)
                .entity("db.entity")
                .service("db.repository")
                .serviceImpl("db.repository.impl")
                .mapper("db.mapper")
                .xml("db.mapper.xml")
                .build();
        basicGenerator.packageInfo(packageConfig);

        boolean onlyCreateEntity = setting.isOnlyCreateEntity();
        //策略配置
        StrategyConfig strategyConfig = new StrategyConfig.Builder()
                .addInclude(table.split(","))
                .build();
        //Entity 策略配置
        strategyConfig.entityBuilder()
                .javaTemplate("/template/custom/entity.java")
                .disableSerialVersionUID()
                .enableChainModel()
                .enableLombok()
                .enableRemoveIsPrefix()
                .enableTableFieldAnnotation()
                .naming(NamingStrategy.underline_to_camel)
                .columnNaming(NamingStrategy.underline_to_camel)
                .formatFileName("%sEntity")
                .enableFileOverride()
                .build();
        //Mapper 策略配置
        Mapper.Builder mapperBuilder = strategyConfig.mapperBuilder();
        if (onlyCreateEntity) {
            mapperBuilder.disable();
        }
        mapperBuilder.build();
        //Service 策略配置
        Service.Builder servicebuilder = strategyConfig.serviceBuilder()
                .formatServiceFileName("%sRepository")
                .formatServiceImplFileName("%sRepositoryImpl")
                .enableFileOverride();
        if (onlyCreateEntity) {
            servicebuilder.disable();
        }
        servicebuilder.build();
        //Controller 策略配置
        strategyConfig.controllerBuilder()
                .disable().build();

        basicGenerator.strategy(strategyConfig);
        //初始化类
        boolean init = setting.isInit();
        String corePath = parent + StringPool.DOT + "core";
        String modelParent = setting.getModelParent();
        if(StringUtils.isNotBlank(modelParent)){
            corePath += StringPool.DOT + modelParent;
        }
        if (init || onlyCreateEntity) {
            //全局配置
            String projectPath = System.getProperty("user.dir");
            String module = setting.getModule();
            if (StringUtils.isNotBlank(module)) {
                projectPath += "/" + module;
            }
            String outputDir = projectPath + setting.outputDir;
            String author = setting.getAuthor();
            GlobalConfig globalConfig = new GlobalConfig.Builder()
                    .enableSwagger()
                    .disableOpenDir()
                    .outputDir(outputDir)
                    .author(author)
                    .dateType(DateType.TIME_PACK)
                    .commentDate("yyyy-MM-dd")
                    .build();
            basicGenerator.global(globalConfig);

            //注入配置
            Map<String, String> customFile = new HashMap<>();
            Map<String, Object> customMap = new HashMap<>();
            customFile.put(FileTypeEnum.ENTITY_GETTER.suffix, "/template/custom/entityGetter.java.ftl");
            if (setting.isCore()) {
                customFile.put(FileTypeEnum.CORE.suffix, "/template/custom/core.java.ftl");
                customMap.put("corePath", outputDir + File.separator + parent.replaceAll("\\.", StringPool.BACK_SLASH + File.separator) + File.separator + "core");
            }

            String finalCorePath = corePath;
            InjectionConfig injectionConfig = new InjectionConfig.Builder()
                    .beforeOutputFile((tableInfo, objectMap) -> {
                        objectMap.put("modelParent", modelParent);
                        objectMap.put("tableName", StringUtils.underlineToCamel(tableInfo.getName()));
                        objectMap.put("coreName", NamingStrategy.capitalFirst(StringUtils.underlineToCamel(tableInfo.getName())));
                        objectMap.put("corePackage", finalCorePath);
                    })
                    .customMap(customMap)
                    .customFile(customFile)
                    .build();
            basicGenerator.injection(injectionConfig);
            basicGenerator.execute(new CustomTemplateEngine());
            System.out.println("初始化完基础类");
        }

        //生成业务模块的service和controller
        List<BusinessModuleSetting> businessModules = setting.getBusinessModules();
        if (CollUtil.isNotEmpty(businessModules)) {
            for (BusinessModuleSetting businessModule : businessModules) {
                AutoGenerator businessGenerator = new AutoGenerator(dataSourceConfig);

                //包配置
                packageConfig = new PackageConfig.Builder()
                        .parent(parent)
                        .entity("db.entity")
                        .controller("controller")
                        .build();
                businessGenerator.packageInfo(packageConfig);
                String projectPath = System.getProperty("user.dir");
                String module = businessModule.getModule();
                if (StringUtils.isNotBlank(module)) {
                    projectPath += "/" + module;
                }
                String outputDir = projectPath + businessModule.getOutputDir();
                String author = setting.getAuthor();
                GlobalConfig globalConfig = new GlobalConfig.Builder()
                        .enableSwagger()
                        .disableOpenDir()
                        .outputDir(outputDir)
                        .author(author)
                        .dateType(DateType.TIME_PACK)
                        .commentDate("yyyy-MM-dd")
                        .build();
                businessGenerator.global(globalConfig);

                //策略配置
                strategyConfig = new StrategyConfig.Builder()
                        .addInclude(table.split(","))
                        .build();
                //Controller 策略配置
                String businessModuleName = businessModule.getName();
                strategyConfig.controllerBuilder()
                        .template("/template/custom/controller.java")
                        .enableRestStyle()
                        .enableHyphenStyle()
                        .enableFileOverride()
                        .formatFileName(businessModuleName + "%sController")
                        .build();
                //entity 策略配置
                strategyConfig.entityBuilder().disable().build();
                //mapper 策略配置
                strategyConfig.mapperBuilder().disable();
                //service
                strategyConfig.serviceBuilder().disable();
                businessGenerator.strategy(strategyConfig);

                //注入配置
                Map<String, Object> customMap = new HashMap<>();
                boolean query = businessModule.isQuery();
                customMap.put("query", query);
                boolean add = businessModule.isAdd();
                customMap.put("add", add);
                boolean edit = businessModule.isEdit();
                customMap.put("edit", edit);
                customMap.put("delete", businessModule.isDelete());
                customMap.put("isNeedBrandId", businessModule.isNeedBrandId());
                customMap.put("isNeedUserId", businessModule.isNeedUserId());

                Map<String, String> customFiles = new HashMap<>();
                if (businessModule.isService()) {
                    customFiles.put(FileTypeEnum.SERVICE.suffix, "/template/custom/service.java.ftl");
                    customMap.put("servicePath", outputDir + File.separator + parent.replaceAll("\\.", StringPool.BACK_SLASH + File.separator) + File.separator + "service");
                }

                String modelPath = outputDir + File.separator + parent.replaceAll("\\.", StringPool.BACK_SLASH + File.separator) + File.separator + "model";
                if (query) {
                    customFiles.put(FileTypeEnum.VO.suffix, "/template/custom/vo.java.ftl");
                    customFiles.put(FileTypeEnum.PARAM_PAGE.suffix, "/template/custom/paramPage.java.ftl");
                    customMap.put("modelPath", modelPath);
                }
                if (add) {
                    customFiles.put(FileTypeEnum.PARAM_ADD.suffix, "/template/custom/paramAdd.java.ftl");
                    customMap.put("modelPath", modelPath);
                }
                if (edit) {
                    customFiles.put(FileTypeEnum.PARAM_EDIT.suffix, "/template/custom/paramEdit.java.ftl");
                    customMap.put("modelPath", modelPath);
                }
                String finalCorePath1 = corePath;
                InjectionConfig injectionConfig = new InjectionConfig.Builder()
                        .beforeOutputFile((tableInfo, objectMap) -> {
                            objectMap.put("tableName", StringUtils.underlineToCamel(tableInfo.getName()));
                            String coreName = NamingStrategy.capitalFirst(StringUtils.underlineToCamel(tableInfo.getName()));
                            String classPrefix = businessModuleName + coreName;
                            objectMap.put("classPrefix", classPrefix);
                            objectMap.put("coreName", coreName);
                            objectMap.put("entityName", coreName + "Entity");
                            objectMap.put("corePackage", finalCorePath1);
                            objectMap.put("servicePackage", parent + StringPool.DOT + "service");
                            objectMap.put("modelParent", modelParent);
                            String paramPackage = parent + StringPool.DOT + "model" + StringPool.DOT + "param";
                            if (StringUtils.isNotBlank(modelParent)) {
                                paramPackage += StringPool.DOT + modelParent.replace("/", StringPool.DOT);
                            }
                            objectMap.put("paramPackage", paramPackage);
                            String voPackage = parent + StringPool.DOT + "model" + StringPool.DOT + "vo";
                            if (StringUtils.isNotBlank(modelParent)) {
                                voPackage += StringPool.DOT + modelParent.replace("/", StringPool.DOT);
                            }
                            objectMap.put("voPackage", voPackage);
                        })
                        .customMap(customMap)
                        .customFile(customFiles)
                        .build();
                businessGenerator.injection(injectionConfig);
                businessGenerator.execute(new CustomTemplateEngine());
                System.out.println("业务模块" + businessModuleName + "已生成完");
            }
        }
    }
}

