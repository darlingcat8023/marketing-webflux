package com.estar.marketing.base.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 响应式分页工具类
 * @author xiaowenrou
 * @data 2022/8/10
 */
@SuppressWarnings(value = {"unchecked"})
public abstract class ReactivePageUtils {

    public static <T> Mono<Page<T>> getPage(List<T> content, Pageable pageable, Mono<Long> totalSupplier) {
        Assert.notNull(content, "Content must not be null!");
        Assert.notNull(pageable, "Pageable must not be null!");
        Assert.notNull(totalSupplier, "TotalSupplier must not be null!");
        if (!pageable.isUnpaged() && pageable.getOffset() != 0L) {
            return content.size() != 0 && pageable.getPageSize() > content.size() ?
                    Mono.just(new PageImpl<>(content, pageable, pageable.getOffset() + (long)content.size())) : totalSupplier.map((total) -> new PageImpl<>(content, pageable, total));
        } else {
            return !pageable.isUnpaged() && pageable.getPageSize() <= content.size() ?
                    totalSupplier.map((total) -> new PageImpl<>(content, pageable, total)) : Mono.just(new PageImpl<>(content, pageable, content.size()));
        }
    }

}
