package org.zhangqi.exception;

public class RpcErrorException extends Exception {

	private static final long serialVersionUID = -7443764564324073078L;

	protected int errorCode;

	public RpcErrorException(int errorCode) {
		super(String.valueOf(errorCode));
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
