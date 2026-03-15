package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员消费记录实体类
 */
@Data
@TableName("member_consumption")
public class MemberConsumption {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id; // 主键ID
    private String memberId; // 会员ID，关联member表
    private String storeId; // 门店ID，关联store表
    private BigDecimal amount; // 消费金额
    private Integer pointsEarned; // 本次消费获得积分
    private Date consumeTime; // 实际消费时间
    private Date createTime; // 创建时间
}
