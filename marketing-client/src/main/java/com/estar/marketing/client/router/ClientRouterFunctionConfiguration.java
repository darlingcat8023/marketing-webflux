package com.estar.marketing.client.router;

import com.estar.marketing.client.handler.AccountLoginHandler;
import com.estar.marketing.client.handler.TokenVerifyHandler;
import com.estar.marketing.client.model.request.ClientCheckRequest;
import com.estar.marketing.client.model.request.AccountLoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.function.Supplier;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author xiaowenrou
 * @data 2022/7/29
 */
@Configuration(proxyBeanMethods = false)
public class ClientRouterFunctionConfiguration {

    @Bean
    public RouterFunction<ServerResponse> thirdVerifyRouterFunction(TokenVerifyHandler thirdVerifyHandler) {
        Supplier<RouterFunction<ServerResponse>> supplier = () -> RouterFunctions.route()
                .GET("/generate", thirdVerifyHandler::generateToken)
                .GET("/verify", thirdVerifyHandler::verifyToken)
                .build();
        return RouterFunctions.route().path("/api/client/token", supplier).build();
    }

    @Bean
    @RouterOperations(value = {
            @RouterOperation(method = POST, path = "/api/client/login", produces = MediaType.APPLICATION_JSON_VALUE, beanClass = AccountLoginHandler.class, beanMethod = "login",
                    operation = @Operation(
                            operationId = "clientLogin",
                            requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = AccountLoginRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            ),
            @RouterOperation(method = POST, path = "/api/client/check", produces = MediaType.APPLICATION_JSON_VALUE, beanClass = AccountLoginHandler.class, beanMethod = "check",
                    operation = @Operation(
                            operationId = "clientCheck",
                            requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = ClientCheckRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> accountLoginRouterFunction(AccountLoginHandler accountLoginHandler) {
        Supplier<RouterFunction<ServerResponse>> supplier = () -> RouterFunctions.route()
                .POST("/login", RequestPredicates.contentType(APPLICATION_JSON), accountLoginHandler::login)
                .POST("/check", RequestPredicates.contentType(APPLICATION_JSON), accountLoginHandler::check)
                .build();
        return RouterFunctions.route().path("/api/client", supplier).build();
    }

}
