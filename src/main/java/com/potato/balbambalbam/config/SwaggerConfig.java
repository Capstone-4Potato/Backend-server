package com.potato.balbambalbam.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "발밤발밤 API 명세서",
                version = "develop"
        )
)
@Configuration
public class SwaggerConfig {
        @Bean
        public GroupedOpenApi homeApi() {
                return GroupedOpenApi.builder()
                        .group("HOME API")
                        .pathsToMatch("/home/**")
                        .build();
        }

        @Bean
        public GroupedOpenApi cardApi() {
                return GroupedOpenApi.builder()
                        .group("CARD API")
                        .pathsToMatch("/cards/**")
                        .build();
        }

        @Bean
        public GroupedOpenApi userApi() {
                return GroupedOpenApi.builder()
                        .group("USER API")
                        .pathsToMatch("/users/**")
                        .build();
        }

        @Bean
        public GroupedOpenApi testApi() {
                return GroupedOpenApi.builder()
                        .group("TEST API")
                        .pathsToMatch("/test/**")
                        .build();
        }

        @Bean
        public GroupedOpenApi reportApi() {
                return GroupedOpenApi.builder()
                        .group("REPORT API")
                        .pathsToMatch("/report/**")
                        .build();
        }

        @Bean
        public GroupedOpenApi notificationApi() {
                return GroupedOpenApi.builder()
                        .group("NOTIFICATION API")
                        .pathsToMatch("/notification/**")
                        .build();
        }

        @Bean
        public GroupedOpenApi allApi() {
                return GroupedOpenApi.builder()
                        .group("ALL API")
                        .pathsToMatch("/**")
                        .build();
        }
}
