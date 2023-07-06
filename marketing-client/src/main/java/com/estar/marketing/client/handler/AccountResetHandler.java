package com.estar.marketing.client.handler;

import com.estar.marketing.base.exception.BusinessException;
import com.estar.marketing.base.utils.ValidatorUtils;
import com.estar.marketing.client.model.request.CommitCodeRequest;
import com.estar.marketing.client.service.AccountResetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.Validator;

/**
 * @author xiaowenrou
 * @date 2023/4/13
 */
@Component
@AllArgsConstructor
public class AccountResetHandler {

    private final Validator validator;

    private final AccountResetService accountResetService;

    public Mono<ServerResponse> sendVerificationCode(ServerRequest request) {
        var mobile = request.queryParam("mobile").filter(StringUtils::hasText).orElseThrow(() -> new BusinessException("mobile null"));
        return this.accountResetService.sendResetVerificationCode(mobile)
                .as(mono -> ServerResponse.ok().body(mono, String.class));
    }

    public Mono<ServerResponse> commitVerificationCode(ServerRequest request) {
        return request.bodyToMono(CommitCodeRequest.class).doOnNext(req -> ValidatorUtils.valid(this.validator, req))
                .flatMap(this.accountResetService::commitVerificationCode)
                .as(mono -> ServerResponse.ok().body(mono, String.class));
    }

}
