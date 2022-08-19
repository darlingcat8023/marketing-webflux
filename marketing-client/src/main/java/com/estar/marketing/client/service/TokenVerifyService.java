package com.estar.marketing.client.service;

import com.estar.marketing.base.exception.BusinessException;
import com.estar.marketing.client.dao.TokenVerifyRepository;
import com.estar.marketing.client.dao.entity.TokenVerifyEntity;
import com.estar.marketing.client.model.request.TokenVerifyRequest;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author xiaowenrou
 * @data 2022/7/29
 */
@Service
@AllArgsConstructor
public class TokenVerifyService {

    private final ApplicationContext applicationContext;

    private final TokenVerifyRepository tokenverifyRepository;

    public Mono<String> generate(Integer source) {
        return TokenStrategy.create(this.applicationContext, source).generateToken();
    }

    public Mono<String> verify(Mono<TokenVerifyRequest> requestMono, String remoteAddress) {
        return requestMono.flatMap(request -> TokenStrategy.create(this.applicationContext, request.source()).parseToken(request.token()))
                .doOnNext(bool -> {
                    if (!bool) {
                        throw new BusinessException("token not valid");
                    }
                })
                .then(requestMono)
                .flatMap(request -> this.tokenverifyRepository.findFirstByToken(request.token())
                        .switchIfEmpty(this.tokenverifyRepository.save(this.convertTokenVerifyEntity(request, remoteAddress)))
                )
                .doOnNext(entity -> {
                    if (!entity.remoteAddress().equals(remoteAddress)) {
                        throw new BusinessException("ip not match");
                    }
                }).map(x -> "success");
    }

    private TokenVerifyEntity convertTokenVerifyEntity(TokenVerifyRequest request, String remoteAddress) {
        return new TokenVerifyEntity(null, null, null, request.source(), request.token(), remoteAddress);
    }

}
