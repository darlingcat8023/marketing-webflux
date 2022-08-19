package com.estar.marketing.admin.dao;

import com.estar.marketing.admin.dao.entity.LoginLogEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * @author xiaowenrou
 * @data 2022/8/10
 */
public interface LogRepository extends R2dbcRepository<LoginLogEntity, Integer> {

}
