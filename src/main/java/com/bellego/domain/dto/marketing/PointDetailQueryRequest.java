package com.bellego.domain.dto.marketing;

import com.bellego.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PointDetailQueryRequest extends PageQuery {
    private String memberId;
    private Integer type;
}

