package com.estar.marketing.client.router;

import com.estar.marketing.client.handler.AccountLoginHandler;
import com.estar.marketing.client.handler.AccountResetHandler;
import com.estar.marketing.client.handler.TokenVerifyHandler;
import com.estar.marketing.client.model.request.AccountLoginRequest;
import com.estar.marketing.client.model.request.ClientCheckRequest;
import com.estar.marketing.client.model.request.CommitCodeRequest;
import com.estar.marketing.client.model.request.MobileLoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
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
            ),
            @RouterOperation(method = POST, path = "/api/client/mobileLogin", produces = MediaType.APPLICATION_JSON_VALUE, beanClass = AccountLoginHandler.class, beanMethod = "loginByMobile",
                    operation = @Operation(
                            operationId = "loginByMobile",
                            requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = MobileLoginRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            ),
            @RouterOperation(method = GET, path = "/api/client/access", beanClass = AccountLoginHandler.class, beanMethod = "checkAccess",
                    operation = @Operation(
                            operationId = "checkAccess",
                            description = "检查权限",
                            parameters = {
                                    @Parameter(in = QUERY, name = "mobile", description = "手机号"),
                                    @Parameter(in = QUERY, name = "index", description = "课程索引")
                            },
                            responses = {
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = Boolean.class))),
                                    @ApiResponse(responseCode = "403", description = "forbidden operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> accountLoginRouterFunction(AccountLoginHandler accountLoginHandler) {
        Supplier<RouterFunction<ServerResponse>> supplier = () -> RouterFunctions.route()
                .POST("/login", RequestPredicates.contentType(APPLICATION_JSON), accountLoginHandler::login)
                .POST("/check", RequestPredicates.contentType(APPLICATION_JSON), accountLoginHandler::check)
                .POST("/mobileLogin", RequestPredicates.contentType(APPLICATION_JSON), accountLoginHandler::loginByMobile)
                .GET("/access", accountLoginHandler::checkAccess)
                .build();
        return RouterFunctions.route().path("/api/client", supplier).build();
    }

    @Bean
    @RouterOperations(value = {
            @RouterOperation(method = GET, path = "/api/client/reset/send", beanClass = AccountResetHandler.class, beanMethod = "sendVerificationCode",
                    operation = @Operation(
                            operationId = "sendVerificationCode",
                            parameters = {
                                    @Parameter(in = QUERY, name = "mobile", description = "手机号")
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            ),
            @RouterOperation(method = POST, path = "/api/client/reset/commit", produces = MediaType.APPLICATION_JSON_VALUE, beanClass = AccountResetHandler.class, beanMethod = "commitVerificationCode",
                    operation = @Operation(
                            operationId = "commitVerificationCode",
                            requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = CommitCodeRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> accountResetRouterFunction(AccountResetHandler accountResetHandler) {
        Supplier<RouterFunction<ServerResponse>> supplier = () -> RouterFunctions.route()
                .GET("/send", accountResetHandler::sendVerificationCode)
                .POST("/commit", RequestPredicates.contentType(APPLICATION_JSON), accountResetHandler::commitVerificationCode)
                .build();
        return RouterFunctions.route().path("/api/client/reset", supplier).build();
    }

}
