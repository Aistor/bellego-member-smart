package com.bellego.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bellego.domain.entity.Member;
import com.bellego.domain.vo.MemberLevelCountVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper extends BaseMapper<Member> {
    List<MemberLevelCountVo> getLevelCount();
}

