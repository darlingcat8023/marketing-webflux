package com.estar.marketing.base.exception;

import lombok.Getter;

/**
 * @author xiaowenrou
 * @data 2022/7/28
 */
@Getter
public class BusinessException extends RuntimeException {

    private Object blamedObject;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Object blamedObject) {
        super(message);
        this.blamedObject = blamedObject;
    }

    public BusinessException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
