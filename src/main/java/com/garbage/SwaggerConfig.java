package com.garbage;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2 // Swagger的开关，表示已经启用Swagger
public class SwaggerConfig {
    @Bean
    public Docket api() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .pathMapping("/")
                .select() // 选择哪些路径和api会生成document
//                .apis(RequestHandlerSelectors.any())// 对所有api进行监控
//                .apis(RequestHandlerSelectors.basePackage("com.garbage.controller"))// 选择监控的package
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))// 只监控有ApiOperation注解的接口
                .build();
        return docket;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Garbage接口文档")
                .description("这是用Swagger动态生成的接口文档")
                .termsOfServiceUrl("NO terms of service")
                .license("The Apache License, Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0.html")
                .version("1.0")
                .build();
    }
}