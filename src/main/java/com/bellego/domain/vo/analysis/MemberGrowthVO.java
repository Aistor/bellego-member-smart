package com.bellego.domain.vo.analysis;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class MemberGrowthVO implements Serializable {
    private LocalDate date;
    private Integer day;
    private String month;
    private Long count;
}
