package com.bellego.domain.dto.system;

import com.bellego.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StoreQueryRequest extends PageQuery {
    private String keyword;
    private Integer status;
}

