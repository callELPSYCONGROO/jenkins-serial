package com.sensin.build.jenkinsserial.aop;

import com.sensin.build.jenkinsserial.domain.Result;
import com.sensin.build.jenkinsserial.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * controller层异常处理
 * @author: Overload
 * @review:
 * @date: 2018/6/28/0028 15:52
 */
@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    /**
     * 自定义异常
     */
    @ExceptionHandler(BizException.class)
    public Result BizExceptionHandler(BizException e) {
	    log.error(e.getMessage(), e);
        return Result.fail(e);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result constraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        String errorInfo = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessageTemplate)
                .collect(Collectors.joining(";"));
        return Result.fail(Result.ResultEnum.PARAMETER_VALID_ERROR.getCode(), errorInfo);
    }

    /**
     * 请求参数类型不匹配或转换错误
     */
    @ExceptionHandler(TypeMismatchException.class)
    public Result typeMismatchExceptionHandler(TypeMismatchException e) {
        log.error(e.getMessage(), e);
        return Result.build(Result.ResultEnum.SERVLET_REQUEST_BINDING_EXCEPTION);
    }

    /**
     * 文件上传请求异常
     */
    @ExceptionHandler(MultipartException.class)
    public Result multipartException(MultipartException e) {
        log.error(e.getMessage(), e);
        return Result.fail(Result.ResultEnum.MULTIPART_EXCEPTION.getCode(), e.getMessage());
    }

    /**
     * SpringMVC请求参数绑定异常
     */
    @ExceptionHandler(ServletRequestBindingException.class)
    public Result servletRequestBindingExceptionHandler(ServletRequestBindingException e) {
        log.error(e.getMessage(), e);
        return Result.build(Result.ResultEnum.SERVLET_REQUEST_BINDING_EXCEPTION);
    }

    /**
     * 请求参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        // 避免错误信息重复
        Set<String> errorMessageSet = new HashSet<>();
        Optional.of(e.getBindingResult()).ifPresent(bindingResult -> bindingResult.getAllErrors().forEach(err -> errorMessageSet.add(err.getDefaultMessage())));
        StringBuilder allErrorMessage = new StringBuilder();
        errorMessageSet.forEach(err -> allErrorMessage.append(err).append("；"));
        String msg = errorMessageSet.size() == 0 ? Result.ResultEnum.PARAMETER_VALID_ERROR.getMessage() : allErrorMessage.deleteCharAt(allErrorMessage.length() - 1).toString();
        return Result.fail(Result.ResultEnum.PARAMETER_VALID_ERROR.getCode(), msg);
    }

    @ExceptionHandler(BindException.class)
    public Result bindExceptionHandler(BindException e) {
        log.error(e.getMessage(), e);
        // 避免错误信息重复
        Set<String> errorMessageSet = new HashSet<>();
        e.getAllErrors().forEach(err -> errorMessageSet.add(err.getDefaultMessage()));
        StringBuilder allErrorMessage = new StringBuilder();
        errorMessageSet.forEach(err -> allErrorMessage.append(err).append("；"));
        if (allErrorMessage.toString().contains("ConversionFailedException")
                || allErrorMessage.toString().contains("ConverterNotFoundException")) {
            // Spring注解校验参数格式错误
            return Result.build(Result.ResultEnum.PARAMETER_VALID_ERROR);
        }
        String msg = errorMessageSet.size() == 0 ? Result.ResultEnum.PARAMETER_VALID_ERROR.getMessage() : allErrorMessage.deleteCharAt(allErrorMessage.length() - 1).toString();
        return Result.fail(Result.ResultEnum.PARAMETER_VALID_ERROR.getCode(), msg);
    }

    /**
     * 请求参数格式错误异常（http.body为空或为非json字符串）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return Result.build(Result.ResultEnum.REQUEST_PARAM_FORMAT_ERROR);
    }

    /**
     * HTTP媒体类型不支持
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error(e.getMessage(), e);
        return Result.build(Result.ResultEnum.HTTP_MEDIA_TYPE_NOT_SUPPORTED);
    }

    /**
     * 响应数据格式错误
     */
    @ExceptionHandler(HttpMessageNotWritableException.class)
    public Result httpMessageNotReadableExceptionHandler(HttpMessageNotWritableException e) {
        log.error(e.getMessage(), e);
        return Result.build(Result.ResultEnum.RESPONSE_DATA_FORMAT_ERROR);
    }

    /**
     * 不支持改请求方法
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        return Result.build(Result.ResultEnum.REQUEST_METHOD_NOT_SUPPORTED);
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return Result.build(Result.ResultEnum.SYSTEM_ERROR);
    }
}
