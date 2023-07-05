package com.estar.marketing.admin.model.request;

import com.estar.marketing.admin.dao.entity.AccountEntity;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author xiaowenrou
 * @data 2022/8/8
 */
public record AccountCheckRequest(
        @NotBlank(message = "用户名不能为空")
        @Length(min = 5, message = "用户名最少为5个字符")
        String account
) {
        public AccountEntity convertEntity() {
                return new AccountEntity(null, null, null,null, this.account(),null,null,null,null,null, null, null,null,null,null,null, null, null);
        }
}
