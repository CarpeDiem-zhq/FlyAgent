package cn.yeezi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wanghh
 * @since 2024-01-04
 */
@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DIFY API")
                        .contact(new Contact().name("zhanghq"))
                        .version("0.0.1")
                        .description( "DIFY API")
                        .license(new License().name("Apache 2.0"))
                );
    }

}
