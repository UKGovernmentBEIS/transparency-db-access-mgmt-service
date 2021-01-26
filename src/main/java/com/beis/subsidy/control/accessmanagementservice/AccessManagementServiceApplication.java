package com.beis.subsidy.control.accessmanagementservice;

import com.beis.subsidy.control.accessmanagementservice.utils.UserPrinciple;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.apache.catalina.filters.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestHeader;

@SpringBootApplication
public class AccessManagementServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccessManagementServiceApplication.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI(@Value("${application-description}") String appDesciption, @Value("${application-version}") String appVersion) {
        return new OpenAPI()
                .info(new Info()
                        .title("BEIS Subsidy Control - Access Management APIs")
                        .version("1.0")
                        .description("BEIS Subsidy Control - Access Management APIs for transparency database")
                        .termsOfService("http://swagger.io/terms/")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
