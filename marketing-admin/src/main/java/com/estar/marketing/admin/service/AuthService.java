package com.estar.marketing.admin.service;

import com.estar.marketing.admin.AdminPropertiesConfiguration;
import com.estar.marketing.base.exception.AccessDeclineException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author xiaowenrou
 * @data 2022/8/11
 */
@Service
@AllArgsConstructor
public class AuthService {

    private static final String USER_TOKEN_SECRET = "uhvwjajgnbm";

    private final AdminPropertiesConfiguration.RootUser rootUser;

    public Mono<String> buildToken(Consumer<Map<String, Object>> consumer) {
        Map<String, Object> map = new HashMap<>(1 << 2);
        if (consumer != null) {
            consumer.accept(map);
        }
        return Mono.just(Jwts.builder().setSubject("user").setClaims(map)
                .signWith(SignatureAlgorithm.HS256, USER_TOKEN_SECRET).compact());
    }

    public Mono<String> parseToken(String token) {
        Supplier<Claims> supplier = () -> {
            try {
                return Jwts.parser().setSigningKey(USER_TOKEN_SECRET).parseClaimsJws(token).getBody();
            } catch (Exception e) {
                throw  new AccessDeclineException("token格式错误", token);
            }
        };
        return Mono.fromSupplier(supplier).doOnNext(body -> {
            var userId = body.get("userId", String.class);
            var password = body.get("password", String.class);
            if (!this.rootUser.name().equals(userId) || !this.rootUser.password().equals(password)) {
                throw new AccessDeclineException("token失效", token);
            }
            var timestamp = body.get("timeStamp", Long.class);
            var current = System.currentTimeMillis();
            if (current - timestamp > 86400000) {
                throw new AccessDeclineException("token过期", token);
            }
        }).map(body -> body.get("userId", String.class));
    }

}
