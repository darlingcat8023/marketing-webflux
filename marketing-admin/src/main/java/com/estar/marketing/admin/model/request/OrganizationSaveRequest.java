package com.estar.marketing.admin.model.request;

import javax.validation.constraints.NotBlank;

/**
 * @author xiaowenrou
 * @data 2022/8/5
 */
public record OrganizationSaveRequest(

        Integer id,

        @NotBlank(message = "标识不能为空")
        String name

) {}
