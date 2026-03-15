package com.bellego.common.result;

import lombok.Getter;

@Getter
public enum ResultEnum {
    SUCCESS(200, "SUCCESS"),
    BAD_REQUEST(400, "请求失败"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    NOT_ACCEPTABLE(406, "参数错误,缺乏必要的参数!"),
    INTERNAL_SERVER_ERROR(500, "服务器错误");

    private final Integer code;
    private final String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
