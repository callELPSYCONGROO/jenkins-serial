package com.sensin.build.jenkinsserial.controller;

import com.sensin.build.jenkinsserial.domain.command.ConfigGitRepositoryAndJobCommand;
import com.sensin.build.jenkinsserial.domain.Result;
import com.sensin.build.jenkinsserial.domain.group.InsertGroup;
import com.sensin.build.jenkinsserial.domain.group.UpdateGroup;
import com.sensin.build.jenkinsserial.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author 無痕剑
 * @date 2019/7/7 23:27
 */
@RestController
@RequestMapping("/config")
public class ConfigController {

	private final ConfigService configService;

	@Autowired
	public ConfigController(ConfigService configService) {
		this.configService = configService;
	}

	@PostMapping("/gitRepositoryAndJobMapping/insert")
	public Result gitRepositoryAndJobMappingInsert(@Validated(InsertGroup.class) @RequestBody ConfigGitRepositoryAndJobCommand command) {
		configService.gitRepositoryAndJobMappingInsert(command);
		return Result.success();
	}

	@PutMapping("/gitRepositoryAndJobMapping/update")
	public Result gitRepositoryAndJobMappingUpdate(@Validated(UpdateGroup.class) @RequestBody ConfigGitRepositoryAndJobCommand command) {
		configService.gitRepositoryAndJobMappingUpdate(command);
		return Result.success();
	}

	@GetMapping("/job/executing")
	public Result executingJob() {
		return Result.success(configService.executingJob());
	}
}
