package com.estar.marketing.base.exception;

import lombok.Getter;

/**
 * @author xiaowenrou
 * @data 2022/7/28
 */
@Getter
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
