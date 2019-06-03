package com.miracle.cog.gateway.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan({ "com.miracle.database.connection", "com.miracle.controller", "com.miracle.config",
		"com.miracle.utility", "com.miracle.cog.gateway.*" })
@EnableSwagger2
@EnableAsync
//@EnableMongoRepositories({ "com.miracle.database.repositories" })
public class CogGatewayStarter extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(CogGatewayStarter.class, args);
	}

	@Override
	public SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(CogGatewayStarter.class);
	}
}
