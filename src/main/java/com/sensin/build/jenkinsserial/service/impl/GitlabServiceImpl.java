package com.sensin.build.jenkinsserial.service.impl;

import com.sensin.build.jenkinsserial.domain.Result;
import com.sensin.build.jenkinsserial.domain.entity.GitRepository;
import com.sensin.build.jenkinsserial.domain.entity.JenkinsProject;
import com.sensin.build.jenkinsserial.domain.entity.JobExecQueue;
import com.sensin.build.jenkinsserial.domain.entity.OperationLog;
import com.sensin.build.jenkinsserial.domain.enums.StatusEnum;
import com.sensin.build.jenkinsserial.domain.webhook.WebhookPushEventRequestBody;
import com.sensin.build.jenkinsserial.exception.BizException;
import com.sensin.build.jenkinsserial.mapper.GitRepositoryRepository;
import com.sensin.build.jenkinsserial.mapper.JenkinsProjectRepository;
import com.sensin.build.jenkinsserial.mapper.JobExecQueueRepository;
import com.sensin.build.jenkinsserial.mapper.OperationLogRepository;
import com.sensin.build.jenkinsserial.service.GitlabService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

/**
 * @author 無痕剑
 * @date 2019/7/6 0:51
 */
@Slf4j
@Service
public class GitlabServiceImpl implements GitlabService {

	private final GitRepositoryRepository gitRepositoryRepository;

	private final JenkinsProjectRepository jenkinsProjectRepository;

	private final JobExecQueueRepository jobExecQueueRepository;

	private final OperationLogRepository operationLogRepository;

	@Autowired
	public GitlabServiceImpl(GitRepositoryRepository gitRepositoryRepository,
			                 JenkinsProjectRepository jenkinsProjectRepository,
	                         JobExecQueueRepository jobExecQueueRepository,
	                         OperationLogRepository operationLogRepository) {
		this.gitRepositoryRepository = gitRepositoryRepository;
		this.jenkinsProjectRepository = jenkinsProjectRepository;
		this.jobExecQueueRepository = jobExecQueueRepository;
		this.operationLogRepository = operationLogRepository;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void handlerWebhook(String jobName, String event, String token, WebhookPushEventRequestBody body) {
		log.info("收到项目[{}]的webhook", jobName);
		String pathWithNamespace = body.getProject().getPathWithNamespace();
		GitRepository gitRepository = gitRepositoryRepository.findByPathWithNamespace(pathWithNamespace);
		if (gitRepository == null) {
			log.warn("项目[{}]未配置", pathWithNamespace);
			throw BizException.build(Result.ResultEnum.GIT_REPOSITORY_NOT_CONFIG, pathWithNamespace);
		}

		JenkinsProject jenkinsProject = jenkinsProjectRepository.findByJob(jobName);
		if (jenkinsProject == null) {
			log.warn("Jenkins项目[{}]未配置", jobName);
			throw BizException.build(Result.ResultEnum.JENKINS_PROJECT_NOT_CONFIG, jobName);
		}

		if (!Objects.equals(gitRepository.getId(), jenkinsProject.getGitRepositoryId())) {
			log.warn("源项目[{}]与Jenkins项目[{}]不匹配", pathWithNamespace, jobName);
			throw BizException.build(Result.ResultEnum.GIT_REPOSITORY_NOT_MATCH_JENKINS_PROJECT, pathWithNamespace, jobName);
		}

		Date now = new Date();
		Integer execStatus = StatusEnum.INIT.getCode();
		JobExecQueue jobExecQueue = new JobExecQueue();
		jobExecQueue.setJenkinsProjectId(jenkinsProject.getId());
		jobExecQueue.setTriggerEvent(event);
		jobExecQueue.setStatus(execStatus);
		jobExecQueue.setCreateTime(now);
		jobExecQueueRepository.save(jobExecQueue);
		log.info("Jenkins任务[{}]进入构建队列", jobName);

		OperationLog operationLog = new OperationLog();
		operationLog.setExecStatus(execStatus);
		operationLog.setJobExecQueueId(jobExecQueue.getId());
		operationLog.setCreateTime(now);
		operationLogRepository.save(operationLog);
		log.info("项目[{}]构建初始化完成", pathWithNamespace);
	}
}
