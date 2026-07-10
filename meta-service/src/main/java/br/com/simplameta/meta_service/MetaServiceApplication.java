package br.com.simplameta.meta_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MetaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MetaServiceApplication.class, args);
	}

}
