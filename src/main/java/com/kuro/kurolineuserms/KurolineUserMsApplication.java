package com.kuro.kurolineuserms;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
public class KurolineUserMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(KurolineUserMsApplication.class, args);
	}
}
