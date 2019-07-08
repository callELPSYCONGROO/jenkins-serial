package com.sensin.build.jenkinsserial.exception;

import com.sensin.build.jenkinsserial.domain.Result;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.text.MessageFormat;

/**
 * @author 無痕剑
 * @date 2019/7/6 15:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BizException extends RuntimeException {

	private Integer code;

	private String message;

	private Object data;

	private BizException(Integer code, String message, Object data) {
		super(message);
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static BizException build(Result.ResultEnum resultEnum) {
		return new BizException(resultEnum.getCode(), resultEnum.getMessage(), null);
	}

	public static BizException build(Result.ResultEnum resultEnum, Object... strings) {
		return new BizException(resultEnum.getCode(), MessageFormat.format(resultEnum.getMessage(), strings), null);
	}

	public static BizException build(Result resultDTO) {
		return new BizException(resultDTO.getCode(), resultDTO.getMessage(), resultDTO.getData());
	}
}
