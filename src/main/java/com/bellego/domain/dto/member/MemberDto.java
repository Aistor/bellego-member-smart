package com.bellego.domain.dto.member;

import com.bellego.common.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class MemberDto extends Pagination {
    private String id;
    private String levelId;
    private String cardNumber;
    private String name;
    private String phone;
    private Integer gender;
    private LocalDate birthday;
    private Integer totalPoints;
    private BigDecimal totalConsumption;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime lastConsumeTime;
}

