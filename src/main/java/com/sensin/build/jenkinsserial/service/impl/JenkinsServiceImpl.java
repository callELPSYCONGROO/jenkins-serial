package com.sensin.build.jenkinsserial.service.impl;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.*;
import com.sensin.build.jenkinsserial.domain.Result;
import com.sensin.build.jenkinsserial.exception.BizException;
import com.sensin.build.jenkinsserial.service.JenkinsService;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.UNKNOWN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author 無痕剑
 * @date 2019/7/6 0:53
 */
@Slf4j
@Service
public class JenkinsServiceImpl implements JenkinsService {

	private final JenkinsServer jenkinsServer;

	@Value("${jenkins.serial.jobRegex}")
	private String jobRegex;

	@Autowired
	public JenkinsServiceImpl(JenkinsServer jenkinsServer) {
		this.jenkinsServer = jenkinsServer;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int execJob(String jobName) {
		JobWithDetails jobWithDetails = getJobWithDetails(jobName);
		int buildNumber = jobWithDetails.getNextBuildNumber();
		log.debug("本次构建构建编号为[{}]", buildNumber);
		try {
			log.info("开始构建[{}]", jobName);
			jobWithDetails.build(true);
		} catch (IOException e) {
			log.error("构建任务[{}]失败，原因：", jobName, e);
			throw BizException.build(Result.ResultEnum.SYSTEM_ERROR);
		}

		return buildNumber;
	}

	@Override
	public BuildResult getBuildResult(String jobName, int buildNumber) {
		JobWithDetails jobWithDetails = getJobWithDetails(jobName);
		Build build = jobWithDetails.getBuildByNumber(buildNumber);
		if (build == null) {
			log.warn("Jenkins项目[{}]构建编号[{}]不存在", jobName, buildNumber);
			throw BizException.build(Result.ResultEnum.JENKINS_PROJECT_BUILD_NUMBER_NOT_EXIST, jobName, buildNumber);
		}

		BuildWithDetails buildWithDetails;
		try {
			buildWithDetails = build.details();
		} catch (IOException e) {
			log.error("获取Jenkins项目[{}]构建详情失败，原因：", jobName, e);
			throw BizException.build(Result.ResultEnum.SYSTEM_ERROR);
		}

		BuildResult buildResult = buildWithDetails.getResult();
		if (jobWithDetails.isInQueue() || buildResult == null) {
			buildResult = BuildResult.UNKNOWN;
		}

		log.info("Jenkins项目[{}]当前构建状态为[{}]", jobName, buildResult);
		return buildResult;
	}

	@Override
	public List<Job> getBuilding() {
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
			return null;
		}

		// 正在构建的Job
		return jobs.values().parallelStream()
				// 名称匹配正则
				.filter(job -> job.getName().matches(jobRegex))
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
	}

	private JobWithDetails getJobWithDetails(String jobName) {
		log.debug("根据名称[{}]获取job", jobName);
		JobWithDetails jobWithDetails;
		try {
			jobWithDetails = jenkinsServer.getJob(jobName);
		} catch (IOException e) {
			log.error("获取任务[{}]失败，原因：", jobName, e);
			throw BizException.build(Result.ResultEnum.SYSTEM_ERROR);
		}

		if (jobWithDetails == null) {
			log.warn("未获取到匹配名称[{}]的job", jobName);
			throw BizException.build(Result.ResultEnum.JOB_NO_MATCH_NAME, jobName);
		}
		return jobWithDetails;
	}
}
