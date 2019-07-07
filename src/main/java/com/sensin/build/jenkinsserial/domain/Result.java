package com.sensin.build.jenkinsserial.domain;

import com.sensin.build.jenkinsserial.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author 無痕剑
 * @date 2019/7/6 0:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

	private Integer code;

	private String message;

	private Object data;

	public static Result success(Object data) {
		return build(ResultEnum.SUCCESS, data);
	}

	public static Result success() {
		return success(null);
	}

	public static Result fail(Integer code, String message, Object data) {
		return new Result(code, message, data);
	}

	public static Result fail(Integer code, String message) {
		return fail(code, message, null);
	}

	public static Result fail(BizException e) {
		return fail(e.getCode(), e.getMessage(), e.getData());
	}

	public static Result build(ResultEnum resultEnum, Object data) {
		return new Result(resultEnum.getCode(), resultEnum.getMessage(), data);
	}

	public static Result build(ResultEnum resultEnum) {
		return build(resultEnum, null);
	}

	public static Result error() {
		return build(ResultEnum.SYSTEM_ERROR);
	}

	@Getter
	public enum ResultEnum {

		HTTP_MEDIA_TYPE_NOT_SUPPORTED(994, "HTTP媒体类型不支持"),
		MULTIPART_EXCEPTION(995, "文件上传异常：{0}"),
		REQUEST_METHOD_NOT_SUPPORTED(996, "不支持该请求方法"),
		RESPONSE_DATA_FORMAT_ERROR(997, "响应参数格式错误"),
		REQUEST_PARAM_FORMAT_ERROR(997, "请求参数格式错误"),
		SERVLET_REQUEST_BINDING_EXCEPTION(998, "请求参数缺失或类型错误"),
		SYSTEM_ERROR(999, "system error"),

		SUCCESS(1000, "success"),

		PARAMETER_VALID_ERROR(1001, "参数校验不通过"),
		JOB_NO_MATCH_NAME(1002, "未获取到匹配名称[{0}]的job"),
		GIT_REPOSITORY_NOT_CONFIG(1003, "项目[{0}]未配置"),
		JENKINS_PROJECT_NOT_CONFIG(1004, "Jenkins项目[{0}]未配置"),
		GIT_REPOSITORY_NOT_MATCH_JENKINS_PROJECT(1004, "源项目[{0}]与Jenkins项目[{1}]不匹配"),
		JENKINS_PROJECT_BUILD_NUMBER_NOT_EXIST(1005, "Jenkins项目[{0}]构建编号[{1}]不存在"),
		GIT_REPOSITORY_AND_JENKINS_PROJECT_MAPPING_EXIST(1006, "Git源[{0}]与Jenkins工程[{1}]映射已存在"),
		JENKINS_PROJECT_ID_NOT_EXIST(1007, "Jenkins工程ID[{0}]不存在"),
		GIT_REPOSITORY_ID_NOT_EXIST(1008, "Git源工程ID[{0}]不存在"),

		;

		private final Integer code;

		private final String message;

		ResultEnum(Integer code, String message) {
			this.code = code;
			this.message = message;
		}
	}
}
