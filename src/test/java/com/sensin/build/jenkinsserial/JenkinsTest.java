package com.sensin.build.jenkinsserial;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.Queue;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author 無痕剑
 * @date 2019/7/6 2:45
 */
public class JenkinsTest {

	@Test
	public void t() throws URISyntaxException, IOException {
		JenkinsServer jenkinsServer = new JenkinsServer(new URI("http://jenkins.sensin-tech.cn/jenkins"), "jenkins_serial", "jenkins_serial");
		System.out.println("*************************");

		Map<String, Job> jobs = jenkinsServer.getJobs();
		System.out.println("all jobs------------>\n" + jobs);

		Queue queue = jenkinsServer.getQueue();
		System.out.println("queue---------->\n" + queue);


		JobWithDetails job = jenkinsServer.getJob("test-serial2");
		System.out.print("所有构建编号------->");
		job.getBuilds().forEach(build -> System.out.println(build.getNumber()));
		Build build = job.getBuildByNumber(3);
		System.out.println("当前构建：" + build);

		System.out.println("*************************");
	}
}
