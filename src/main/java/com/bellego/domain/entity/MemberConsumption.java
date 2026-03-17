package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("member_consumption")
public class MemberConsumption {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String memberId;
    private String storeId;
    private BigDecimal amount;
    private Integer pointsEarned;
    private Date consumeTime;
    private Date createTime;
}

