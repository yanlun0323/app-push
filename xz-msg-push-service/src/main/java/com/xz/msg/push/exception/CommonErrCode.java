package com.xz.msg.push.exception;


/**
 * 
 * @author Yan
 *
 */
public enum CommonErrCode {

    NONE("000000", ""),
    ARGS_INVALID("400000", "请求参数有误"),

    NETWORK_ERROR("400500", "网络通讯故障"),
    BUSINESS("400600", "业务处理异常"),

    INTERNAL_SERVER_ERROR("500000", "服务器内部错误"),
    SERVICE_INVOKE_ERROR("500100", "服务调用出错"),

    UNKNOW_ERROR("999999", "网络超时或未知异常");

    String code;
    String desc;

    CommonErrCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
