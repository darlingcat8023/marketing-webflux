package com.estar.marketing.client.model.request;

import javax.validation.constraints.NotBlank;

/**
 * @author xiaowenrou
 * @date 2023/4/13
 */
public record MobileLoginRequest(

        @NotBlank(message = "手机号不能为空")
        String mobile,

        @NotBlank(message = "用户不能为空")
        String password

) {
}
