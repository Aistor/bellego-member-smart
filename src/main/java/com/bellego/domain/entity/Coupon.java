package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 卡券实体类
 */
@Data
@TableName("coupon")
public class Coupon {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id; // 主键 ID
    private String name; // 卡券名称
    private Integer type; // 卡券类型：1-折扣券 2-满减券
    private BigDecimal couponValue; // 面值/折扣值（类型 1 为折扣，类型 2 为减免金额）
    private BigDecimal useCondition; // 使用门槛（0 表示无门槛）
    private Integer stock; // 库存总量
    private Integer totalIssued; // 已发放数量
    private Date startTime; // 有效期开始时间
    private Date endTime; // 有效期结束时间
    private Integer status; // 状态：0-禁用 1-启用
    private Date createTime; // 创建时间
}
