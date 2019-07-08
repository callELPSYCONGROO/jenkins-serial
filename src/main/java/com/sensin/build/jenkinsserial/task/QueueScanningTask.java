package com.sensin.build.jenkinsserial.task;

import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.Job;
import com.sensin.build.jenkinsserial.domain.Result;
import com.sensin.build.jenkinsserial.domain.entity.JenkinsProject;
import com.sensin.build.jenkinsserial.domain.entity.JobExecQueue;
import com.sensin.build.jenkinsserial.domain.entity.OperationLog;
import com.sensin.build.jenkinsserial.domain.enums.StatusEnum;
import com.sensin.build.jenkinsserial.exception.BizException;
import com.sensin.build.jenkinsserial.mapper.GitRepositoryRepository;
import com.sensin.build.jenkinsserial.mapper.JenkinsProjectRepository;
import com.sensin.build.jenkinsserial.mapper.JobExecQueueRepository;
import com.sensin.build.jenkinsserial.mapper.OperationLogRepository;
import com.sensin.build.jenkinsserial.service.JenkinsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Administrator
 * @date 2019/7/6/006 18:20
 */
@Slf4j
@Component
public class QueueScanningTask {

	private final GitRepositoryRepository gitRepositoryRepository;

	private final JenkinsProjectRepository jenkinsProjectRepository;

	private final JobExecQueueRepository jobExecQueueRepository;

	private final OperationLogRepository operationLogRepository;

	private final JenkinsService jenkinsService;

	@Autowired
	public QueueScanningTask(GitRepositoryRepository gitRepositoryRepository,
	                         JenkinsProjectRepository jenkinsProjectRepository,
	                         JobExecQueueRepository jobExecQueueRepository,
	                         OperationLogRepository operationLogRepository,
	                         JenkinsService jenkinsService) {
		this.gitRepositoryRepository = gitRepositoryRepository;
		this.jenkinsProjectRepository = jenkinsProjectRepository;
		this.jobExecQueueRepository = jobExecQueueRepository;
		this.operationLogRepository = operationLogRepository;
		this.jenkinsService = jenkinsService;
	}

	@Scheduled(fixedDelay = 3000)
	@Transactional(rollbackFor = Exception.class)
	public void execJob() {
		// 查看当前是否有正在执行的任务
		Integer executingStatusCode = StatusEnum.EXECUTING.getCode();
		JobExecQueue executingJob = jobExecQueueRepository.findTopByStatusOrderByCreateTimeAsc(executingStatusCode);
		if (executingJob != null) {
			Optional<JenkinsProject> optionalJenkinsProject = jenkinsProjectRepository.findById(executingJob.getJenkinsProjectId());
			if (optionalJenkinsProject.isPresent()) {
				log.info("目前任务[{}]正在执行构建[#{}]",
						optionalJenkinsProject.get().getJob(),
						executingJob.getBuildNumber());
			} else {
				log.warn("未获取到JobExecQueue.id=[{}]记录的JenkinsProject.id[{}]，请检查",
						executingJob.getId(),
						executingJob.getJenkinsProjectId());
			}
			return;
		}

		List<Job> jobList = jenkinsService.getBuilding();
		if (!CollectionUtils.isEmpty(jobList)) {
			log.info("有未经本系统的构建正在触发");
			return;
		}

		// 查询第一个初始化的任务
		JobExecQueue initJob = jobExecQueueRepository.findTopByStatusOrderByCreateTimeAsc(StatusEnum.INIT.getCode());
		if (initJob == null) {
			log.debug("当前没有初始化的任务");
			return;
		}

		// 执行第一个初始化任务
		Optional<JenkinsProject> optionalJenkinsProject = jenkinsProjectRepository.findById(initJob.getJenkinsProjectId());
		Long initJobExecId = initJob.getId();
		if (!optionalJenkinsProject.isPresent()) {
			log.warn("未获取到JobExecQueue.id=[{}]记录的JenkinsProject.id[{}]，请检查", initJobExecId, initJob.getJenkinsProjectId());
			return;
		}

		// 执行任务
		Integer buildNumber;
		try {
			buildNumber = jenkinsService.execJob(optionalJenkinsProject.get().getJob());
		} catch (BizException e) {
			// 业务异常跳过
			return;
		} catch (Exception e) {
			// 其他异常记录日志
			log.error("执行任务发生异常，原因：", e);
			return;
		}

		// 任务成功
		initJob.setStatus(executingStatusCode);
		Date now = new Date();
		initJob.setStartExecTime(now);
		initJob.setBuildNumber(buildNumber);
		jobExecQueueRepository.save(initJob);
		log.info("任务jobExecQueue.id[{}]状态变更为执行中", initJobExecId);

		OperationLog operationLog = new OperationLog();
		operationLog.setJobExecQueueId(initJobExecId);
		operationLog.setCreateTime(now);
		operationLog.setExecStatus(executingStatusCode);
		operationLogRepository.save(operationLog);
	}

	@Scheduled(fixedDelay = 10000)
	@Transactional(rollbackFor = Exception.class)
	public void endJob() {
		// 查看当前是否有正在执行的任务
		Integer executingStatusCode = StatusEnum.EXECUTING.getCode();
		List<JobExecQueue> executingQueue = jobExecQueueRepository.findByStatus(executingStatusCode);
		if (CollectionUtils.isEmpty(executingQueue)) {
			log.debug("当前没有正在执行的任务");
			return;
		}

		/*
		 * 尽管业务上只会有一个任务正在执行，
		 * 但是不排除手动修改数据库或其他异常情况时，
		 * 可能有多个任务正在执行，
		 * 所以每个任务都进行一次查看状态并关闭的操作
		 */
		for (JobExecQueue jobExecQueue : executingQueue) {
			Optional<JenkinsProject> optionalJenkinsProject = jenkinsProjectRepository.findById(jobExecQueue.getJenkinsProjectId());
			if (!optionalJenkinsProject.isPresent()) {
				log.warn("未获取到JobExecQueue.id=[{}]记录的JenkinsProject.id[{}]，请检查", jobExecQueue.getId(), jobExecQueue.getJenkinsProjectId());
				throw BizException.build(Result.ResultEnum.SYSTEM_ERROR);
			}
			Integer buildNumber = jobExecQueue.getBuildNumber();
			String jobName = optionalJenkinsProject.get().getJob();
			BuildResult buildResult;
			try {
				buildResult = jenkinsService.getBuildResult(jobName, buildNumber);
			} catch (BizException e) {
				continue;
			} catch (Exception e) {
				log.error("获取JobExecQueue.id=[{}]记录的JenkinsProject.id[{}]的构建状态发生异常，原因：", jobExecQueue.getId(), jobExecQueue.getJenkinsProjectId(), e);
				continue;
			}

			if (buildResult == null) {
				log.warn("未获取到JobExecQueue.id=[{}]的构建结果", jobExecQueue.getId());
				continue;
			}

			/*
			 * 可以任务这里查询到的任务至少是已经启动构建了的，所以不存在NOT_BUILT；
			 * SUCCESS表示成功；
			 * BUILDING表示执行中，REBUILDING可能是由于扫描间隔出现，那么还是当作执行中；
			 * FAILURE, UNSTABLE表示构建失败；
			 * ABORTED，CANCELLED表示放弃执行。
			 */
			boolean update = dealBuildResult(jobExecQueue, buildResult, jobName, buildNumber);
			if (update) {
				// 更新状态任务状态
				jobExecQueueRepository.save(jobExecQueue);
			}
		}
	}

	/**
	 * 处理构建结果
	 * @param jobExecQueue 构建数据
	 * @param buildResult 构建结果
	 * @return 是否更新构建数据
	 */
	private boolean dealBuildResult(JobExecQueue jobExecQueue, BuildResult buildResult, String jobName, Integer buildNumber) {
		boolean update;
		Date now = new Date();
		switch (buildResult) {
			case SUCCESS:
				jobExecQueue.setStatus(3);
				jobExecQueue.setEndExecTime(now);
				update = true;
				log.info("Jenkins项目[{}]构建编号[{}]构建完成", jobName, buildNumber);
				break;
			case BUILDING:
			case REBUILDING:
				update = false;
				log.debug("Jenkins项目[{}]构建编号[{}]正在构建中", jobName, buildNumber);
				break;
			case FAILURE:
			case UNSTABLE:
				jobExecQueue.setStatus(5);
				jobExecQueue.setEndExecTime(now);
				update = true;
				log.info("Jenkins项目[{}]构建编号[{}]构建失败", jobName, buildNumber);
				break;
			case ABORTED:
			case CANCELLED:
				jobExecQueue.setStatus(4);
				jobExecQueue.setEndExecTime(now);
				update = true;
				log.info("Jenkins项目[{}]构建编号[{}]放弃构建", jobName, buildNumber);
				break;
			case UNKNOWN:
				update = false;
				log.debug("Jenkins项目[{}]构建编号[{}]构建结果未知", jobName, buildNumber);
				break;
			default:
				throw BizException.build(Result.ResultEnum.SYSTEM_ERROR);
		}
		return update;
	}
}
