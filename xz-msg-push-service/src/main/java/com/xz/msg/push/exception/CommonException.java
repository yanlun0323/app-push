package com.xz.msg.push.exception;

/**
 * 
 * @author Yan
 *
 */
public class CommonException extends BaseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -386555251356246067L;

	public CommonException(String errCode, String desc) {
        super(errCode, desc);
    }

    public CommonException(String errCode, String desc, Throwable causeBy) {
        super(errCode, desc, causeBy);
    }

    public CommonException(CommonErrCode errCode) {
        super(errCode.getCode(), errCode.getDesc());
    }

    public CommonException(CommonErrCode errCode, Throwable causeBy) {
        super(errCode.getCode(), errCode.getDesc(), causeBy);
    }

    public CommonException(CommonErrCode errCode, String desc) {
        super(errCode.getCode(), desc);
    }

    public CommonException(CommonErrCode errCode, String desc, Throwable causeBy) {
        super(errCode.getCode(), desc, causeBy);
    }

}

