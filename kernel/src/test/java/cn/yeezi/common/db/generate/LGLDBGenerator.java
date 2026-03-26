package cn.yeezi.common.db.generate;


import cn.yeezi.common.generator.DBGenerator;

import java.util.List;

class LGLDBGenerator {

    public static void main(String[] args) {
        List<DBGenerator.BusinessModuleSetting> businessModules = List.of(
                DBGenerator.BusinessModuleSetting
                        .builder()
                        .name("")
                        .module("webapp/manager")
                        .parent("cn.yeezi")
                        .outputDir("/src/main/java")
                        .service(true)
                        .controller(false)
                        .query(true)
                        .add(true)
                        .edit(true)
                        .delete(true)
                        .needUserId(false)
                        .build()
        );

        DBGenerator.GenerateSetting setting = DBGenerator.GenerateSetting.builder()
                .author("luoguoliang")
                .dbUrl("jdbc:mysql://rm-2zete046qc9k48828no.mysql.rds.aliyuncs.com:3306/hetaoyuan_test?useSSL=false")
                .dbUsername("yezi_dev")
                .dbPassword("AyCT&bNpDY$TJq(Z")
                .module("kernel")
                .parent("cn.yeezi")
                .outputDir("/src/main/java")
                .modelParent("advertise")
                .init(true)
                .core(true)
                .onlyCreateEntity(false)
//                .businessModules(businessModules)
                .build();
        DBGenerator.generateTable("sys_manager", setting);
    }
}
