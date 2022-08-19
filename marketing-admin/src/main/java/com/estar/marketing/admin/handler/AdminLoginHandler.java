package com.estar.marketing.admin.handler;

import com.estar.marketing.admin.model.request.AdminLoginRequest;
import com.estar.marketing.admin.service.AdminLoginService;
import com.estar.marketing.base.utils.ValidatorUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.Validator;

/**
 * @author xiaowenrou
 * @data 2022/7/29
 */

@Slf4j
@Component
@AllArgsConstructor
public class AdminLoginHandler {

    private final Validator validator;

    private final AdminLoginService adminLoginService;

    public Mono<ServerResponse> adminLogin(ServerRequest request) {
        var requestMono = request.bodyToMono(AdminLoginRequest.class).log()
                .doOnNext(req -> ValidatorUtils.valid(this.validator, req));
        return ServerResponse.ok().body(this.adminLoginService.adminLoginService(requestMono), String.class);
    }

}
