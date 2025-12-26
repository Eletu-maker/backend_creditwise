package com.creditwise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class CreditwiseBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditwiseBackendApplication.class, args);
	}

}