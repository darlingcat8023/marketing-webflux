package com.estar.marketing.admin.model.response;

import com.estar.marketing.admin.dao.entity.AccountEntity;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        String mobile,
        String password,
        String accountName,
        String businessName,
        Integer organizationId,
        String organizationName,
        Integer bindDevice,
        String deviceId,
        String orderNumber,
        Integer active,
        LocalDateTime activeTime,
        String type,
        String access,
        List<Integer> accesses
) {

    public AccountListResponse(AccountEntity entity) {
        this(entity.id(), entity.createdAt(), entity.updatedAt(), entity.deletedAt(), entity.account(), entity.mobile(), entity.password(), entity.accountName(), entity.businessName(), entity.organizationId(), entity.organizationName(), entity.bindDevice(), entity.deviceId(), entity.orderNumber(), entity.active(), entity.activeTime(), entity.type(), entity.access(), null);
    }

    public AccountListResponse(Integer id, LocalDateTime createdAt, LocalDateTime updatedAt, Integer deletedAt, String account, String mobile, String password, String accountName, String businessName, Integer organizationId, String organizationName, Integer bindDevice, String deviceId, String orderNumber, Integer active, LocalDateTime activeTime, String type, String access, List<Integer> accesses) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.account = account;
        this.mobile = mobile;
        this.password = password;
        this.accountName = accountName;
        this.businessName = businessName;
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.bindDevice = bindDevice;
        this.deviceId = deviceId;
        this.orderNumber = orderNumber;
        this.active = active;
        this.activeTime = activeTime;
        this.type = type;
        this.access = access;
        if (StringUtils.hasText(access)) {
            var list = new ArrayList<Integer>();
            var arr = access.toCharArray();
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == '1') {
                    list.add(i + 1);
                }
            }
            this.accesses = list;
        } else {
            this.accesses = List.of();
        }
    }
}
