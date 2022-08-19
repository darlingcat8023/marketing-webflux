package com.estar.marketing.admin.service;

import com.estar.marketing.admin.AdminPropertiesConfiguration;
import com.estar.marketing.admin.model.request.AdminLoginRequest;
import com.estar.marketing.base.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author xiaowenrou
 * @data 2022/7/29
 */
@Service
@AllArgsConstructor
public class AdminLoginService {

    private final AuthService authService;

    private final AdminPropertiesConfiguration.RootUser rootUser;

    public Mono<String> adminLoginService(Mono<AdminLoginRequest> requestMono) {
        return requestMono.filter(request -> request.equals(this.rootUser))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException("用户名或密码错误"))))
                .flatMap(request -> this.authService.buildToken(map -> {
                    map.put("userId", request.name());
                    map.put("password", request.password());
                    map.put("timeStamp", System.currentTimeMillis());
                }));
    }

}
