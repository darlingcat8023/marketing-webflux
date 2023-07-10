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
import java.util.List;
import java.util.function.Function;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

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
        return request.bodyToMono(AccountCheckRequest.class).log().doOnNext(req -> ValidatorUtils.valid(this.validator, req))
                .flatMap(this.accountService::checkAccount)
                .as(mono -> ok().body(mono, Boolean.class));
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(AccountSaveRequest.class).log().doOnNext(req -> ValidatorUtils.valid(this.validator, req))
                .flatMap(this.accountService::saveAccount)
                .as(mono -> ok().body(mono.thenReturn("success"), Integer.class));
    }

    public Mono<ServerResponse> importFile(ServerRequest request) {
        return request.multipartData().mapNotNull(map -> map.getFirst("file")).cast(FilePart.class)
                .flatMap(this.accountService::importAccount)
                .as(mono -> ok().body(mono, String.class));
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        var page = request.queryParam("page").map(Integer::parseInt).orElse(1);
        return this.accountService.pageAccount(this.buildRequestModel(request), PageRequest.of(page - 1, 10))
                .as(flux -> ok().body(flux, new ParameterizedTypeReference<>() {}));
    }

    public Mono<ServerResponse> export(ServerRequest request) {
        Function<Mono<Resource>, Mono<ServerResponse>> downloadFunction = mono -> ok().header(CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE)
                .header(CONTENT_DISPOSITION,  "attachment;filename=" + System.currentTimeMillis() + ".xlsx")
                .body(mono, Resource.class);
        return this.accountService.exportAccount(this.buildRequestModel(request)).as(downloadFunction);
    }

    public Mono<ServerResponse> reset(ServerRequest request) {
        return request.bodyToMono(AccountResetRequest.class).log().doOnNext(req -> ValidatorUtils.valid(this.validator, req))
                .flatMap(this.accountService::resetAccount)
                .as(mono -> ok().body(mono, Boolean.class));
    }

    public Mono<ServerResponse> batchDisable(ServerRequest request) {
        return request.bodyToMono(new ParameterizedTypeReference<List<String>>() {}).log()
                .flatMap(this.accountService::batchDisableAccount)
                .as(mono -> ok().body(mono, String.class));

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
