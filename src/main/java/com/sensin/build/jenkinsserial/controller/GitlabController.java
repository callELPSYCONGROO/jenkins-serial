package com.sensin.build.jenkinsserial.controller;

import com.sensin.build.jenkinsserial.domain.Result;
import com.sensin.build.jenkinsserial.domain.webhook.WebhookPushEventRequestBody;
import com.sensin.build.jenkinsserial.service.GitlabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author mayuhan
 * @date 2019/7/5 18:10
 */
@RestController
@RequestMapping("/gitlab")
public class GitlabController {

	private final GitlabService gitlabService;

	@Autowired
	public GitlabController(GitlabService gitlabService) {
		this.gitlabService = gitlabService;
	}

	@PostMapping("/webhook/{jobName}")
	public Result webhook(@PathVariable("jobName") String jobName,
	                      @RequestHeader("X-Gitlab-Event") String event,
	                      @RequestHeader(value = "X-Gitlab-Token", required = false) String token,// 目前暂时不需要
	                      @RequestBody WebhookPushEventRequestBody body) {
		gitlabService.handlerWebhook(jobName, event, token, body);
		return Result.success();
	}
}
