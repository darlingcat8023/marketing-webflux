package com.estar.marketing.admin.service;

import com.estar.marketing.admin.dao.ApplicationVersionRepository;
import com.estar.marketing.admin.dao.entity.ApplicationVersionEntity;
import com.estar.marketing.admin.model.request.ApplicationSaveRequest;
import com.estar.marketing.admin.model.response.ApplicationVersionResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author xiaowenrou
 * @date 2022/8/16
 */
@Service
@AllArgsConstructor
public class ApplicationVersionService {

    private final ApplicationVersionRepository applicationVersionRepository;

    @Transactional(rollbackFor = {Exception.class})
    public Mono<String> save(Flux<ApplicationSaveRequest> requestFlux) {
        return this.applicationVersionRepository.deleteAll()
                .thenMany(this.applicationVersionRepository.saveAll(requestFlux.map(ApplicationSaveRequest::convertEntity)))
                .then(Mono.fromSupplier(() -> "success"));
    }

    public Flux<ApplicationVersionResponse> info() {
        return this.applicationVersionRepository.findAll().map(this::convertResponse);
    }

    private ApplicationVersionResponse convertResponse(ApplicationVersionEntity entity) {
        return new ApplicationVersionResponse(entity.system(), entity.download());
    }

}
