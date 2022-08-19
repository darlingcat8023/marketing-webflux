package com.estar.marketing.admin.dao;

import com.estar.marketing.admin.dao.entity.ApplicationVersionEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * @author xiaowenrou
 * @date 2022/8/16
 */
public interface ApplicationVersionRepository extends R2dbcRepository<ApplicationVersionEntity, Integer> {

}
