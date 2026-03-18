package com.bellego.domain.dto.member;

import com.bellego.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConsumptionQueryDto extends PageQuery {
    private String memberId;
    private String storeId;
}