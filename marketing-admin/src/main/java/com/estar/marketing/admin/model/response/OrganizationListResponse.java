package com.estar.marketing.admin.model.response;

import java.time.LocalDateTime;

/**
 * @author xiaowenrou
 * @data 2022/8/8
 */
public record OrganizationListResponse(
        Integer id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer deletedAt,
        String name
) {}
