package com.estar.marketing.admin.model.response;

import java.time.LocalDateTime;

/**
 * @author xiaowenrou
 * @data 2022/8/10
 */
public record LogListResponse(
        Integer id,
        LocalDateTime createdAt,
        LocalDateTime loginAt,
        Integer organizationId,
        String organizationName,
        String account,
        String accountName
) {}
