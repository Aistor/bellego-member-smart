package com.bellego.common.result;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result<T>{
    private Integer code;
    private String message;
    private T data;
}
