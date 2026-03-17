package com.bellego.domain.dto.member;

import com.bellego.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConsumptionQueryRequest extends PageQuery {
    private String memberId;
    private String storeId;
}

