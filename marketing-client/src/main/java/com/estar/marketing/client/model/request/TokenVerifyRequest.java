package com.estar.marketing.client.model.request;

/**
 * @author xiaowenrou
 * @data 2022/8/2
 */
public record TokenVerifyRequest(
        String source,
        String token
) {}
