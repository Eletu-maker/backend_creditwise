package com.creditwise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
@EnableJpaAuditing
public class CreditwiseBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditwiseBackendApplication.class, args);
	}

}