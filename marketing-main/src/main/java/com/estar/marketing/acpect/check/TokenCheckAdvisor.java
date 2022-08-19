package com.estar.marketing.acpect.check;

import com.estar.marketing.base.annotation.TokenCheck;
import com.estar.marketing.base.support.AnnotationResource;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;

/**
 * 切点通知
 * @author xiaowenrou
 * @data 2022/8/11
 */
public class TokenCheckAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private final transient Pointcut pointcut;

    private final transient AnnotationResource<TokenCheck> resource ;

    public TokenCheckAdvisor(AnnotationResource<TokenCheck> resource, MethodInterceptor methodInterceptor) {
        this.pointcut = new TokenCheckAdvisor.TokenCheckPointCut();
        this.resource = resource;
        this.setAdvice(methodInterceptor);
    }

    @NonNull
    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    private class TokenCheckPointCut extends StaticMethodMatcherPointcut {

        @Override
        public boolean matches(@NonNull Method method, @NonNull Class<?> targetClass) {
            return TokenCheckAdvisor.this.resource.getSource(method, targetClass) != null;
        }

    }

}
