package com.estar.marketing.admin.dao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * @author xiaowenrou
 * @data 2022/8/8
 */
@Table(value = "marketing_account")
public record AccountEntity(

        @Id
        Integer id,

        @ReadOnlyProperty
        LocalDateTime createdAt,

        @ReadOnlyProperty
        LocalDateTime updatedAt,

        @ReadOnlyProperty
        Integer deletedAt,

        String account,

        String password,

        String accountName,

        String businessName,

        Integer organizationId,

        String organizationName,

        Integer bindDevice,

        String deviceId,

        String orderNumber,

        Integer active,

        LocalDateTime activeTime

) {}
