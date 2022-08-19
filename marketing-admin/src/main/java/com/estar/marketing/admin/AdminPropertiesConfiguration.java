package com.estar.marketing.admin;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * @author xiaowenrou
 * @data 2022/7/29
 */
@Configuration(proxyBeanMethods = false)
public class AdminPropertiesConfiguration {

    @Validated
    @ConstructorBinding
    @ConfigurationProperties(prefix = "admin")
    public record RootUser(
            @NotBlank String name,
            @NotBlank String password
    ) {}

}
