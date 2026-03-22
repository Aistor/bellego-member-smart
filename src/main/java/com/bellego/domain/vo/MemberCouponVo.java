package com.bellego.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class MemberCouponVo {
    private String id;
    private String memberId;
    private String memberName;
    private String couponId;
    private String couponName;
    private String code;
    private Integer status;
    private Date receiveTime;
    private Date useTime;
    private Date expireTime;
}
