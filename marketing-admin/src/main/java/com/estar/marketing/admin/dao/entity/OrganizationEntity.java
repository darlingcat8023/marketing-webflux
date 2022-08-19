package com.estar.marketing.admin.dao.entity;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * @author xiaowenrou
 * @data 2022/8/5
 */
@Table(value = "marketing_organization")
public record OrganizationEntity(

        @Id
        Integer id,

        @ReadOnlyProperty
        LocalDateTime createdAt,

        @ReadOnlyProperty
        LocalDateTime updatedAt,

        Integer deletedAt,

        String name

) {}
