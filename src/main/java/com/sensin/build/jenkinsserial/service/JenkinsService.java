package com.sensin.build.jenkinsserial.service;

import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.Job;

import java.util.List;

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

	/**
	 * 查询匹配jobRegex的Jenkins项目是否有正在构建的
	 * @return 正在构建的Jenkins项目集合
	 */
	List<Job> getBuilding();
}
