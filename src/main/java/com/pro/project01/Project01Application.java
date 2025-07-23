package com.pro.project01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.pro.project01.v2")
@EnableJpaRepositories(basePackages = "com.pro.project01.v2")
@EntityScan(basePackages = "com.pro.project01.v2")
@ComponentScan(basePackages = "com.pro.project01.v2")
public class Project01Application {

    public static void main(String[] args) {
        SpringApplication.run(Project01Application.class, args);
    }

}
