package com.filestats.server.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan({"controller","model"})
public class FileStatsRestControllerApplication {

	public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(FileStatsRestControllerApplication.class);
        builder.headless(false).run(args);
	}

}
