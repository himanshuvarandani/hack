package com.application.hack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages="com.application")
@EnableAutoConfiguration
@EntityScan("com.application.entity")
@EnableJpaRepositories("com.application.repository")
public class HackApplication {

	public static void main(String[] args) {
		SpringApplication.run(HackApplication.class, args);
	}

}
