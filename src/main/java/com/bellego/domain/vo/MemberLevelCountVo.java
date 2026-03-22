package com.bellego.domain.vo;

import lombok.Data;

@Data
public class MemberLevelCountVo {
    private String levelId;
    private String levelName;
    private Integer count;
}
