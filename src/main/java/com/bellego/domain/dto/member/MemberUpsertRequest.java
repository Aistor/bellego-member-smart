package com.bellego.domain.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberUpsertRequest {
    private String levelId;

    @NotBlank(message = "会员卡号不能为空")
    private String cardNumber;

    @NotBlank(message = "会员姓名不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotNull(message = "性别不能为空")
    private Integer gender;

    private LocalDate birthday;

    @NotNull(message = "状态不能为空")
    private Integer status;
}

