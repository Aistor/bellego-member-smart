package com.bellego.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class MemberVo {
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
    private Date createTime;
    private Date updateTime;
    private Date lastConsumeTime;
}

