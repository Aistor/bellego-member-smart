package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * 会员实体类
 */
@Data
@TableName("member")
public class Member {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id; // 会员ID
    private String levelId; // 会员等级ID
    private String cardNumber; // 会员卡号
    private String name; // 姓名
    private String phone; // 手机号
    private Integer gender; // 性别 0-未知 1-男 2-女
    private LocalDate birthday; // 生日
    private Integer totalPoints; // 总积分
    private BigDecimal totalConsumption; // 总消费金额
    private Integer status; // 状态：0-禁用 1-启用
    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间
    private Date lastConsumeTime; // 最后消费时间
}
