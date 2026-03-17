package com.bellego.domain.dto.marketing;

import com.bellego.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CouponQueryRequest extends PageQuery {
    private String keyword;
    private Integer status;
}

