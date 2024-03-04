package com.motomami;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.motomami") // 添加这一行，确保能够扫描到服务类
public class MotomamiAplication {

	public static void main(String[] args) {
		SpringApplication.run(MotomamiAplication.class, args);
	}

}
