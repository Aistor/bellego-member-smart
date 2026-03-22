package com.bellego.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class PointDetailVo {
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