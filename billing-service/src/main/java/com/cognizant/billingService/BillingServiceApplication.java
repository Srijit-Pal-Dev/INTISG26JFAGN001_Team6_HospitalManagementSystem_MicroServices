package com.cognizant.billingService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.cognizant.billingService.client")
@EnableDiscoveryClient
public class BillingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingServiceApplication.class, args);
	}

}
