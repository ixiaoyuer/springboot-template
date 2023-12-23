package com.xiaoyuer.springboot.common;

import lombok.Getter;

/**
 * 自定义错误码
 *
 * @author 小鱼儿
 * @date 2023/12/23 16:35:29
 */
@Getter
public enum ErrorCode {

    SUCCESS(20000, "操作成功"),
    PARAMS_ERROR(40000, "请求参数错误"),
    PARAMS_NULL(40001, "请求参数为空"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
