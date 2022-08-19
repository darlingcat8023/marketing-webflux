package com.estar.marketing.client.service;

import com.estar.marketing.admin.dao.AccountRepository;
import com.estar.marketing.admin.dao.LogRepository;
import com.estar.marketing.admin.dao.entity.LoginLogEntity;
import com.estar.marketing.base.exception.BusinessException;
import com.estar.marketing.client.model.request.ClientCheckRequest;
import com.estar.marketing.client.model.request.AccountLoginRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author xiaowenrou
 * @date 2022/8/11
 */
@Service
@AllArgsConstructor
public class AccountLoginService {

    private static final String USER_TOKEN_SECRET = "avnkjla";

    public final AccountRepository accountRepository;

    public final LogRepository logRepository;

    @Transactional(rollbackFor = {Exception.class})
    public Mono<String> accountLogin(Mono<AccountLoginRequest> requestMono) {
        return requestMono.flatMap(request -> this.accountRepository.findByAccountAndPassword(request.account(), request.password())
                .switchIfEmpty(Mono.defer(() -> Mono.error(() -> new BusinessException("用户名或密码错误"))))
                .doOnNext(entity -> {
                    if (entity.active().equals(1)) {
                        throw new BusinessException("账户已停用");
                    }
                    if (entity.bindDevice().equals(1) && StringUtils.hasText(entity.deviceId())) {
                        if (!entity.deviceId().equals(request.deviceId())) {
                            throw new BusinessException("账号已经绑定了设备");
                        }
                    }
                })
                .flatMap(entity -> {
                    var logEntity = new LoginLogEntity(null, null, null, entity.organizationId(), entity.organizationName(), entity.id(), entity.account(), entity.accountName());
                    if (entity.bindDevice().equals(1)) {
                        return this.accountRepository.updateAccountBindInfo(request.deviceId(), entity.id())
                                .then(this.logRepository.save(logEntity));
                    } else {
                        return this.logRepository.save(logEntity);
                    }
                })
                .map(entity -> this.generateClientToken(request.account(), request.password(), request.deviceId()))
        );
    }

    private String generateClientToken(String account, String password, String deviceId) {
        return Jwts.builder().setSubject("user").setClaims(Map.of("account", account, "password", password, "device", deviceId))
                .signWith(SignatureAlgorithm.HS256, USER_TOKEN_SECRET).compact();
    }

    public Mono<String> checkAccount(Mono<ClientCheckRequest> requestMono) {
        return requestMono.map(this::parseClientToken)
                .flatMap(item -> this.accountRepository.findByAccountAndPassword(item.account(), item.password())
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException("用户名或密码异常"))))
                        .doOnNext(entity -> {
                            if (entity.active().equals(1)) {
                                throw new BusinessException("账户已停用");
                            }
                            if (entity.bindDevice().equals(1)) {
                                if (!StringUtils.hasText(entity.deviceId())) {
                                    throw new BusinessException("设备绑定异常");
                                }
                                if (!entity.deviceId().equals(item.deviceId())) {
                                    throw new BusinessException("绑定的设备地址不匹配");
                                }
                            }
                        })
                ).map(entity -> "success");
    }

    private AccountLoginRequest parseClientToken(ClientCheckRequest request) {
        try {
            var body= Jwts.parser().setSigningKey(USER_TOKEN_SECRET).parseClaimsJws(request.token()).getBody();
            var deviceId = body.get("device", String.class);
            if (!deviceId.equals(request.deviceId())) {
                throw new BusinessException("绑定的设备地址不匹配");
            }
            var account = body.get("account", String.class);
            var password = body.get("password", String.class);
            return new AccountLoginRequest(account, password, deviceId);
        } catch (Exception e) {
            throw new BusinessException("token解析失败");
        }
    }

}
