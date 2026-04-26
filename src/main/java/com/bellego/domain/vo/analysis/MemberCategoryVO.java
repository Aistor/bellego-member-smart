package com.bellego.domain.vo.analysis;

import lombok.Data;

import java.io.Serializable;

@Data
public class MemberCategoryVO implements Serializable {
    private String category;
    private Long count;
}
