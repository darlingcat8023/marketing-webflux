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
import com.estar.marketing.admin.utils.EasyExcelUtils;
import com.estar.marketing.base.ByteArrayInOutStream;
import com.estar.marketing.base.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author xiaowenrou
 * @data 2022/8/8
 */
@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Mono<Boolean> checkAccount(AccountCheckRequest request) {
        return this.accountRepository.exists(Example.of(request.convertEntity(), ExampleMatcher.matching().withIgnoreNullValues()));
    }

    @Transactional(rollbackFor = {Exception.class})
    public Mono<AccountEntity> saveAccount(AccountSaveRequest request) {
        var ent = request.convertEntity();
        var accountCheck = this.accountRepository.findByAccount(ent.account()).flatMap(model -> ent.id() == null || !model.id().equals(ent.id()) ? Mono.error(() -> new BusinessException("account exists: " + model.account(), model)) : Mono.empty());
        var mobileCheck = Mono.empty();
        if (StringUtils.hasText(ent.mobile())) {
            mobileCheck = this.accountRepository.findByMobile(ent.mobile()).flatMap(model -> ent.id() == null || !model.id().equals(ent.id()) ? Mono.error(() -> new BusinessException("mobile exists: " + model.mobile(), model)) : Mono.empty());
        }
        return accountCheck.then(mobileCheck).then(this.accountRepository.save(ent));
    }

    @Transactional(rollbackFor = {Exception.class})
    public Mono<String> importAccount(FilePart part) {
        Supplier<InputStream> supplier = () -> new InputStream() {
            @Override
            public int read() { return -1;}
        };
        return Mono.using(
                State::new,
                state -> part.content().reduceWith(supplier, (input, buffer) -> new SequenceInputStream(input, buffer.asInputStream()))
                        .flatMapMany(stream -> EasyExcelUtils.read(stream, AccountImportBean.class, 0))
                        .map(AccountImportBean::convert).flatMap(this::saveAccount).onErrorContinue(BusinessException.class, (err, obj) -> {
                            state.roll = true;
                            if (err instanceof BusinessException e) {
                                var ent = ((AccountEntity) e.getBlamedObject());
                                state.duplicates.add(ent.account());
                            }
                        }).then(Mono.just(state)),
                state -> {})
                .flatMap(state -> state.roll ? Mono.error(() -> new BusinessException(String.join(",", state.duplicates) + " 以上用户名有重复")) : Mono.just("success"));
    }

    public Mono<Page<AccountListResponse>> pageAccount(AccountListRequest query, Pageable pageable) {
        var order = Sort.by("id").descending();
        return this.accountRepository.findBy(query.buildExample(), fluent -> fluent.sortBy(order).page(pageable))
                .map(page -> page.map(AccountListResponse::new)).log();
    }

    public Mono<Resource> exportAccount(AccountListRequest query) {
        var order = Sort.by("id").ascending();
        return this.accountRepository.findBy(query.buildExample(), fluent -> fluent.as(AccountExportBean.class).sortBy(order).all()).log()
                .collectList().flatMap(list -> {
                    var stream = new ByteArrayInOutStream();
                    EasyExcel.write(stream, AccountExportBean.class).sheet("用户信息").doWrite(list);
                    return Mono.just(new InputStreamResource(stream.getInputStream()));
                });
    }

    public Mono<Boolean> resetAccount(AccountResetRequest request) {
        return this.accountRepository.resetAccount(request.account(), request.password()).map(account -> account.equals(1));
    }

    public Mono<String> batchDisableAccount(Collection<String> accounts) {
        return this.accountRepository.batchDisableByAccount(accounts).thenReturn("success");
    }

    static class State {
        boolean roll = false;
        List<String> duplicates = new ArrayList<>();
    }

    @Data
    public static class AccountImportBean {
        @ExcelProperty(index = 0)
        private String businessName;
        @ExcelProperty(index = 1)
        private String orderNumber;
        @ExcelProperty(index = 2)
        private String organizationName;
        @ExcelProperty(index = 3)
        private Integer organizationId;
        @ExcelProperty(index = 4)
        private String type;
        @ExcelProperty(index = 5)
        private String accesses;
        @ExcelProperty(index = 6)
        private String account;
        @ExcelProperty(index = 7)
        private String accountName;
        @ExcelProperty(index = 8)
        private String mobile;
        @ExcelProperty(index = 9)
        private String password;

        public AccountSaveRequest convert() {
            var accessList = new ArrayList<Integer>();
            if (StringUtils.hasText(this.accesses)) {
                Arrays.stream(this.accesses.split(",")).map(Integer::parseInt).forEach(accessList::add);
            }
            return new AccountSaveRequest(null, this.account, this.mobile, this.password, this.accountName, this.businessName, this.organizationId, this.organizationName,
                    0, null, this.orderNumber, 0, this.type, accessList);
        }
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
