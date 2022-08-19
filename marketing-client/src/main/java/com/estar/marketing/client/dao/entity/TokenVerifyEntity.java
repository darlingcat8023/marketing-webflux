package com.estar.marketing.client.dao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

/**
 * @author xiaowenrou
 * @data 2022/7/29
 */
@Table(name = "marketing_verify")
public record TokenVerifyEntity(

        @Id
        Integer id,

        @ReadOnlyProperty
        Timestamp createdAt,

        @ReadOnlyProperty
        Timestamp updatedAt,

        Integer source,

        String token,

        String remoteAddress

) {}
