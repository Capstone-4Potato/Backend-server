package com.potato.balbambalbam.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "발밤발밤 API 명세서",
                version = "develop"
        )
)
@Configuration
public class SwaggerConfig {
}
