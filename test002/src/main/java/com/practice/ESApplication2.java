package com.practice;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//使@RabbitListener生效
@EnableRabbit
@SpringBootApplication
public class ESApplication2 {

	public static void main(String[] args) {
		SpringApplication.run(ESApplication2.class, args);
	}
}
