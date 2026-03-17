package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("member_coupon")
public class MemberCoupon {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String memberId;
    private String couponId;
    private String code;
    private Integer status;
    private Date receiveTime;
    private Date useTime;
    private Date expireTime;
}

