package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("point_detail")
public class PointDetail {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String memberId;
    private Integer type;
    private Integer points;
    private Integer balance;
    private String source;
    private String sourceId;
    private String remark;
    private Date createTime;
}

