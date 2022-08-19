package com.estar.marketing.acpect.check;

import com.estar.marketing.admin.service.AuthService;
import com.estar.marketing.base.annotation.TokenCheck;
import com.estar.marketing.base.support.AnnotationResource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @author xiaowenrou
 * @data 2022/8/11
 */
@Configuration(proxyBeanMethods = false)
@Role(value = BeanDefinition.ROLE_INFRASTRUCTURE)
public class TokenAuthConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AnnotationResource<TokenCheck> tokenAnnotationResource() {
        return new AnnotationResource<>(TokenCheck.class);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public TokenCheckInterceptor tokenCheckInterceptor(AnnotationResource<TokenCheck> tokenAnnotationResource, AuthService authService) {
        return new TokenCheckInterceptor(tokenAnnotationResource, authService);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public TokenCheckAdvisor requestAuthAdvisor(AnnotationResource<TokenCheck> tokenAnnotationResource, TokenCheckInterceptor tokenCheckInterceptor) {
        return new TokenCheckAdvisor(tokenAnnotationResource, tokenCheckInterceptor);
    }

}
