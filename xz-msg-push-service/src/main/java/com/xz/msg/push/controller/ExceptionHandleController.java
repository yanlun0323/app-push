package com.xz.msg.push.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NoPermissionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.alibaba.fastjson.JSON;
import com.xz.msg.push.entity.Result;
import com.xz.msg.push.exception.CommonException;

/**
 * 
 * @author Yan
 *
 */
@SuppressWarnings("deprecation")
@ControllerAdvice
public class ExceptionHandleController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandleController.class);

	/**
	 * 无效的 URL 请求 找不到处理该 URL 的 Controller
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = NoHandlerFoundException.class)
	@ResponseBody
	public ResponseEntity<Result> handleNoHandlerFoundException(NoHandlerFoundException ex) {
		Result entity = new Result(HttpStatus.NOT_FOUND.value(),
				"没有找到 [" + ex.getHttpMethod().toUpperCase() + " " + ex.getRequestURL() + "] 相应的处理器.");
		return getJSONResp(entity, HttpStatus.NOT_FOUND);
	}

	/**
	 * 无效的 URL 请求 找不到 Controller 中对应的方法
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = NoSuchRequestHandlingMethodException.class)
	@ResponseBody
	public ResponseEntity<Result> handleNoSuchRequestHandlingMethodException(NoSuchRequestHandlingMethodException ex) {
		Result entity = new Result(HttpStatus.NOT_FOUND.value(), "没有找到处理该请求的方法 [" + ex.getMethodName() + "].");
		return getJSONResp(entity, HttpStatus.NOT_FOUND);
	}

	/**
	 * 不支持的 Http Method
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseBody
	public ResponseEntity<Result> handleHttpRequestMethodNotSupportedException(
			HttpRequestMethodNotSupportedException ex) {
		StringBuffer supportedMethods = new StringBuffer();
		if (ex.getSupportedMethods() != null && ex.getSupportedMethods().length > 0) {
			for (int i = 0; i < ex.getSupportedMethods().length; i++) {
				if (i != 0) {
					supportedMethods.append(" | ");
				}
				supportedMethods.append(ex.getSupportedMethods()[i]);
			}
		}
		Result entity = new Result(HttpStatus.METHOD_NOT_ALLOWED.value(),
				"不支持的Http方法 [" + ex.getMethod().toUpperCase() + "], 请尝试 [" + supportedMethods.toString() + "].");
		return getJSONResp(entity, HttpStatus.METHOD_NOT_ALLOWED);
	}

	/**
	 * 不支持的 Http media type
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	@ResponseBody
	public ResponseEntity<Result> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
		Result entity = new Result(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
				"不支持的Http媒体类型 [" + ex.getContentType().toString() + "].");
		return getJSONResp(entity, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	@ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
	@ResponseBody
	public ResponseEntity<Result> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
		StringBuffer supportsList = new StringBuffer();
		if (ex.getSupportedMediaTypes() != null && ex.getSupportedMediaTypes().size() > 0) {
			for (int i = 0; i < ex.getSupportedMediaTypes().size(); i++) {
				if (i != 0) {
					supportsList.append(" | ");
				}
				supportsList.append(ex.getSupportedMediaTypes().get(i).toString());
			}
		}
		Result entity = new Result(HttpStatus.NOT_ACCEPTABLE.value(),
				"不可接受的Http媒体类型, 仅支持 [" + supportsList.toString() + "].");
		return getJSONResp(entity, HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * URL 缺少必要的请求参数
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseBody
	public ResponseEntity<Result> handleMissingServletRequestParameterException(
			MissingServletRequestParameterException ex) {
		Result entity = new Result(HttpStatus.BAD_REQUEST.value(), "URL参数 [" + ex.getParameterName() + "] 不能为空.");
		return getJSONResp(entity, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Header 缺少必要的参数
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(ServletRequestBindingException.class)
	@ResponseBody
	public ResponseEntity<Result> handleServletRequestBindingException(ServletRequestBindingException ex) {
		Result entity = new Result(HttpStatus.BAD_REQUEST.value(), "HEADER参数不能为空.");
		return getJSONResp(entity, HttpStatus.BAD_REQUEST);
	}

	/**
	 * 缺少 Request Part
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(MissingServletRequestPartException.class)
	@ResponseBody
	public ResponseEntity<Result> handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
		Result entity = new Result(HttpStatus.BAD_REQUEST.value(),
				"Request part [" + ex.getRequestPartName() + "] 不能为空.");
		return getJSONResp(entity, HttpStatus.BAD_REQUEST);
	}

	/**
	 * 请求体(Request Body)不可读, 或转换出错
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseBody
	public ResponseEntity<Result> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
		logger.error("HttpMessageNotReadableException -> {}", ex.getMessage());
		Result entity = new Result(HttpStatus.BAD_REQUEST.value(), "请求体(Request Body)不可读或转换出错");
		return getJSONResp(entity, HttpStatus.BAD_REQUEST);
	}

	/**
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(ConversionNotSupportedException.class)
	@ResponseBody
	public ResponseEntity<Result> handleConversionNotSupportedException(ConversionNotSupportedException ex) {
		Result entity = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"不支持的类型转换, 属性 [" + ex.getPropertyName() + "].");
		return getJSONResp(entity, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	@ResponseBody
	public ResponseEntity<Result> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		Map<String, Object> results = new HashMap<>();
		BindingResult bindingResult = ex.getBindingResult();
		List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		if (fieldErrors != null) {
			for (int i = 0; i < fieldErrors.size(); i++) {
				FieldError fieldError = fieldErrors.get(i);
				String value = ((fieldError.getRejectedValue() == null) ? "null"
						: fieldError.getRejectedValue().toString());
				String reason = ((fieldError.getDefaultMessage() == null) ? "" : fieldError.getDefaultMessage());
				String errorFieldMessage = "被拒绝的值 [" + value + "], 原因 [" + reason + "].";
				results.put(fieldError.getField(), errorFieldMessage);
			}
		}
		Result entity = new Result(HttpStatus.BAD_REQUEST.value(), "请求数据格式有误.");
		entity.setMessage(JSON.toJSONString(results));
		return getJSONResp(entity, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = { NoPermissionException.class })
	@ResponseBody
	public ResponseEntity<Result> handleNoPermissionException(NoPermissionException ex) {
		Result entity = new Result(HttpStatus.FORBIDDEN.value(), ex.getMessage());
		return getJSONResp(entity, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(value = { CommonException.class })
	@ResponseBody
	public ResponseEntity<Result> handleAuthKeyNotExistsException(CommonException ex) {
		Result entity = new Result(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
		return getJSONResp(entity, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(value = { RuntimeException.class, Exception.class, Throwable.class })
	@ResponseBody
	public ResponseEntity<Result> handleException(Throwable th) {
		logger.error("Internal server error -> {}", th);
		Result entity = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "出了点小问题,工程师正在努力抢修...");
		return getJSONResp(entity, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}