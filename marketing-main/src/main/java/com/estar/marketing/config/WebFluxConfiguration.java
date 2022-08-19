package com.estar.marketing.config;

import com.estar.marketing.base.utils.ReactiveContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.lang.NonNull;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author xiaowenrou
 * @date 2022/7/28
 */
@EnableWebFlux
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
public class WebFluxConfiguration implements WebFluxConfigurer {

    private final ObjectMapper objectMapper;

    private final javax.validation.Validator validator;

    /**
     * Webflux跨域
     * @param registry
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        WebFluxConfigurer.super.addCorsMappings(registry);
        registry.addMapping("/**")
                .allowedOriginPatterns("*").allowedHeaders("*")
                .allowedMethods("GET", "POST", "OPTIONS", "DELETE", "PUT");
    }

    /**
     * Webflux参数校验，在function模式下仅用来注入一个Adapter
     * @return
     */
    @Override
    public Validator getValidator() {
        return new SpringValidatorAdapter(this.validator);
    }

    /**
     * 序列化和反序列化
     * @param configurer
     */
    @Override
    public void configureHttpMessageCodecs(@NonNull ServerCodecConfigurer configurer) {
        WebFluxConfigurer.super.configureHttpMessageCodecs(configurer);
        // 配置jackson
        var serverDefaultCodecs = configurer.defaultCodecs();
        // 启用debug日志记录http请求
        serverDefaultCodecs.enableLoggingRequestDetails(true);
        // 设置默认编码解码器
        serverDefaultCodecs.jackson2JsonEncoder(new Jackson2JsonEncoder(this.objectMapper));
        serverDefaultCodecs.jackson2JsonDecoder(new Jackson2JsonDecoder(this.objectMapper));
    }

    /**
     * webflux 上下文拦截器
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    public static class ServerRequestFilter implements WebFilter {

        @Override
        public Mono<Void> filter(@NonNull ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
            return webFilterChain.filter(serverWebExchange)
                    .contextWrite(context -> context.put(ReactiveContextHolder.KEY_SERVER_EXCHANGE, serverWebExchange));
        }

    }

}
