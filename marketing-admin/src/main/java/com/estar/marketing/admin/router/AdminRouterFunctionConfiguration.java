package com.estar.marketing.admin.router;

import com.estar.marketing.admin.handler.*;
import com.estar.marketing.admin.model.request.*;
import com.estar.marketing.admin.model.response.ApplicationVersionResponse;
import com.estar.marketing.base.model.PullModel;
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
import org.springframework.data.domain.Page;
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
 * 路由配置
 * @author xiaowenrou
 * @data 2022/7/28
 */
@Configuration(proxyBeanMethods = false)
public class AdminRouterFunctionConfiguration {

    @Bean
    @RouterOperations(value = {
            @RouterOperation(method = POST, path = "/api/admin/login", produces = MediaType.APPLICATION_JSON_VALUE, beanClass = AdminLoginHandler.class, beanMethod = "adminLogin",
                    operation = @Operation(
                            operationId = "login",
                            requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = AdminLoginRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> adminLoginRouterFunction(AdminLoginHandler adminLoginHandler) {
        Supplier<RouterFunction<ServerResponse>> supplier = () -> RouterFunctions.route()
                .POST("/login", RequestPredicates.contentType(APPLICATION_JSON), adminLoginHandler::adminLogin)
                .build();
        return RouterFunctions.route().path("/api/admin", supplier).build();
    }

    @Bean
    @RouterOperations(value = {
            @RouterOperation(method = POST, path = "/api/admin/account/check", produces = MediaType.APPLICATION_JSON_VALUE, beanClass = AccountHandler.class, beanMethod = "check",
                    operation = @Operation(
                            operationId = "checkAccount",
                            description = "检查account名称是否有重复",
                            requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = AccountCheckRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Boolean.class))),
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "403", description = "forbidden operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            ),
            @RouterOperation(method = POST, path = "/api/admin/account/save", produces = MediaType.APPLICATION_JSON_VALUE, beanClass = AccountHandler.class, beanMethod = "save",
                    operation = @Operation(
                            operationId = "saveAccount",
                            description = "保存用户",
                            requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = AccountSaveRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Integer.class))),
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "403", description = "forbidden operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            ),
            @RouterOperation(method = GET, path = "/api/admin/account/list", beanClass = AccountHandler.class, beanMethod = "list",
                    operation = @Operation(
                            operationId = "listAccount",
                            description = "用户列表",
                            parameters = {
                                    @Parameter(in = QUERY, name = "page", description = "分页查询的页数，每页10条"),
                                    @Parameter(in = QUERY, name = "account", description = "用户名模糊查询"),
                                    @Parameter(in = QUERY, name = "accountName", description = "姓名模糊查询"),
                                    @Parameter(in = QUERY, name = "businessName", description = "业务类型精确查询"),
                                    @Parameter(in = QUERY, name = "organizationId", description = "所属机构精确查询"),
                                    @Parameter(in = QUERY, name = "orderNumber", description = "订单号模糊查询"),
                                    @Parameter(in = QUERY, name = "active", description = "账号状态查询")
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Page.class))),
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "403", description = "forbidden operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            ),
            @RouterOperation(method = GET, path = "/api/admin/account/export", beanClass = AccountHandler.class, beanMethod = "export",
                    operation = @Operation(
                            operationId = "exportAccount",
                            description = "用户列表导出",
                            parameters = {
                                    @Parameter(in = QUERY, name = "account", description = "用户名模糊查询"),
                                    @Parameter(in = QUERY, name = "accountName", description = "姓名模糊查询"),
                                    @Parameter(in = QUERY, name = "businessName", description = "业务类型精确查询"),
                                    @Parameter(in = QUERY, name = "organizationId", description = "所属机构精确查询"),
                                    @Parameter(in = QUERY, name = "orderNumber", description = "订单号模糊查询"),
                                    @Parameter(in = QUERY, name = "active", description = "账号状态查询")
                            },
                            responses = {
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "403", description = "forbidden operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            ),
            @RouterOperation(method = POST, path = "/api/admin/account/reset", beanClass = AccountHandler.class, beanMethod = "reset",
                    operation = @Operation(
                            operationId = "resetAccount",
                            description = "重置用户密码",
                            requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = AccountResetRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Boolean.class))),
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "403", description = "forbidden operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> accountRouterFunction(AccountHandler accountHandle) {
        Supplier<RouterFunction<ServerResponse>> supplier = () -> RouterFunctions.route()
                .POST("/check", RequestPredicates.contentType(APPLICATION_JSON), accountHandle::check)
                .POST("/save", RequestPredicates.contentType(APPLICATION_JSON), accountHandle::save)
                .GET("/list", accountHandle::list)
                .GET("/export", accountHandle::export)
                .POST("/reset", RequestPredicates.contentType(APPLICATION_JSON), accountHandle::reset)
                .build();
        return RouterFunctions.route().path("/api/admin/account", supplier).build();
    }

    @Bean
    @RouterOperations(value = {
            @RouterOperation(method = POST, path = "/api/admin/org/save", produces = MediaType.APPLICATION_JSON_VALUE, beanClass = OrganizationHandler.class, beanMethod = "save",
                    operation = @Operation(
                            operationId = "saveOrganization",
                            description = "保存组织结构",
                            requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = OrganizationSaveRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Integer.class))),
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "403", description = "forbidden operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            ),
            @RouterOperation(method = GET, path = "/api/admin/org/list", beanClass = OrganizationHandler.class, beanMethod = "list",
                    operation = @Operation(
                            operationId = "listOrganization",
                            description = "组织结构列表",
                            parameters = {
                                    @Parameter(in = QUERY, name = "page", description = "分页查询的页数，每页10条"),
                                    @Parameter(in = QUERY, name = "name", description = "organization name 模糊查询")
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Page.class))),
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "403", description = "forbidden operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            ),
            @RouterOperation(method = GET, path = "/api/admin/org/delete", beanClass = OrganizationHandler.class, beanMethod = "delete",
                    operation = @Operation(
                            operationId = "deleteOrganization",
                            description = "删除组织结构",
                            parameters = {
                                    @Parameter(in = QUERY, name = "id", required = true, description = "id")
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "403", description = "forbidden operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            ),
            @RouterOperation(method = GET, path = "/api/admin/org/pull", beanClass = OrganizationHandler.class, beanMethod = "pull",
                    operation = @Operation(
                            operationId = "pullOrganization",
                            description = "组织结构下拉列表",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = PullModel.class))),
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "403", description = "forbidden operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> organizationRouterFunction(OrganizationHandler organizationHandler) {
        Supplier<RouterFunction<ServerResponse>> supplier = () -> RouterFunctions.route()
                .POST("/save", RequestPredicates.contentType(APPLICATION_JSON), organizationHandler::save)
                .GET("/list", organizationHandler::list)
                .GET("/delete", organizationHandler::delete)
                .GET("/pull", organizationHandler::pull)
                .build();
        return RouterFunctions.route().path("/api/admin/org", supplier).build();
    }

    @Bean
    @RouterOperations(value = {
            @RouterOperation(method = GET, path = "/api/admin/log/list", beanClass = LogHandler.class, beanMethod = "list",
                    operation = @Operation(
                            operationId = "log",
                            description = "登录日志",
                            parameters = {
                                    @Parameter(in = QUERY, name = "page", description = "分页查询的页数，每页10条"),
                                    @Parameter(in = QUERY, name = "account", description = "用户名模糊查询"),
                                    @Parameter(in = QUERY, name = "organizationId", description = "所属机构精确查询"),
                                    @Parameter(in = QUERY, name = "startTime", description = "开始时间"),
                                    @Parameter(in = QUERY, name = "endTime", description = "截止时间")
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Page.class))),
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "403", description = "forbidden operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            ),
            @RouterOperation(method = GET, path = "/api/admin/log/export", beanClass = LogHandler.class, beanMethod = "export",
                    operation = @Operation(
                            operationId = "logExport",
                            description = "登录日志导出",
                            parameters = {
                                    @Parameter(in = QUERY, name = "account", description = "用户名模糊查询"),
                                    @Parameter(in = QUERY, name = "organizationId", description = "所属机构精确查询"),
                                    @Parameter(in = QUERY, name = "startTime", description = "开始时间"),
                                    @Parameter(in = QUERY, name = "endTime", description = "截止时间")
                            },
                            responses = {
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "403", description = "forbidden operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> logRouterFunction(LogHandler logHandler) {
        Supplier<RouterFunction<ServerResponse>> supplier = () -> RouterFunctions.route()
                .GET("/list", logHandler::list)
                .GET("/export", logHandler::export)
                .build();
        return RouterFunctions.route().path("/api/admin/log", supplier).build();
    }

    @Bean
    @RouterOperations(value = {
            @RouterOperation(method = POST, path = "/api/admin/app/save", beanClass = ApplicationVersionHandler.class, beanMethod = "save",
                    operation = @Operation(
                            operationId = "save",
                            requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = ApplicationSaveRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            ),
            @RouterOperation(method = GET, path = "/api/admin/app/info", beanClass = ApplicationVersionHandler.class, beanMethod = "info",
                    operation = @Operation(
                            operationId = "info",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ApplicationVersionResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "fail operation", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> applicationVersionRouterFunction(ApplicationVersionHandler applicationVersionHandler) {
        Supplier<RouterFunction<ServerResponse>> supplier = () -> RouterFunctions.route()
                .POST("/save", RequestPredicates.contentType(APPLICATION_JSON), applicationVersionHandler::save)
                .GET("/info", applicationVersionHandler::info)
                .build();
        return RouterFunctions.route().path("/api/admin/app", supplier).build();
    }

}
