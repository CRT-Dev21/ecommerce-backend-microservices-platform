package com.ecommerce.crtdev.notification_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		System.out.println("IF THE SUN DON'T SHINE, ON ME TODAY, AND IF THE SUBWAYS FLOOD AND BRIDGES BREAK, WILL YOU LAY YOURSELF, DOWN AND DIVE YOUR GRAVE OR WILL YOU RAIL AGAINST THE DYING DAY");
	}

}
