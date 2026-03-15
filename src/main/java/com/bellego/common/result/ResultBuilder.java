package com.bellego.common.result;

public class ResultBuilder {
    public static <T> Result<T> success() {
        return Result.<T>builder()
                .code(ResultEnum.SUCCESS.getCode())
                .message(ResultEnum.SUCCESS.getMessage())
                .build();
    }

    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .code(ResultEnum.SUCCESS.getCode())
                .message(ResultEnum.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    public static <T> Result<T> error(String message) {
        return Result.<T>builder()
                .code(ResultEnum.BAD_REQUEST.getCode())
                .message(message)
                .build();
    }

    public static <T> Result<T> error(ResultEnum resultEnum) {
        return Result.<T>builder()
                .code(resultEnum.getCode())
                .message(resultEnum.getMessage())
                .build();
    }

    public static <T> Result<T> error(ResultEnum resultEnum, String message) {
        return Result.<T>builder()
                .code(resultEnum.getCode())
                .message(message)
                .build();
    }
}
