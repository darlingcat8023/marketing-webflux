package com.estar.marketing.admin.handler;

import com.estar.marketing.admin.model.request.OrganizationSaveRequest;
import com.estar.marketing.admin.service.OrganizationService;
import com.estar.marketing.base.model.PullModel;
import com.estar.marketing.base.annotation.TokenCheck;
import com.estar.marketing.base.exception.BusinessException;
import com.estar.marketing.base.utils.ValidatorUtils;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.Validator;

/**
 * @author xiaowenrou
 * @data 2022/8/5
 */
@TokenCheck
@Component
@AllArgsConstructor
public class OrganizationHandler {

    private final Validator validator;

    private final OrganizationService organizationService;

    public Mono<ServerResponse> save(ServerRequest request) {
        var requestMono = request.bodyToMono(OrganizationSaveRequest.class).log()
                .doOnNext(req -> ValidatorUtils.valid(this.validator, req));
        return ServerResponse.ok().body(this.organizationService.saveOrganization(requestMono), Integer.class);
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        var name = request.queryParam("name").filter(StringUtils::hasText).orElse(null);
        var page = request.queryParam("page").map(Integer::parseInt).orElse(1);
        var ret = this.organizationService.pageOrganization(name, PageRequest.of(page - 1, 10));
        return ServerResponse.ok().body(ret, new ParameterizedTypeReference<>() {});
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        var id = request.queryParam("id").map(Integer::parseInt)
                .orElseThrow(() -> new BusinessException("id 不能为空"));
        return ServerResponse.ok().body(this.organizationService.deleteOrganization(id), Integer.class);
    }

    public Mono<ServerResponse> pull(ServerRequest request) {
        return ServerResponse.ok().body(this.organizationService.filterOrganization(), PullModel.class);
    }

}
