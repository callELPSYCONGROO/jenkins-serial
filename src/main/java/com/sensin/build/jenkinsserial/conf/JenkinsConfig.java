package com.sensin.build.jenkinsserial.conf;

import com.offbytwo.jenkins.JenkinsServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Administrator
 * @date 2019/7/6/006 16:02
 */
@Configuration
public class JenkinsConfig {

	@Value("${jenkins.url}")
	private String url;

	@Value("${jenkins.username}")
	private String username;

	@Value("${jenkins.password}")
	private String password;

	@Bean
	public JenkinsServer jenkinsServer() throws URISyntaxException {
		return new JenkinsServer(new URI(url), username, password);
	}
}
