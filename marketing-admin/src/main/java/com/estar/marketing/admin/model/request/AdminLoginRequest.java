package com.estar.marketing.admin.model.request;

import com.estar.marketing.admin.AdminPropertiesConfiguration;

import javax.validation.constraints.NotBlank;

/**
 * @author xiaowenrou
 * @data 2022/7/29
 */
public record AdminLoginRequest(

        @NotBlank(message = "用户名不能为空")
        String name,

        @NotBlank(message = "密码不能为空")
        String password

) {

    public boolean equals(AdminPropertiesConfiguration.RootUser rootUser) {
        return this.name.equals(rootUser.name()) && this.password.equals(rootUser.password());
    }

}
