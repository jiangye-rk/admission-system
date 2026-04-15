package com.admission;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.admission.mapper")
@EnableCaching
public class AdmissionApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdmissionApplication.class, args);
    }
}