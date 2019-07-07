package com.sensin.build.jenkinsserial;

import com.offbytwo.jenkins.model.BuildResult;
import com.sensin.build.jenkinsserial.service.JenkinsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JenkinsServiceTest {

	@Autowired
	private JenkinsService jenkinsService;

	@Test
	public void execJobTest() {
		String jobName = "test-serial";
		int buildNumber = jenkinsService.execJob(jobName);
		System.out.println(jobName + "构建编号为：" + buildNumber);
	}

	@Test
	public void getJobStatusTest() {
		System.out.println("*************************");
		BuildResult buildResult = jenkinsService.getBuildResult("test-serial", 2);
		System.out.println(buildResult);
		System.out.println("*************************");
	}
}
