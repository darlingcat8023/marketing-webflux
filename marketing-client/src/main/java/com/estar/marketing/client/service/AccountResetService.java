package com.estar.marketing.client.service;

import com.estar.marketing.admin.dao.AccountRepository;
import com.estar.marketing.base.exception.BusinessException;
import com.estar.marketing.client.model.request.CommitCodeRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * @author xiaowenrou
 * @date 2023/4/13
 */
@Service
@AllArgsConstructor
public class AccountResetService {

    private static final String SMS_SERVICE = "http://ty-cloud-service-svc";

    private static final String SMS_URL = "/api/inner/cloud-service/aliyun/sms/send";


    private final AccountRepository accountRepository;

    private final Cache<String, String> verificationCache;

    public Mono<String> sendResetVerificationCode(String mobile) {
        return this.accountRepository.findByMobile(mobile).switchIfEmpty(Mono.error(() -> new BusinessException("手机号不存在")))
                .flatMap(entity -> Mono.justOrEmpty(this.verificationCache.getIfPresent(mobile)))
                .flatMap(cache -> Mono.error(() -> new BusinessException("请勿重复发送")))
                .switchIfEmpty(this.smsRequest(mobile).flatMap(code -> {
                    this.verificationCache.put(mobile, code);
                    return Mono.empty();
                })).thenReturn("success");
    }

    public Mono<String> commitVerificationCode(CommitCodeRequest request) {
        return Mono.justOrEmpty(this.verificationCache.getIfPresent(request.mobile())).switchIfEmpty(Mono.error(() -> new BusinessException("验证码失效")))
                .filter(request.code()::equals).switchIfEmpty(Mono.error(() -> new BusinessException("验证码错误")))
                .flatMap(code -> this.accountRepository.resetAccountByMobile(request.mobile(), request.password()))
                .doOnSuccess(i -> this.verificationCache.invalidate(request.mobile())).thenReturn("success");
    }

    private Mono<String> smsRequest(String mobile) {
        Function<JsonNode, String> function = json -> {
            var code = json.get("code").asInt();
            if (code != 0) {
                throw new BusinessException("发送短信失败");
            }
            return json.get("data").get("code").asText();
        };
        Function<ClientResponse, Mono<String>> responseFunction = response -> {
            if (response.statusCode() == HttpStatus.OK) {
                return response.bodyToMono(JsonNode.class).map(function);
            } else {
                return Mono.error(() -> new BusinessException("发送短信失败"));
            }
        };
        return WebClient.create(SMS_SERVICE).post().uri(SMS_URL).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new VerificationCodeSendRequest(mobile)).exchangeToMono(responseFunction);
    }

    @Data
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class VerificationCodeSendRequest {
        private String templateCode = "SMS_276405279";
        private String signName = "聂卫平围棋网校";
        private String mobile;
        public VerificationCodeSendRequest(String mobile) {
            this.mobile = mobile;
        }
    }

}
