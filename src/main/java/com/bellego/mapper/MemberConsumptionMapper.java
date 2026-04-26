package com.bellego.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bellego.domain.entity.MemberConsumption;
import com.bellego.domain.vo.analysis.DailyConsumeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface MemberConsumptionMapper extends BaseMapper<MemberConsumption> {
    List<DailyConsumeVO> getDailyTotalAmount(Date start, Date end);
}

