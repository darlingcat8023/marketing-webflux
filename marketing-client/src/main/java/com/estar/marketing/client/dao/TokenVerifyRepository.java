package com.estar.marketing.client.dao;

import com.estar.marketing.client.dao.entity.TokenVerifyEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * @author xiaowenrou
 * @data 2022/7/29
 */
public interface TokenVerifyRepository extends R2dbcRepository<TokenVerifyEntity, Integer> {

    /**
     * 根据 token 查找
     * @param token
     * @return
     */
    Mono<TokenVerifyEntity> findFirstByToken(String token);

}
