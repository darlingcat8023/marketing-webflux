package com.estar.marketing.admin.dao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * @author xiaowenrou
 * @data 2022/8/10
 */
@Table(value = "marketing_login_log")
public record LoginLogEntity(

        @Id
        Integer id,

        @ReadOnlyProperty
        LocalDateTime createdAt,

        @ReadOnlyProperty
        LocalDateTime loginAt,

        Integer organizationId,

        String organizationName,

        Integer accountId,

        String account,

        String accountName

) {}
