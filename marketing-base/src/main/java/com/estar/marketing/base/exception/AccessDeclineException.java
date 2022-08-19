package com.estar.marketing.base.exception;

/**
 * @author xiaowenrou
 * @data 2022/8/11
 */
public class AccessDeclineException extends RuntimeException {

    private final String token;

    public AccessDeclineException(String message, String token) {
        super(message);
        this.token = token;
    }

    public String getOutPutMessage() {
        return this.getMessage();
    }

}
