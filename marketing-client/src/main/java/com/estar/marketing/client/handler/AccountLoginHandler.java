package com.estar.marketing.client.handler;

import com.estar.marketing.base.exception.BusinessException;
import com.estar.marketing.base.utils.ValidatorUtils;
import com.estar.marketing.client.model.request.ClientCheckRequest;
import com.estar.marketing.client.model.request.AccountLoginRequest;
import com.estar.marketing.client.model.request.MobileLoginRequest;
import com.estar.marketing.client.service.AccountLoginService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.Validator;

/**
 * @author xiaowenrou
 * @data 2022/8/11
 */
@Component
@AllArgsConstructor
public class AccountLoginHandler {

    private final Validator validator;

    private final AccountLoginService accountLoginService;

    public Mono<ServerResponse> login(ServerRequest request) {
        var requestMono = request.bodyToMono(AccountLoginRequest.class).log()
                .doOnNext(req -> ValidatorUtils.valid(this.validator, req));
        return ServerResponse.ok().body(this.accountLoginService.accountLogin(requestMono), String.class);
    }

    public Mono<ServerResponse> check(ServerRequest request) {
        var requestMono = request.bodyToMono(ClientCheckRequest.class).log()
                .doOnNext(req -> ValidatorUtils.valid(this.validator, req));
        return ServerResponse.ok().body(this.accountLoginService.checkAccount(requestMono), String.class);
    }

    public Mono<ServerResponse> loginByMobile(ServerRequest request) {
        var requestMono = request.bodyToMono(MobileLoginRequest.class).log()
                .doOnNext(req -> ValidatorUtils.valid(this.validator, req));
        return ServerResponse.ok().body(this.accountLoginService.mobileLogin(requestMono), String.class);
    }

    public Mono<ServerResponse> checkAccess(ServerRequest request) {
        var mobile = request.queryParam("mobile").filter(StringUtils::hasText).orElseThrow(() -> new BusinessException("unknown mobile"));
        var index = request.queryParam("index").map(Integer::parseInt).orElseThrow(() -> new BusinessException("unknown index"));
        return ServerResponse.ok().body(this.accountLoginService.checkAccess(mobile, index), Boolean.class);
    }

}
