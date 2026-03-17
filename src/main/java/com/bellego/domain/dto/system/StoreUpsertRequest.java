package com.bellego.domain.dto.system;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StoreUpsertRequest {
    @NotBlank(message = "门店名称不能为空")
    private String name;
    @NotBlank(message = "门店编码不能为空")
    private String code;
    @NotBlank(message = "门店地址不能为空")
    private String address;
    @NotBlank(message = "门店电话不能为空")
    private String phone;
    @NotNull(message = "状态不能为空")
    private Integer status;
}

