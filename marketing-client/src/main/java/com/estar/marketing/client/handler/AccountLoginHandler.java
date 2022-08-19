package com.estar.marketing.client.handler;

import com.estar.marketing.base.utils.ValidatorUtils;
import com.estar.marketing.client.model.request.ClientCheckRequest;
import com.estar.marketing.client.model.request.AccountLoginRequest;
import com.estar.marketing.client.service.AccountLoginService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
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

}
