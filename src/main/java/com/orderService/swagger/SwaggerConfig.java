package com.orderService.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableWebMvc
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public Docket swaggerConfiguration() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(PathSelectors.ant("/error").negate())
                .build()
                .apiInfo(apiInfo());
        docket.useDefaultResponseMessages(false);
        return appendTags(docket);

    }
    private Docket appendTags(Docket docket) {
        return docket.tags(
                new Tag(DescriptionVariables.ORDERS,
                        "Used to get, create, edit and delete products"),
        new Tag(DescriptionVariables.ORDER_ITEM,
                "Used to get, create, edit and delete category")
                );
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Marker Service API")
                .description("Market Service API")
                .version("1.0")
                .build();
    }

}