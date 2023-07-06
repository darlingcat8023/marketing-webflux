package com.estar.marketing.client.handler;

import com.estar.marketing.base.exception.BusinessException;
import com.estar.marketing.base.utils.RequestUtils;
import com.estar.marketing.client.model.request.TokenVerifyRequest;
import com.estar.marketing.client.service.TokenVerifyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author xiaowenrou
 * @data 2022/7/29
 */
@Slf4j
@Component
@AllArgsConstructor
public class TokenVerifyHandler {

    private final TokenVerifyService tokenVerifyService;

    public Mono<ServerResponse> generateToken(ServerRequest request) {
        var source = request.queryParam("source").orElseThrow(() -> new BusinessException("unknown source"));
        return this.tokenVerifyService.generate(source).as(mono -> ServerResponse.ok().body(mono, String.class));
    }

    public Mono<ServerResponse> verifyToken(ServerRequest request) {
        var source = request.queryParam("source").orElseThrow(() -> new BusinessException("unknown source"));
        var token = request.queryParam("token").filter(StringUtils::hasText)
                .orElseThrow(() -> new BusinessException("unknown token"));
        return this.tokenVerifyService.verify(new TokenVerifyRequest(source, token), RequestUtils.getActualAddress(request))
                .as(mono -> ServerResponse.ok().body(mono, String.class));
    }

}
