package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 门店实体类
 */
@Data
@TableName("store")
public class Store {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id; // 主键 ID
    private String name; // 门店名称
    private String code; // 门店编码（唯一）
    private String address; // 门店详细地址
    private String phone; // 门店联系电话
    private Integer status; // 状态：0-禁用 1-启用
    private Date createTime; // 创建时间
}
