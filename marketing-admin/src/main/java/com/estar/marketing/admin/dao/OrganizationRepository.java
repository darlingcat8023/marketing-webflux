package com.estar.marketing.admin.dao;

import com.estar.marketing.admin.dao.entity.OrganizationEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author xiaowenrou
 * @data 2022/8/5
 */
public interface OrganizationRepository extends R2dbcRepository<OrganizationEntity, Integer> {

    /**
     * 过滤器
     * @param deletedAt
     * @return
     */
    Flux<OrganizationEntity> findAllByDeletedAt(Integer deletedAt);

    /**
     * 软删除
     * @param id
     * @return
     */
    @Modifying
    @Query(value = "UPDATE marketing_organization SET deleted_at = 1 WHERE id = :id")
    Mono<Integer> softDelete(Integer id);

}
