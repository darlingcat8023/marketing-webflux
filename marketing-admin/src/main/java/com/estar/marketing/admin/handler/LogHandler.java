package com.estar.marketing.admin.handler;

import com.estar.marketing.admin.model.request.LogListRequest;
import com.estar.marketing.admin.service.LogService;
import com.estar.marketing.base.annotation.TokenCheck;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

/**
 * @author xiaowenrou
 * @date 2022/8/10
 */
@TokenCheck
@Component
@AllArgsConstructor
public class LogHandler {

    private final LogService logService;

    public Mono<ServerResponse> list(ServerRequest request) {
        var page = request.queryParam("page").map(Integer::parseInt).orElse(1);
        var ret = this.logService.page(this.buildRequest(request), PageRequest.of(page - 1, 10));
        return ServerResponse.ok().body(ret, new ParameterizedTypeReference<>() {});
    }

    public Mono<ServerResponse> export(ServerRequest request) {
        return ServerResponse.ok()
                .header(CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE)
                .header(CONTENT_DISPOSITION,  "attachment;filename=" + System.currentTimeMillis() + ".xlsx")
                .body(this.logService.exportLog(this.buildRequest(request)), Resource.class);
    }

    private LogListRequest buildRequest(ServerRequest request) {
        var account = request.queryParam("account").orElse(null);
        var organizationId = request.queryParam("organizationId").filter(StringUtils::hasText)
                .map(Integer::parseInt).orElse(null);
        var startTime = request.queryParam("startTime").orElse(null);
        var endTime = request.queryParam("endTime").orElse(null);
        return new LogListRequest(account, organizationId, startTime, endTime);
    }

}
