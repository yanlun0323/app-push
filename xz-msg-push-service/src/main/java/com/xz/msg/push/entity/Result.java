package com.xz.msg.push.entity;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 表示执行/查询结果的通用结构
 * @author Yan
 *
 */
public class Result {

    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static int DEFAULT_SUCCESS = 0;

    private static int DEFAULT_FAIL = -1;

    private int resultCode;     // 结果代码，通常 0 表示成功

    private String message;      // 相关信息，通常用于展示错误信息

    private Map<String, Object> data;   // 返回值

    private Throwable exception;        // 相关异常信息

    ////////////////////////////////////////////////////////////////

    public Result() {
    }

    public Result(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public Result(int resultCode, String message, Throwable exception) {
        this.resultCode = resultCode;
        this.message = message;
        this.exception = exception;
    }

    /**
     * 获得缺省的失败代码，注意这个设置是全局的
     *
     * @return 缺省的失败代码
     */
    public static int getDefaultFail() {
        return DEFAULT_FAIL;
    }

    /**
     * 获得缺省的失败代码，注意这个设置是全局的
     *
     * @param defaultFail 缺省的失败代码
     */
    public static void setDefaultFail(int defaultFail) {
        DEFAULT_FAIL = defaultFail;
    }

    public static int getDefaultSuccess() {
        return DEFAULT_SUCCESS;
    }

    public static void setDefaultSuccess(int defaultSuccess) {
        Result.DEFAULT_SUCCESS = defaultSuccess;
    }

    public static Result success() {
        return new Result();
    }

    public static Result success(String message) {
        return new Result(DEFAULT_SUCCESS, message);
    }

    public static Result fail(String message) {
        return fail(DEFAULT_FAIL, message, null);
    }

    public static Result fail(int resultCode, String message) {
        return fail(resultCode, message, null);
    }

    public static Result fail(int resultCode, String message, Throwable exception) {
        if (resultCode == DEFAULT_SUCCESS) {
            throw new IllegalArgumentException("失败的代码不能与 DEFAULT_SUCCESS 相同");
        }
        return new Result(resultCode, message, exception);
    }

    ////////////////////////////////////////////////////////////////

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /**
     * 设置值
     *
     * @param key   键
     * @param value 值
     *
     * @return Result 对象自身
     */
    public Result set(String key, Object value) {
        if (this.data == null) {
            this.data = new HashMap<>();
        }
        this.data.put(key, value);
        return this;
    }

    /////////////////////////////////////////////////////////////

    /**
     * 获得值
     *
     * @param key 键
     * @param <T> 值类型
     *
     * @return 值，如果没有则返回 null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return this.data == null ? null : (T) this.data.get(key);
    }

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

}
