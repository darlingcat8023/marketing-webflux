package com.estar.marketing.base.utils;

import com.estar.marketing.base.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.StringTokenizer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author xiaowenrou
 * @data 2022/7/29
 */
@Slf4j
public abstract class RequestUtils {

    /**
     * 获取客户端真实IP
     * @param request
     * @return
     */
    public static String getActualAddress(final ServerRequest request) {
        var headers = request.headers();
        headers.asHttpHeaders().forEach((k,v) -> log.info("http headers key = {} value = {}", k, v));
        var ip = "";
        Stream<Supplier<String>> stream = Stream.of(
                () -> headers.firstHeader("Ali-Cdn-Real-Ip"),
                () -> headers.firstHeader("X-Forwarded-For"),
                () -> headers.firstHeader("Proxy-Client-IP"),
                () -> request.remoteAddress()
                        .map(InetSocketAddress::getAddress)
                        .map(InetAddress::getHostAddress)
                        .orElse("")
        );
        Predicate<String> predicate = "unknown"::equals;
        ip = stream.map(Supplier::get).filter(predicate.negate().and(StringUtils::hasText))
                .findFirst().orElseThrow(() -> new BusinessException("无法获取ip地址"));
        if (ip.contains(",")) {
            return new StringTokenizer(ip, ",").nextToken();
        }
        return ip;
    }

}
