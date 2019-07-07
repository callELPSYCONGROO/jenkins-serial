package com.sensin.build.jenkinsserial.domain.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * @author Administrator
 * @date 2019/7/6/006 18:06
 */
@Getter
public enum StatusEnum {

	INIT(1, "初始化"),
	EXECUTING(2, "执行中"),
	SUCCESS(3, "执行完成"),
	GIVE_UP(4, "放弃执行"),
	FAIL(5, "执行失败"),

	;

	private final Integer code;

	private final String info;

	StatusEnum(Integer code, String info) {
		this.code = code;
		this.info = info;
	}

	public static String getInfo(Integer code) {
		if (code == null) {
			return null;
		}
		for (StatusEnum statusEnum : StatusEnum.values()) {
			if (Objects.equals(statusEnum.code, code)) {
				return statusEnum.info;
			}
		}
		return null;
	}
}
