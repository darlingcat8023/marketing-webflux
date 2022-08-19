package com.estar.marketing.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.nio.charset.StandardCharsets;

/**
 * @author xiaowenrou
 * @data 2022/8/2
 */
@Configuration(proxyBeanMethods = false)
public class ClientPropertiesConfiguration {

    @Validated
    @ConstructorBinding
    @ConfigurationProperties(prefix = "provider.honghe")
    public record ProviderHonghe(
            @NotBlank String appid,
            @NotBlank String secret
    ) {

        public byte[] getSecretBytes() {
            return this.secret.getBytes(StandardCharsets.UTF_8);
        }

    }

}
