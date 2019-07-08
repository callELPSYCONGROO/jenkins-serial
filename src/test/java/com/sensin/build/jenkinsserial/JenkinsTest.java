package com.sensin.build.jenkinsserial;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.Queue;
import com.sensin.build.jenkinsserial.domain.Result;
import com.sensin.build.jenkinsserial.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 無痕剑
 * @date 2019/7/6 2:45
 */
@Slf4j
public class JenkinsTest {

	private JenkinsServer jenkinsServer;

	@Before
	public void before() throws URISyntaxException {
		jenkinsServer = new JenkinsServer(new URI("http://jenkins.sensin-tech.cn/jenkins"), "jenkins_serial", "REsyne0aQgzDV*^f");
	}

	@Test
	public void t() throws IOException {
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

	@Test
	public void s() {
		// 搜索匹配jobRegex的工程
		Map<String, Job> jobs;
		try {
			jobs = jenkinsServer.getJobs();
		} catch (IOException e) {
			log.error("未获取到所有的Job，原因：", e);
			throw BizException.build(Result.ResultEnum.SYSTEM_ERROR);
		}
		if (CollectionUtils.isEmpty(jobs)) {
			log.info("Jenkins中项目为空");
			return;
		}

		// 正在构建的Job
		List<Job> collect = jobs.values().parallelStream()
				// 名称匹配正则
				.filter(job -> job.getName().matches("^front.+"))
				// 正在构建的项目
				.filter(job -> {
					try {
						JobWithDetails jobWithDetails = job.details();
						return jobWithDetails.isInQueue() || jobWithDetails.getLastBuild().details().isBuilding();
					} catch (IOException e) {
						log.error("获取Jenkins项目[{}]的构建状态时发生异常，原因：", job.getName(), e);
						return true;
					}
				})
				.collect(Collectors.toList());
		System.out.println("********************");
		System.out.println(collect);
		System.out.println("********************");
	}
}
