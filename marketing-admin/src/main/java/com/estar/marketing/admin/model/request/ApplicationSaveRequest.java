package com.estar.marketing.admin.model.request;

import com.estar.marketing.admin.dao.entity.ApplicationVersionEntity;

import javax.validation.constraints.NotBlank;

/**
 * @author xiaowenrou
 * @date 2022/8/16
 */
public record ApplicationSaveRequest(

        @NotBlank(message = "系统名不能为空")
        String system,

        @NotBlank(message = "下载地址不能为空")
        String download

) {

    public ApplicationVersionEntity convertEntity() {
        return new ApplicationVersionEntity(null, null, this.system(), this.download());
    }

}
