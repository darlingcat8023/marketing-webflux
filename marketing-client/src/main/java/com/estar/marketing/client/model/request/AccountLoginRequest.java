package com.estar.marketing.client.model.request;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author xiaowenrou
 * @data 2022/8/11
 */
public record AccountLoginRequest(

        @NotBlank(message = "用户不能为空")
        @Length(min = 5, message = "用户名最少为5个字符")
        String account,

        @NotBlank(message = "密码不能为空")
        @Length(min = 5, message = "密码最少为5个字符")
        String password,

        @NotBlank(message = "设备号不能为空")
        String deviceId

) {}
