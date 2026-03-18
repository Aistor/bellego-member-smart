package com.bellego.domain.dto.member;

import com.bellego.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MemberQueryDto extends PageQuery {
    private String keyword;
    private String levelId;
    private Integer status;
}