package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 积分规则实体类
 */
@Data
@TableName("point_rule")
public class PointRule {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id; // 主键 ID
    private String ruleName; // 规则名称（如普通会员消费积分规则）
    private Integer ruleType; // 规则类型：1-消费积分 2-签到积分
    private String applicableLevelId; // 适用会员等级 ID（空表示通用）
    private Integer pointsPerUnit; // 每单位获得积分（如每 1 元积 1 分）
    private BigDecimal minAmount; // 适用最小消费金额（可选）
    private Integer maxPoints; // 单次最大积分上限（可选）
    private Integer status; // 状态：0-禁用 1-启用
    private Date createTime; // 创建时间
}
