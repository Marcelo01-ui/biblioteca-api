package com.example.biblioteca.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bibliotecaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Biblioteca API")
                        .description("API RESTful para cadastro, consulta e gerenciamento de livros e autores.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Grupo Biblioteca API")
                                .email("grupo@example.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
