package com.cognizant.prescriptionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.cognizant.prescriptionservice.client")
public class PrescriptionserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrescriptionserviceApplication.class, args);
    }
}