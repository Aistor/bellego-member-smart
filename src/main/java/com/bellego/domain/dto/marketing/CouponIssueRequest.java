package com.bellego.domain.dto.marketing;

import lombok.Data;

import java.util.List;

@Data
public class CouponIssueRequest {
    private Boolean issueAll = false;
    private List<String> memberIds;
}

