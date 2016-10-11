package com.zuowei.utils.bridge.handler;

public class HandlerException extends RuntimeException {
    private static final long serialVersionUID = -1L;

    public HandlerException(String detailMessage) {
        super(detailMessage);
    }

    public HandlerException(Throwable throwable) {
        super(throwable);
    }

    public HandlerException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
