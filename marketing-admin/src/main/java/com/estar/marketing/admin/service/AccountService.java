package com.estar.marketing.admin.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.estar.marketing.admin.dao.AccountRepository;
import com.estar.marketing.admin.dao.entity.AccountEntity;
import com.estar.marketing.admin.model.request.AccountCheckRequest;
import com.estar.marketing.admin.model.request.AccountListRequest;
import com.estar.marketing.admin.model.request.AccountResetRequest;
import com.estar.marketing.admin.model.request.AccountSaveRequest;
import com.estar.marketing.admin.model.response.AccountListResponse;
import com.estar.marketing.base.ByteArrayInOutStream;
import com.estar.marketing.base.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * @author xiaowenrou
 * @data 2022/8/8
 */
@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Mono<Boolean> checkAccount(Mono<AccountCheckRequest> requestMono) {
        Function<AccountEntity, Example<AccountEntity>> function = entity -> Example.of(entity, ExampleMatcher.matching().withIgnoreNullValues());
        return requestMono.map(function.compose(AccountCheckRequest::convertEntity))
                .flatMap(this.accountRepository::exists);
    }

    public Mono<Integer> saveAccount(Mono<AccountSaveRequest> requestMono) {
        return requestMono.map(AccountSaveRequest::convertEntity)
                .flatMap(request -> this.accountRepository.findByAccount(request.account())
                        .doOnNext(model -> {
                            if (request.id() == null || !model.id().equals(request.id())) {
                                throw new BusinessException("account已经存在");
                            }
                        })
                        .then(this.accountRepository.save(request))
                        .map(AccountEntity::id)
                );
    }

    public Mono<Page<AccountListResponse>> pageAccount(AccountListRequest query, Pageable pageable) {
        var order = Sort.by("id").descending();
        return this.accountRepository.findBy(this.buildQueryExample(query), fluent -> fluent.as(AccountListResponse.class).sortBy(order).page(pageable)).log();
    }

    public Mono<Resource> exportAccount(AccountListRequest query) {
        var order = Sort.by("id").ascending();
        return this.accountRepository.findBy(this.buildQueryExample(query), fluent -> fluent.as(AccountExportBean.class).sortBy(order).all()).log()
                .collectList().flatMap(list -> {
                    var stream = new ByteArrayInOutStream();
                    EasyExcel.write(stream, AccountExportBean.class).sheet("用户信息").doWrite(list);
                    return Mono.just(new InputStreamResource(stream.getInputStream()));
                });
    }

    public Mono<Boolean> resetAccount(Mono<AccountResetRequest> requestMono) {
        return requestMono.flatMap(request -> this.accountRepository.resetAccount(request.account(), request.password()))
                .map(account -> account.equals(1));
    }

    private Example<AccountEntity> buildQueryExample(AccountListRequest query) {
        return Example.of(query.convertEntity(), ExampleMatcher.matching()
                .withMatcher("account", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("accountName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("orderNumber", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreNullValues());
    }

    @Data
    private static class AccountExportBean {
        @ExcelProperty(value = "序号")
        Integer id;
        @ExcelProperty(value = "创建时间")
        LocalDateTime createdAt;
        @ExcelProperty(value = "用户名")
        String account;
        @ExcelProperty(value = "用户姓名")
        String accountName;
        @ExcelProperty(value = "业务类型")
        String businessName;
        @ExcelProperty(value = "所属机构")
        String organizationName;
        @ExcelProperty(value = "是否绑定设备")
        Integer bindDevice;
        @ExcelProperty(value = "设备地址")
        String deviceId;
        @ExcelProperty(value = "订单号")
        String orderNumber;
        @ExcelProperty(value = "账号是否有效")
        Integer active;
        @ExcelProperty(value = "激活时间")
        LocalDateTime activeTime;
    }

}
