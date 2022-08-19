package com.estar.marketing.base;

import java.time.format.DateTimeFormatter;

/**
 * @author xiaowenrou
 * @data 2022/8/9
 */
public abstract class Constants {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

}
