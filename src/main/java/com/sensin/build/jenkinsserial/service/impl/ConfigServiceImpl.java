package com.sensin.build.jenkinsserial.service.impl;

import com.sensin.build.jenkinsserial.domain.Result;
import com.sensin.build.jenkinsserial.domain.command.ConfigGitRepositoryAndJobCommand;
import com.sensin.build.jenkinsserial.domain.dto.CurrentExecutingJobDTO;
import com.sensin.build.jenkinsserial.domain.entity.GitRepository;
import com.sensin.build.jenkinsserial.domain.entity.JenkinsProject;
import com.sensin.build.jenkinsserial.domain.entity.JobExecQueue;
import com.sensin.build.jenkinsserial.domain.enums.StatusEnum;
import com.sensin.build.jenkinsserial.exception.BizException;
import com.sensin.build.jenkinsserial.mapper.GitRepositoryRepository;
import com.sensin.build.jenkinsserial.mapper.JenkinsProjectRepository;
import com.sensin.build.jenkinsserial.mapper.JobExecQueueRepository;
import com.sensin.build.jenkinsserial.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * @author 無痕剑
 * @date 2019/7/7 23:33
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

	private final GitRepositoryRepository gitRepositoryRepository;

	private final JenkinsProjectRepository jenkinsProjectRepository;

	private final JobExecQueueRepository jobExecQueueRepository;

	@Autowired
	public ConfigServiceImpl(GitRepositoryRepository gitRepositoryRepository,
	                         JenkinsProjectRepository jenkinsProjectRepository,
	                         JobExecQueueRepository jobExecQueueRepository) {
		this.gitRepositoryRepository = gitRepositoryRepository;
		this.jenkinsProjectRepository = jenkinsProjectRepository;
		this.jobExecQueueRepository = jobExecQueueRepository;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void gitRepositoryAndJobMappingInsert(ConfigGitRepositoryAndJobCommand command) {
		String pathWithNamespace = command.getPathWithNamespace();
		String job = command.getJob();
		modifyRepositoryAndJobMapping(pathWithNamespace, job);
		log.debug("创建新Jenkins项目[{}]", job);

		log.info("插入Git源[{}]与Jenkins项目[{}]关联", pathWithNamespace, job);
	}

	@Override
	public CurrentExecutingJobDTO executingJob() {
		JobExecQueue executingJob = jobExecQueueRepository.findTopByStatusOrderByCreateTimeAsc(StatusEnum.EXECUTING.getCode());
		if (executingJob == null) {
			return null;
		}

		Optional<JenkinsProject> optionalJenkinsProject = jenkinsProjectRepository.findById(executingJob.getJenkinsProjectId());
		if (!optionalJenkinsProject.isPresent()) {
			throw BizException.build(Result.ResultEnum.JENKINS_PROJECT_ID_NOT_EXIST, executingJob.getJenkinsProjectId());
		}

		JenkinsProject jenkinsProject = optionalJenkinsProject.get();
		Optional<GitRepository> optionalGitRepository = gitRepositoryRepository.findById(jenkinsProject.getGitRepositoryId());
		if (!optionalGitRepository.isPresent()) {
			throw BizException.build(Result.ResultEnum.GIT_REPOSITORY_ID_NOT_EXIST, executingJob.getJenkinsProjectId());
		}

		CurrentExecutingJobDTO currentExecutingJobDTO = new CurrentExecutingJobDTO();
		currentExecutingJobDTO.setGitRepository(optionalGitRepository.get());
		currentExecutingJobDTO.setJenkinsProject(jenkinsProject);
		currentExecutingJobDTO.setJobExecQueue(executingJob);
		return currentExecutingJobDTO;
	}

	private void modifyRepositoryAndJobMapping(String pathWithNamespace, String job) {
		Date now = new Date();
		GitRepository gitRepository = gitRepositoryRepository.findByPathWithNamespace(pathWithNamespace);
		if (gitRepository == null) {
			gitRepository = new GitRepository();
			gitRepository.setPathWithNamespace(pathWithNamespace);
			gitRepository.setCreateTime(now);
			gitRepositoryRepository.save(gitRepository);
			log.debug("创建新Git源[{}]", pathWithNamespace);
		}

		JenkinsProject jenkinsProject = jenkinsProjectRepository.findByJob(job);
		if (jenkinsProject != null) {
			log.warn("已存在[{}]与[{}]映射", pathWithNamespace, job);
			throw BizException.build(Result.ResultEnum.GIT_REPOSITORY_AND_JENKINS_PROJECT_MAPPING_EXIST, pathWithNamespace, job);
		}

		jenkinsProject = new JenkinsProject();
		jenkinsProject.setGitRepositoryId(gitRepository.getId());
		jenkinsProject.setJob(job);
		jenkinsProject.setCreateTime(now);
		jenkinsProjectRepository.save(jenkinsProject);
		log.info("创建新映射：Git源[{}]，Jenkins项目[{}]", pathWithNamespace, job);
	}
}
