package com.estar.marketing.admin.dao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * @author xiaowenrou
 * @date 2022/8/16
 */
@Table(value = "marketing_application_version")
public record ApplicationVersionEntity(

        @Id
        Integer id,

        @ReadOnlyProperty
        LocalDateTime createdAt,

        String system,

        String download

) {}
