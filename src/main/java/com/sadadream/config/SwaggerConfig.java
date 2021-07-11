package com.sadadream.config;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
=======
import java.util.Arrays;
import java.util.List;
>>>>>>> 5864838f30262978eaf80993bc9af52d8bbd0ac6

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

<<<<<<< HEAD
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
=======
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
>>>>>>> 5864838f30262978eaf80993bc9af52d8bbd0ac6
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

<<<<<<< HEAD
    private static final Contact DEFAULT_CONTACT = new Contact("Dong Wook Lee", "https://dongwooklee96.github.io/",
        "sh95119@gmail.com");

    private static final ApiInfo DEFAULT_API_INFO = new ApiInfo("사다드림 API V1",
        "My User management REST API service", "1.0", "urn:tos",
        DEFAULT_CONTACT, "Apache 2.0", "https://apache.org/licenses/LICENSE-2.0", new ArrayList<>());

    private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = new HashSet<>(
        Arrays.asList("application/json", "application/xml"));

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
=======
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .useDefaultResponseMessages(false)
>>>>>>> 5864838f30262978eaf80993bc9af52d8bbd0ac6
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
<<<<<<< HEAD
            .apiInfo(DEFAULT_API_INFO)
            .produces(DEFAULT_PRODUCES_AND_CONSUMES)
            .consumes(DEFAULT_PRODUCES_AND_CONSUMES);
=======
            .apiInfo(metaData())
            .securityContexts(Arrays.asList(securityContext()))
            .securitySchemes(Arrays.asList(apiKey()));
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder()
            .title("사다드림 API V2")
            .description("사다드림 REST API V2 입니다.")
            .version("0.0.1")
            .termsOfServiceUrl("Terms of Service")
            .contact(new Contact("Dong Wook Lee", "https://dongwooklee96.github.io/",
                "sh95119@gmail.com"))
            .license("Apache License Version 2.0")
            .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
            .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return springfox
            .documentation
            .spi.service
            .contexts
            .SecurityContext
            .builder()
            .securityReferences(defaultAuth()).forPaths(PathSelectors.any()).build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
>>>>>>> 5864838f30262978eaf80993bc9af52d8bbd0ac6
    }
}
