package com.bellego.domain.dto;

import com.bellego.common.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class MemberDto extends Pagination {
    private String id; // 浼氬憳ID
    private String levelId; // 浼氬憳绛夌骇ID
    private String cardNumber; // 浼氬憳鍗″彿
    private String name; // 濮撳悕
    private String phone; // 鎵嬫満鍙?
    private Integer gender; // 鎬у埆 0-鏈煡 1-鐢?2-濂?
    private LocalDate birthday; // 鐢熸棩
    private Integer totalPoints; // 鎬荤Н鍒?
    private BigDecimal totalConsumption; // 鎬绘秷璐归噾棰?
    private Integer status; // 鐘舵€?0-绂佺敤 1-鍚敤
    private LocalDateTime createTime; // 鍒涘缓鏃堕棿
    private LocalDateTime updateTime; // 鏇存柊鏃堕棿
    private LocalDateTime lastConsumeTime; // 鏈€鍚庢秷璐规椂闂?
}

