package com.estar.marketing.admin.model.request;

import com.estar.marketing.admin.dao.entity.AccountEntity;

/**
 * @author xiaowenrou
 * @data 2022/8/9
 */
public record AccountListRequest(
        String account,
        String accountName,
        String businessName,
        Integer organizationId,
        String orderNumber,
        Integer active
) {
    public AccountEntity convertEntity() {
        return new AccountEntity(null,null,null,null, this.account(),null, this.accountName, this.businessName, this.organizationId, null,null,null, this.orderNumber, this.active,null);
    }
}