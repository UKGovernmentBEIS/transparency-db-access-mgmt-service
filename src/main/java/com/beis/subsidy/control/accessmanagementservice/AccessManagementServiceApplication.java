package com.beis.subsidy.control.accessmanagementservice;

import com.beis.subsidy.control.accessmanagementservice.controller.feign.GraphAPIFeignClient;
import com.beis.subsidy.control.accessmanagementservice.controller.feign.GraphAPILoginFeignClient;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients(basePackages = {
        "com.beis.subsidy.control.accessmanagementservice.controller.feign" },
        basePackageClasses = { GraphAPIFeignClient.class, GraphAPILoginFeignClient.class }
)
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
