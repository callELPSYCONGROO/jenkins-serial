package com.sensin.build.jenkinsserial.service;

import com.sensin.build.jenkinsserial.domain.webhook.WebhookPushEventRequestBody;

/**
 * @author 無痕剑
 * @date 2019/7/6 0:51
 */
public interface GitlabService {

	/**
	 * 处理webhook请求数据
	 * @param jobName Jenkins项目
	 * @param event 发起事件
	 * @param token 请求token
	 * @param body gitlab请求体数据
	 */
	void handlerWebhook(String jobName, String event, String token, WebhookPushEventRequestBody body);
}
