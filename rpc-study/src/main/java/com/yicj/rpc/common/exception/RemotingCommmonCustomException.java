package com.yicj.rpc.common.exception;

public class RemotingCommmonCustomException extends RemotingException {
	private static final long serialVersionUID = 1546308581637799641L;
	public RemotingCommmonCustomException(String message) {
        super(message, null);
    }
    public RemotingCommmonCustomException(String message, Throwable cause) {
        super(message, cause);
    }
}