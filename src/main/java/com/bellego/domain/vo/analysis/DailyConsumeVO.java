package com.bellego.domain.vo.analysis;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailyConsumeVO {
    private LocalDate consumeDate;
    private BigDecimal totalAmount;
}
