package com.sensin.build.jenkinsserial.domain.dto;

import com.sensin.build.jenkinsserial.domain.entity.GitRepository;
import com.sensin.build.jenkinsserial.domain.entity.JenkinsProject;
import com.sensin.build.jenkinsserial.domain.entity.JobExecQueue;
import lombok.Data;

/**
 * @author 無痕剑
 * @date 2019/7/8 2:00
 */
@Data
public class CurrentExecutingJobDTO {

	private GitRepository gitRepository;

	private JenkinsProject jenkinsProject;

	private JobExecQueue jobExecQueue;
}
