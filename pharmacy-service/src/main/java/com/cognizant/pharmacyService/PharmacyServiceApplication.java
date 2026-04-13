package com.cognizant.pharmacyService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@ComponentScan("com.cognizant.pharmacyService")
public class PharmacyServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(PharmacyServiceApplication.class, args);
	}
}