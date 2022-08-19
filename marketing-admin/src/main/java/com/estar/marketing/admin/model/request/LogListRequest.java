package com.estar.marketing.admin.model.request;

/**
 * @author xiaowenrou
 * @data 2022/8/10
 */
public record LogListRequest(

        String account,

        Integer organizationId,

        String startTime,

        String endTime

) {}
