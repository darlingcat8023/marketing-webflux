package com.estar.marketing.acpect.check;

import com.estar.marketing.admin.service.AuthService;
import com.estar.marketing.base.annotation.TokenCheck;
import com.estar.marketing.base.exception.AccessDeclineException;
import com.estar.marketing.base.support.AnnotationResource;
import com.estar.marketing.base.utils.ReactiveContextHolder;
import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * 拦截器
 * @author xiaowenrou
 * @data 2022/8/11
 */
@AllArgsConstructor
@SuppressWarnings(value = "all")
public class TokenCheckInterceptor implements MethodInterceptor {

    private final AnnotationResource<TokenCheck> resource;

    private final AuthService authService;

    @Override
    public @Nullable Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        var check = this.resource.getSource(invocation.getMethod(), invocation.getThis().getClass());
        if (check == null) {
            return invocation.proceed();
        }
        // 解析token
        Function<ServerWebExchange, Mono<String>> parseFunction = exchange -> {
            String tokenString = this.getTokenFromRequest(exchange.getRequest());
            if (!StringUtils.hasText(tokenString)) {
                return Mono.error(() -> new AccessDeclineException("请求无token", null));
            }
            return this.authService.parseToken(tokenString);
        };
        // 保持上下文
        Function<String, Mono<ServerWebExchange>> holdFunction = user -> ReactiveContextHolder.getServerWebExchange().
                    doOnNext(exchange -> exchange.getAttributes().put("userId", user));
        // 调用目标方法
        Function<ServerWebExchange, Mono<Object>> invokeFunction = context -> {
            try {
                return (Mono<Object>) invocation.proceed();
            } catch (Throwable e) {
                return Mono.error(e);
            }
        };
        return ReactiveContextHolder.getServerWebExchange()
                .flatMap(parseFunction)
                .flatMap(holdFunction)
                .flatMap(invokeFunction);
    }

    private String getTokenFromRequest(ServerHttpRequest serverRequest) {
        var tokenString = serverRequest.getHeaders().getFirst("token");
        if (StringUtils.hasText(tokenString)) {
            return tokenString;
        }
        tokenString = serverRequest.getQueryParams().getFirst("token");
        if (StringUtils.hasText(tokenString)) {
            return tokenString;
        }
        return null;
    }

}
