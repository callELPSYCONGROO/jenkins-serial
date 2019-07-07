package com.sensin.build.jenkinsserial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaRepositories
@SpringBootApplication
public class JenkinsSerialApplication {

	public static void main(String[] args) {
		SpringApplication.run(JenkinsSerialApplication.class, args);
	}
}
