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
        var source = request.queryParam("source").map(Integer::parseInt)
                .orElseThrow(() -> new BusinessException("unknown source"));
        return ServerResponse.ok().body(this.tokenVerifyService.generate(source), String.class);
    }

    public Mono<ServerResponse> verifyToken(ServerRequest request) {
        var source = request.queryParam("source").map(Integer::parseInt)
                .orElseThrow(() -> new BusinessException("unknown source"));
        var token = request.queryParam("token").filter(StringUtils::hasText)
                .orElseThrow(() -> new BusinessException("unknown token"));
        var ret = this.tokenVerifyService.verify(Mono.just(new TokenVerifyRequest(source, token)).log(), RequestUtils.getActualAddress(request));
        return ServerResponse.ok().body(ret, String.class);
    }

}
