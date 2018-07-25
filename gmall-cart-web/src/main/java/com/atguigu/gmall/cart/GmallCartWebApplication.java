package com.atguigu.gmall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu.gmall")
public class GmallCartWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallCartWebApplication.class, args);
	}
}
