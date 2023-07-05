package com.estar.marketing.admin.model.request;

import com.estar.marketing.admin.dao.entity.AccountEntity;
import org.hibernate.validator.constraints.Length;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * @author xiaowenrou
 * @data 2022/8/9
 */
public record AccountSaveRequest(

        Integer id,

        @NotBlank(message = "用户不能为空")
        @Length(min = 5, message = "用户名最少为5个字符")
        String account,

        String mobile,

        @NotBlank(message = "密码不能为空")
        @Length(min = 5, message = "密码最少为5个字符")
        String password,

        String accountName,

        @NotBlank(message = "业务类型不能为空")
        String businessName,

        @NotNull(message = "组织结构id不能为空")
        Integer organizationId,

        @NotBlank(message = "组织结构名不能为空")
        String organizationName,

        @NotNull(message = "绑定设备不能为空")
        Integer bindDevice,

        String deviceId,

        @NotBlank(message = "订单号不能为空")
        String orderNumber,

        @NotNull(message = "账号状态不能为空")
        Integer active,

        String type,

        List<Integer> accesses

) {
    public AccountEntity convertEntity() {
        var access = "";
        if (!CollectionUtils.isEmpty(this.accesses)) {
            var arr = new char[32];
            Arrays.fill(arr, '0');
            this.accesses.forEach(i -> arr[i - 1] = '1');
            access = new String(arr);
        }
        return new AccountEntity(this.id(),null,null,0, this.account(), this.mobile(), this.password(), this.accountName(), this.businessName(), this.organizationId(),
                this.organizationName(), this.bindDevice(),this.deviceId() == null ? "" : this.deviceId(), this.orderNumber(), this.active(),null, this.type(), access);
    }
}
