package com.estar.marketing.admin.dao;

import com.estar.marketing.admin.dao.entity.AccountEntity;
import com.estar.marketing.admin.dao.entity.projection.AccountProjection;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * @author xiaowenrou
 * @data 2022/8/8
 */
public interface AccountRepository extends R2dbcRepository<AccountEntity, Integer> {

    /**
     * 根据account查找
     * @param account
     * @return
     */
    Mono<AccountEntity> findByAccount(String account);

    /**
     * 跟据手机号查找
     * @param mobile
     * @return
     */
    Mono<AccountEntity> findByMobile(String mobile);

    /**
     * 根据用户名和密码查找
     * @param account
     * @param password
     * @return
     */
    Mono<AccountEntity> findByAccountAndPassword(String account, String password);

    /**
     * 根据手机号密码查找
     * @param mobile
     * @param password
     * @return
     */
    Mono<AccountEntity> findByMobileAndPassword(String mobile, String password);

    /**
     * 差找重复的account
     * @param accounts
     * @return
     */
    Flux<AccountProjection> findByAccountIn(Collection<String> accounts);

    /**
     * 重置密码
     * @param account
     * @param password
     * @return
     */
    @Modifying
    @Query(value = "UPDATE marketing_account SET password = :password WHERE account = :account")
    Mono<Integer> resetAccount(String account, String password);

    /**
     * 重置密码
     * @param mobile
     * @param password
     * @return
     */
    @Modifying
    @Query(value = "UPDATE marketing_account SET password = :password WHERE mobile = :mobile")
    Mono<Integer> resetAccountByMobile(String mobile, String password);

    /**
     * 更新组织架构
     * @param organizationId
     * @param organizationName
     * @return
     */
    @Modifying
    @Query(value = "UPDATE marketing_account SET organization_name = :organizationName WHERE organization_id = :organizationId")
    Mono<Integer> updateAccountOrganization(Integer organizationId, String organizationName);

    /**
     * 更新绑定信息
     * @param deviceId
     * @param id
     * @return
     */
    @Modifying
    @Query(value = "UPDATE marketing_account SET device_id = :deviceId, active_time = CURRENT_TIMESTAMP WHERE id = :id")
    Mono<Integer> updateAccountBindInfo(String deviceId, Integer id);

    /**
     * 批量禁用
     * @param accounts
     * @return
     */
    @Modifying
    @Query(value = "UPDATE marketing_account SET active = 1 WHERE account IN (:accounts)")
    Mono<Integer> batchDisableByAccount(Collection<String> accounts);

}
