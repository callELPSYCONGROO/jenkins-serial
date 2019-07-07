package com.sensin.build.jenkinsserial.service;

import com.sensin.build.jenkinsserial.domain.command.ConfigGitRepositoryAndJobCommand;
import com.sensin.build.jenkinsserial.domain.dto.CurrentExecutingJobDTO;

/**
 * @author 無痕剑
 * @date 2019/7/7 23:33
 */
public interface ConfigService {

	void gitRepositoryAndJobMappingInsert(ConfigGitRepositoryAndJobCommand command);

	void gitRepositoryAndJobMappingUpdate(ConfigGitRepositoryAndJobCommand command);

	CurrentExecutingJobDTO executingJob();
}
