package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
@TableName("member")
public class Member {
    @TableId(type = IdType.ASSIGN_UUID)
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

