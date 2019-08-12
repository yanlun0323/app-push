package com.xz.msg.push.exception;

import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * 
 * @author Yan
 *
 */
public abstract  class BaseException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 264797136906752673L;
	
	private String errCode;
    private String errMsg;
    private Map<String, Object> ext;
    private Throwable causeBy;

    public BaseException(String errCode, String errMsg){
        super(errMsg + "(" + errCode + ")");
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public BaseException(String errCode, String errMsg, Throwable causeBy) {
        this(errCode, errMsg);
        this.causeBy = causeBy;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("************  Exception Start  ************").append("\r\n");
        buffer.append("Error Code: ").append(getString(getErrCode())).append("\r\n");
        buffer.append("Error Message: ").append(getString(getErrMsg())).append("\r\n");
        buffer.append("Description: ").append(getString(getMessage())).append("\r\n");
        if(!CollectionUtils.isEmpty(getExt())) {
            buffer.append("Ext: ").append(getExt()).append("\r\n");
        }
        if(getCauseBy() != null) {
            buffer.append("Cause By: ").append(getCauseBy().getClass()).append("\r\n");
        }
        buffer.append("************  Exception End  ************\r\n");
        return buffer.toString();
    }

    private String getString(String str) {
        if(str == null) return "";
        return str;
    }

    public Map<String, Object> getExt() {
        return ext;
    }

    public void setExt(Map<String, Object> ext) {
        this.ext = ext;
    }

    public Throwable getCauseBy() {
        return causeBy;
    }

    public void setCauseBy(Throwable causeBy) {
        this.causeBy = causeBy;
    }

}
