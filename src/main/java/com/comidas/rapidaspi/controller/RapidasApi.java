package com.comidas.rapidaspi.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.comidas.rapidaspi.model")
@EnableJpaRepositories(basePackages = "com.comidas.rapidaspi.repository")
public class RapidasApi {

    public static void main(String[] args) {
        SpringApplication.run(RapidasApi.class, args);
    }

}