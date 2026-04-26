package com.bellego.domain.vo.analysis;

import lombok.Data;

@Data
public class MemberLevelCountVO {
    private String levelId;
    private String levelName;
    private Integer count;
}
