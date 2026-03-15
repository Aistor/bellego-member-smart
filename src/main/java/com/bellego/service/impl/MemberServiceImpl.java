package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bellego.domain.dto.MemberDto;
import com.bellego.domain.entity.Member;
import com.bellego.domain.vo.MemberVo;
import com.bellego.mapper.MemberMapper;
import com.bellego.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService{

    @Override
    public void saveMember(MemberDto memberDto) {

    }

    @Override
    public MemberVo getMemberById(String id) {
        return null;
    }

    @Override
    public IPage<MemberVo> memberList(MemberDto memberDto) {
        return null;
    }

    @Override
    public void updateMember(String id, MemberDto memberDto) {

    }

    @Override
    public void deleteById(String id) {

    }

    @Override
    public void batchDelete(List<String> ids) {

    }
}
