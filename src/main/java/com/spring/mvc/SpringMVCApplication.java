package com.spring.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.spring.mvc", "com.hibernate"})
@EntityScan(basePackages = "com.hibernate.entity")
@EnableJpaRepositories(basePackages = "com.hibernate.repository")
public class SpringMVCApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringMVCApplication.class, args);
	}

}
