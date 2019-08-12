package com.xz.msg.push.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.xz.msg.push.entity.Result;

/**
 * @author 作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年4月14日 上午10:10:50
 */
public abstract class BaseController {

	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public ResponseEntity<String> getSucceedStringResp() {
		return getStringResp("Successfully.", HttpStatus.OK);
	}

	public ResponseEntity<String> getStringResp(String body, HttpStatus statusCode) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-Type", "text/plain;charset=utf-8");
		return new ResponseEntity<String>(body, responseHeaders, statusCode);
	}

	public ResponseEntity<Result> getSucceedJsonResp(String msg) {
		return getJSONResp(new Result(HttpStatus.OK.value(), msg), HttpStatus.OK);
	}

	public <T> ResponseEntity<T> getJSONResp(T body, HttpStatus statusCode) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-Type", "application/json;charset=utf-8");
		return new ResponseEntity<T>(body, responseHeaders, statusCode);
	}

	/**
	 * 获取RequestHeader并存储于Map
	 * 
	 * @param request
	 * @return
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	protected Map<String, String> getRequestHeaders(HttpServletRequest request) {
		Map<String, String> headerMap = new HashMap<>();

		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			headerMap.put(headerName, request.getHeader(headerName));
		}

		return headerMap;
	}
}
