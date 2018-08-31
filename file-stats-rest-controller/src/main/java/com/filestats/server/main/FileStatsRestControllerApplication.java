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
<<<<<<< HEAD
<<<<<<< HEAD
=======
		//SpringApplication.run(FileStatsRestControllerApplication.class, args);
>>>>>>> 03541ebe9d722a882e85efdc5db02c16362a5e26
=======
>>>>>>> af2841811d2c1bb75ceaf19a1eb5aa24141fa74e
	}

}
