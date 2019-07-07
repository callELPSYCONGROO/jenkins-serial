package com.sensin.build.jenkinsserial.service;

import com.offbytwo.jenkins.model.BuildResult;

/**
 * @author 無痕剑
 * @date 2019/7/6 0:53
 */
public interface JenkinsService {

	/**
	 * 执行构建任务
	 * @param jobName Jenkins job name（project name）
	 * @return 构建任务编号
	 */
	int execJob(String jobName);

	/**
	 * 获得任务对应构建编号的构建状态
	 * @param jobName Jenkins job name（project name）
	 * @param buildNumber 构建编号
	 */
	BuildResult getBuildResult(String jobName, int buildNumber);
}
