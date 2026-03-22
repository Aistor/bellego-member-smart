package com.bellego.domain.dto.marketing;

import com.bellego.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PointRuleQueryDto extends PageQuery {
    private String ruleName;
    private Integer status;
}
