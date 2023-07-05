package com.estar.marketing.client.model.request;

/**
 * @author xiaowenrou
 * @date 2023/4/13
 */
public record CommitCodeRequest(

        String mobile,

        String code,

        String password

) {
}
