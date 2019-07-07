package com.sensin.build.jenkinsserial.service.impl;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.sensin.build.jenkinsserial.domain.Result;
import com.sensin.build.jenkinsserial.exception.BizException;
import com.sensin.build.jenkinsserial.service.JenkinsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * @author 無痕剑
 * @date 2019/7/6 0:53
 */
@Slf4j
@Service
public class JenkinsServiceImpl implements JenkinsService {

	private final JenkinsServer jenkinsServer;

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
		log.info("Jenkins项目[{}]当前构建状态为[{}]", jobName, buildResult);
		return buildResult;
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
