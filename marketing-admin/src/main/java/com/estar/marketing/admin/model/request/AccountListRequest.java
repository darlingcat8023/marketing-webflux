package com.estar.marketing.admin.model.request;

import com.estar.marketing.admin.dao.entity.AccountEntity;
import lombok.Data;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

/**
 * @author xiaowenrou
 * @data 2022/8/9
 */
@Data
public class AccountListRequest {

    private String account;

    private String accountName;

    private String businessName;

    private Integer organizationId;

    private String orderNumber;

    private Integer active;
    public AccountEntity convertEntity() {
        return new AccountEntity(null,null,null,null, this.account,null,null, this.accountName, this.businessName, this.organizationId, null,null,null, this.orderNumber, this.active,null, null, null);
    }

    public Example<AccountEntity> buildExample() {
        return Example.of(this.convertEntity(), ExampleMatcher.matching()
                .withMatcher("account", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("accountName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("orderNumber", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreNullValues());
    }

}