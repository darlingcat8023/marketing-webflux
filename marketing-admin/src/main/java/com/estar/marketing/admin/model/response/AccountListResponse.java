package com.estar.marketing.admin.model.response;

import java.time.LocalDateTime;

/**
 * @author xiaowenrou
 * @data 2022/8/10
 */
public record AccountListResponse(
        Integer id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
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
