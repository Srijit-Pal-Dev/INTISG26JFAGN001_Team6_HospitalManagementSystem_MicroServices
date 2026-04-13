package com.cognizant.labService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.cognizant.labService.client")
@EnableDiscoveryClient
public class LabServiceApplication {

	public static void main(String[] args) {

        SpringApplication.run(LabServiceApplication.class, args);
	}

}
