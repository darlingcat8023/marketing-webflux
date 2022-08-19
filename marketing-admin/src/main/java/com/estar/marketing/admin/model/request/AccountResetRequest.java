package com.estar.marketing.admin.model.request;

import javax.validation.constraints.NotNull;

/**
 * @author xiaowenrou
 * @data 2022/8/10
 */
public record AccountResetRequest(

        @NotNull(message = "用户名不能为空")
        String account,

        @NotNull(message = "密码不能为空")
        String password

) {}
