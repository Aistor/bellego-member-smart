package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 积分明细实体类
 */
@Data
@TableName("point_detail")
public class PointDetail {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id; // 主键 ID
    private String memberId; // 会员 ID，关联 member 表
    private Integer type; // 变动类型：1-获取 2-消费/兑换 3-过期
    private Integer points; // 变动积分数（正增负减）
    private Integer balance; // 变动后积分余额
    private String source; // 积分来源（如消费、签到、兑换）
    private Long sourceId; // 来源业务 ID（关联消费/兑换记录）
    private String remark; // 备注说明
    private Date createTime; // 创建时间
}
