package com.filestats.server.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan({"controller","service"})
public class FileStatsRestControllerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileStatsRestControllerApplication.class, args);
	}
}
