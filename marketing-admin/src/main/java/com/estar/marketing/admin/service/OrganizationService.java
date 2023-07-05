package com.estar.marketing.admin.service;

import com.estar.marketing.admin.dao.AccountRepository;
import com.estar.marketing.admin.dao.OrganizationRepository;
import com.estar.marketing.admin.dao.entity.OrganizationEntity;
import com.estar.marketing.admin.model.request.OrganizationSaveRequest;
import com.estar.marketing.admin.model.response.OrganizationListResponse;
import com.estar.marketing.base.model.PullModel;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author xiaowenrou
 * @data 2022/8/5
 */
@Service
@AllArgsConstructor
public class OrganizationService {

    private final AccountRepository accountRepository;

    private final OrganizationRepository organizationRepository;

    @Transactional(rollbackFor = {Exception.class})
    public Mono<Integer> saveOrganization(Mono<OrganizationSaveRequest> requestMono) {
        return requestMono.map(this::convertEntity)
                .flatMap(this.organizationRepository::save)
                .flatMap(entity -> this.accountRepository.updateAccountOrganization(entity.id(), entity.name()).map(model -> entity.id()));
    }

    public Mono<Page<OrganizationListResponse>> pageOrganization(String name, Pageable pageable) {
        var organizationEntity = new OrganizationEntity(null, null, null, 0, name);
        var matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreNullValues();
        var order = Sort.by( "id").descending();
        return this.organizationRepository.findBy(Example.of(organizationEntity, matcher), fluent -> fluent.sortBy(order).as(OrganizationListResponse.class).page(pageable)).log();
    }

    public Mono<String> deleteOrganization(Integer id) {
        return this.organizationRepository.softDelete(id).map(x -> "success");
    }

    public Flux<PullModel> filterOrganization() {
        return this.organizationRepository.findAllByDeletedAt(0).map(entity -> new PullModel(entity.name(), entity.id()));
    }

    private OrganizationEntity convertEntity(OrganizationSaveRequest request) {
        return new OrganizationEntity(request.id(), null, null, 0, request.name());
    }

}
