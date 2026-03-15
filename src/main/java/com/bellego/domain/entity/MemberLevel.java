package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员等级实体类
 */
@Data
@TableName("member_level")
public class MemberLevel {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id; // 主键ID
    private String name; // 等级名称
    private Integer level; // 等级数值（用于排序）
    private Integer minPoints; // 升级所需最小积分
    private BigDecimal minConsumption; // 升级所需最小累计消费
    private BigDecimal discountRate; // 折扣率
    private Integer pointRate; // 积分倍率（消费 1 元积 n 分）
    private Integer status; // 状态：0-禁用 1-启用
    private Date createTime; // 创建时间
}
