package com.estar.marketing.config;

import com.estar.marketing.base.exception.AccessDeclineException;
import com.estar.marketing.base.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Webflux全局异常处理
 * @author xiaowenrou
 * @date 2022/8/11
 */
@Slf4j
@Order(value = -2)
@Configuration(proxyBeanMethods = false)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    private final ApplicationContext applicationContext;

    private final List<ErrorHandler> handlers = new ArrayList<>();

    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties properties, ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, properties.getResources(), applicationContext);
        this.setMessageWriters(serverCodecConfigurer.getWriters());
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        this.handlers.addAll(this.applicationContext.getBeansOfType(ErrorHandler.class).values());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        HandlerFunction<ServerResponse> handlerFunction = request -> {
            var error = errorAttributes.getError(request);
            Supplier<String> supplier = () -> {
                log.error("未知异常", error);
                return error.getMessage();
            };
            return this.handlers.stream().filter(handler -> handler.canHandle(error))
                    .map(handler -> handler.handle(error)).findFirst()
                    .orElse(ServerResponse.badRequest().body(Mono.fromSupplier(supplier), String.class));
        };
        return RouterFunctions.route(RequestPredicates.all(), handlerFunction);
    }

    public interface ErrorHandler {

        /**
         * 能处理的异常类型
         * @param throwable
         * @return
         */
        boolean canHandle(Throwable throwable);

        /**
         * 处理构造异常响应
         * @param throwable
         * @return
         */
        Mono<ServerResponse> handle(Throwable throwable);

    }

    @Slf4j
    @Component
    @AllArgsConstructor
    private static final class BusinessErrorHandler implements ErrorHandler {

        @Override
        public boolean canHandle(Throwable throwable) {
            return ClassUtils.isAssignable(BusinessException.class, throwable.getClass());
        }

        @Override
        public Mono<ServerResponse> handle(Throwable throwable) {
            var businessException = (BusinessException) throwable;
            return ServerResponse.badRequest().body(Mono.just(businessException.getMessage()), String.class);
        }

    }

    @Slf4j
    @Component
    @AllArgsConstructor
    private static final class AccessDeclineErrorHandler implements ErrorHandler {

        @Override
        public boolean canHandle(Throwable throwable) {
            return ClassUtils.isAssignable(AccessDeclineException.class, throwable.getClass());
        }

        @Override
        public Mono<ServerResponse> handle(Throwable throwable) {
            var accessError = (AccessDeclineException) throwable;
            return ServerResponse.status(HttpStatus.FORBIDDEN).body(Mono.just(accessError.getOutPutMessage()), String.class);
        }

    }

}