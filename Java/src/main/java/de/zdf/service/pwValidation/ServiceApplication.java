package de.zdf.service.pwValidation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = {"de.zdf.service.commons", "de.zdf.service.pwValidation"})
@EnableSwagger2
public class ServiceApplication extends WebMvcConfigurationSupport {
    @Value("${info.build.version}")
    private String version;

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    @Bean
    public Docket taggingApi() {
        return createDocket().select().apis(RequestHandlerSelectors.basePackage("de.zdf.service.pwValidation")).build();
    }

    private Docket createDocket() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Broker-Service API")
                .description("Webservice für den broker-service")
                .version(version)
                .build();
    }
}
