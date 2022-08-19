package com.estar.marketing.client.model.request;

import javax.validation.constraints.NotBlank;

/**
 * @author xiaowenrou
 * @data 2022/8/11
 */
public record ClientCheckRequest(

        @NotBlank(message = "token不能为空")
        String token,

        @NotBlank(message = "设备号不能为空")
        String deviceId

) {}
