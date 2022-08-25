package com.estar.marketing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author xiaowenrou
 * @date 2022/7/28
 */
@Configuration(proxyBeanMethods = false)
@EnableR2dbcAuditing
@EnableR2dbcRepositories(basePackages = "**.dao")
@EnableTransactionManagement(proxyTargetClass = true)
public class DatabaseConfiguration {

    /**
     * 命名转化策略
     * @return
     */
    @Bean
    public NamingStrategy namingStrategy() {
        return NamingStrategy.INSTANCE;
    }

}
