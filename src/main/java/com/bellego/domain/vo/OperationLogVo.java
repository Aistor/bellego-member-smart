package com.bellego.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class OperationLogVo {
    private String id;
    private String operatorId;
    private String operatorName;
    private String module;
    private String operation;
    private String detail;
    private String ip;
    private Date createTime;
}