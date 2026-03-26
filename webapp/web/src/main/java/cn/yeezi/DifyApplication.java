package cn.yeezi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"cn.yeezi"})
@MapperScan(basePackages = {"cn.yeezi.db.mapper"})
@ServletComponentScan
@EnableScheduling
@EnableAsync
public class DifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DifyApplication.class, args);
    }
}
