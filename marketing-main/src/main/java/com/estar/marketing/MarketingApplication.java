package com.estar.marketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import reactor.core.publisher.Hooks;

/**
 * @author xiaowenrou
 * @date 2022/7/29
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.estar")
public class MarketingApplication {

    public static void main(String[] args) {
        Hooks.onOperatorDebug();
        SpringApplication.run(MarketingApplication.class, args);
    }

}
