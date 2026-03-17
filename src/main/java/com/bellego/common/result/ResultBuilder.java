package com.bellego.common.result;

public final class ResultBuilder {

    private ResultBuilder() {
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .code(ResultEnum.SUCCESS.getCode())
                .message(ResultEnum.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    public static <T> Result<T> error(ResultEnum resultEnum) {
        return error(resultEnum, resultEnum.getMessage());
    }

    public static <T> Result<T> error(ResultEnum resultEnum, String message) {
        return Result.<T>builder()
                .code(resultEnum.getCode())
                .message(message)
                .build();
    }
}

