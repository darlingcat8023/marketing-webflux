package com.estar.marketing.base.annotation;

import java.lang.annotation.*;

/**
 * @author xiaowenrou
 * @data 2022/8/11
 */
@Documented
@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface TokenCheck {}
