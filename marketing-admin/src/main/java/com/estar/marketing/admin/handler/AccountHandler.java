package com.estar.marketing.admin.handler;

import com.estar.marketing.admin.model.request.AccountCheckRequest;
import com.estar.marketing.admin.model.request.AccountListRequest;
import com.estar.marketing.admin.model.request.AccountResetRequest;
import com.estar.marketing.admin.model.request.AccountSaveRequest;
import com.estar.marketing.admin.service.AccountService;
import com.estar.marketing.base.annotation.TokenCheck;
import com.estar.marketing.base.utils.ValidatorUtils;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.Validator;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

/**
 * @author xiaowenrou
 * @data 2022/8/8
 */
@TokenCheck
@Component
@AllArgsConstructor
public class AccountHandler {

    private final Validator validator;

    private final AccountService accountService;

    public Mono<ServerResponse> check(ServerRequest request) {
        var requestMono = request.bodyToMono(AccountCheckRequest.class).log()
                .doOnNext(req -> ValidatorUtils.valid(this.validator, req));
        return ServerResponse.ok().body(requestMono.flatMap(this.accountService::checkAccount), Boolean.class);
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        var requestMono = request.bodyToMono(AccountSaveRequest.class).log()
                .doOnNext(req -> ValidatorUtils.valid(this.validator, req));
        return ServerResponse.ok().body(requestMono.flatMap(this.accountService::saveAccount), Integer.class);
    }

    public Mono<ServerResponse> importFile(ServerRequest request) {
        var requestMono = request.multipartData().mapNotNull(map -> map.getFirst("file")).cast(FilePart.class);
        return ServerResponse.ok().body(requestMono.flatMap(this.accountService::importAccount), String.class);
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        var page = request.queryParam("page").map(Integer::parseInt).orElse(1);
        var ret = this.accountService.pageAccount(this.buildRequestModel(request), PageRequest.of(page - 1, 10));
        return ServerResponse.ok().body(ret, new ParameterizedTypeReference<>() {});
    }

    public Mono<ServerResponse> export(ServerRequest request) {
        return ServerResponse.ok().header(CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE)
                .header(CONTENT_DISPOSITION,  "attachment;filename=" + System.currentTimeMillis() + ".xlsx")
                .body(this.accountService.exportAccount(this.buildRequestModel(request)), Resource.class);
    }

    public Mono<ServerResponse> reset(ServerRequest request) {
        var requestMono = request.bodyToMono(AccountResetRequest.class).log()
                .doOnNext(req -> ValidatorUtils.valid(this.validator, req));
        return ServerResponse.ok().body(requestMono.flatMap(this.accountService::resetAccount), Boolean.class);
    }

    private AccountListRequest buildRequestModel(ServerRequest request){
        var query = new AccountListRequest();
        request.queryParam("account").ifPresent(query::setAccount);
        request.queryParam("accountName").ifPresent(query::setAccountName);
        request.queryParam("businessName").ifPresent(query::setBusinessName);
        request.queryParam("organizationId").filter(StringUtils::hasText).map(Integer::parseInt).ifPresent(query::setOrganizationId);
        request.queryParam("orderNumber").ifPresent(query::setOrderNumber);
        request.queryParam("active").filter(StringUtils::hasText).map(Integer::parseInt).ifPresent(query::setActive);
        return query;
    }

}
