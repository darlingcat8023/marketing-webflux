package com.estar.marketing.admin.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.estar.marketing.admin.dao.LogRepository;
import com.estar.marketing.admin.dao.entity.LoginLogEntity;
import com.estar.marketing.admin.model.request.LogListRequest;
import com.estar.marketing.admin.model.response.LogListResponse;
import com.estar.marketing.base.ByteArrayInOutStream;
import com.estar.marketing.base.utils.ReactivePageUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * @author xiaowenrou
 * @data 2022/8/10
 */
@Service
@AllArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    private final R2dbcEntityOperations r2dbcEntityOperations;

    public Mono<Page<LogListResponse>> page(LogListRequest request, Pageable pageable) {
        var matching= this.r2dbcEntityOperations.select(LoginLogEntity.class)
                .as(LogListResponse.class).matching(this.buildQuery(request).with(pageable).sort(Sort.by("id").descending()));
        return matching.all().collectList().flatMap(content -> ReactivePageUtils.getPage(content, pageable, matching.count())).log();
    }

    public Mono<Resource> exportLog(LogListRequest request) {
        return this.r2dbcEntityOperations.select(LoginLogEntity.class)
                .as(LogExportBean.class).matching(this.buildQuery(request).sort(Sort.by("id").ascending()))
                .all().collectList().flatMap(list -> {
                    var stream = new ByteArrayInOutStream();
                    EasyExcel.write(stream, LogExportBean.class).sheet("????????????").doWrite(list);
                    return Mono.just(new InputStreamResource(stream.getInputStream()));
                });
    }

    private Query buildQuery(LogListRequest request) {
        var criteria = Criteria.empty();
        if (StringUtils.hasText(request.account())) {
            criteria = criteria.and(Criteria.where("account").like("%" + request.account() + "%"));
        }
        if (request.organizationId() != null) {
            criteria = criteria.and(Criteria.where("organization_id").is(request.organizationId()));
        }
        if (StringUtils.hasText(request.startTime())) {
            criteria = criteria.and(Criteria.where("login_at").greaterThanOrEquals(request.startTime()));
        }
        if (StringUtils.hasText(request.endTime())) {
            criteria = criteria.and(Criteria.where("login_at").lessThanOrEquals(request.endTime()));
        }
        return Query.query(criteria);
    }

    @Data
    @NoArgsConstructor
    private static class LogExportBean {
        @ExcelProperty(value = "??????")
        private Integer id;
        @ExcelProperty(value = "?????????")
        private String account;
        @ExcelProperty(value = "????????????")
        private String accountName;
        @ExcelProperty(value = "????????????")
        private String organizationName;
        @ExcelProperty(value = "????????????")
        private LocalDateTime createdAt;
    }


}
