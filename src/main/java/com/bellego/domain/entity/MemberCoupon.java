package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 会员卡券实体类
 */
@Data
@TableName("member_coupon")
public class MemberCoupon {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id; // 主键 ID
    private String memberId; // 会员 ID，关联 member 表
    private String couponId; // 卡券 ID，关联 coupon 表
    private String code; // 卡券唯一编码（核销码）
    private Integer status; // 状态：0-未使用 1-已使用 2-已过期
    private Date receiveTime; // 领取时间
    private Date useTime; // 使用时间
    private Date expireTime; // 过期时间
}
