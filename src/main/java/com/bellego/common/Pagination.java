package com.bellego.common;

import lombok.Data;

@Data
public class Pagination {
    private Long pageSize=10L;
    private Long currentPage=1L;
}
