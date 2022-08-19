package com.estar.marketing.admin.handler;

import com.estar.marketing.admin.model.request.ApplicationSaveRequest;
import com.estar.marketing.admin.model.response.ApplicationVersionResponse;
import com.estar.marketing.admin.service.ApplicationVersionService;
import com.estar.marketing.base.utils.ValidatorUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.Validator;

/**
 * @author xiaowenrou
 * @date 2022/8/16
 */
@Component
@AllArgsConstructor
public class ApplicationVersionHandler {

    private final Validator validator;

    private final ApplicationVersionService applicationVersionService;


    public Mono<ServerResponse> save(ServerRequest request) {
        var requestFlux = request.bodyToFlux(ApplicationSaveRequest.class).log()
                .doOnNext(req -> ValidatorUtils.valid(this.validator, req));
        return ServerResponse.ok().body(this.applicationVersionService.save(requestFlux), String.class);
    }

    public Mono<ServerResponse> info(ServerRequest request) {
        return ServerResponse.ok().body(this.applicationVersionService.info(), ApplicationVersionResponse.class);
    }

}
