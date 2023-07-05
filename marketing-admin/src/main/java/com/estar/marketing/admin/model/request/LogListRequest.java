package com.estar.marketing.admin.model.request;

import lombok.Data;

/**
 * @author xiaowenrou
 * @data 2022/8/10
 */
@Data
public class LogListRequest {

    private String account;

    private Integer organizationId;

    private String startTime;

    private String endTime;

}
