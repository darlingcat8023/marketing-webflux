package com.estar.marketing.client.service;

import com.estar.marketing.base.exception.BusinessException;
import com.estar.marketing.base.utils.ReactiveContextHolder;
import com.estar.marketing.client.ClientPropertiesConfiguration;
import com.google.common.hash.Hashing;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author xiaowenrou
 * @data 2022/8/2
 */
public abstract class TokenStrategy {

    /**
     * token验证工厂
     * @param context
     * @param source
     * @return
     */
    public static TokenStrategy create(ApplicationContext context, Integer source) {
        return switch (source) {
            case 1 -> context.getBean(HongHeTokenStrategy.class);
            case 2 -> context.getBean(HuaYaoStrategy.class);
            default -> throw new BusinessException("unknown source");
        };
    }

    /**
     * 生成token
     * @return
     */
    public abstract Mono<String> generateToken();

    /**
     * 解析token
     * @param token
     * @return
     */
    public abstract Mono<Boolean> parseToken(String token);

    /**
     * 鸿合平台token策略
     */
    @Slf4j
    @Component
    @AllArgsConstructor
    public final static class HongHeTokenStrategy extends TokenStrategy {

        private final ClientPropertiesConfiguration.ProviderHonghe provider;

        @Override
        public Mono<String> generateToken() {
            return Mono.error(() -> new BusinessException("no need token gen"));
        }

        @Override
        public Mono<Boolean> parseToken(String token) {
            Function<ServerWebExchange, ServerHttpRequest> function = ServerWebExchange::getRequest;
            // 需要通过 Context 获取请求上下文
            return ReactiveContextHolder.getServerWebExchange().map(function.andThen(ServerHttpRequest::getURI)
                    .andThen(uri -> {
                        var decoded = new String(Base64.getDecoder().decode(token)).split("\n");
                        if (decoded.length != 6 || !this.provider.appid().equals(decoded[1])) {
                            return false;
                        }
                        var signatureBody = MessageFormat.format("{0}\n{1}\n{2}\n{3}\n{4}", decoded[0], decoded[1], decoded[2], decoded[3], decoded[4]);
                        var genSignature = Hashing.hmacSha256(this.provider.getSecretBytes()).hashString(signatureBody, StandardCharsets.UTF_8).toString();
                        return genSignature.equals(decoded[5]);
                    })
            );
        }

    }

    /**
     * 华耀平台token策略
     */
    @Component
    @AllArgsConstructor
    public static class HuaYaoStrategy extends TokenStrategy {

        private static final String version = "v1";

        private static final String provider = "HuaYao";

        private static final String secret = "xjpshabi";

        @Override
        public Mono<String> generateToken() {
            var salt = UUID.randomUUID().toString().replace("_","").substring(5, 10);
            var signatureBody = MessageFormat.format("{0}\n{1}\n{2}\n{3}", this.getVersion(), this.getProvider(), System.currentTimeMillis(), salt);
            var signature = Hashing.hmacSha256(this.getSecretBytes()).hashString(signatureBody, StandardCharsets.UTF_8).toString();
            var str = signatureBody.concat("\n").concat(signature);
            return Mono.defer(() -> Mono.just(Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8))));
        }

        protected String getVersion() {
            return HuaYaoStrategy.version;
        }

        protected String getProvider() {
            return HuaYaoStrategy.provider;
        }

        protected byte[] getSecretBytes() {
            return HuaYaoStrategy.secret.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public Mono<Boolean> parseToken(String token) {
            var decoded = new String(Base64.getDecoder().decode(token)).split("\n");
            if (decoded.length != 5 || !this.getVersion().equals(decoded[0]) || !this.getProvider().equals(decoded[1])) {
                return Mono.defer(() -> Mono.just(false));
            }
            var signatureBody = MessageFormat.format("{0}\n{1}\n{2}\n{3}", decoded[0], decoded[1], decoded[2], decoded[3]);
            var signature = Hashing.hmacSha256(this.getSecretBytes()).hashString(signatureBody, StandardCharsets.UTF_8).toString();
            return Mono.defer(() -> Mono.just(signature.equals(decoded[4])));
        }

    }

}