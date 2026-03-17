package com.bellego.common.util;

import com.bellego.domain.entity.Admin;
import com.bellego.domain.entity.Coupon;
import com.bellego.domain.entity.Member;
import com.bellego.domain.entity.MemberConsumption;
import com.bellego.domain.entity.MemberCoupon;
import com.bellego.domain.entity.MemberLevel;
import com.bellego.domain.entity.OperationLog;
import com.bellego.domain.entity.PointDetail;
import com.bellego.domain.entity.PointRule;
import com.bellego.domain.entity.Role;
import com.bellego.domain.entity.Store;
import com.bellego.domain.vo.AdminVo;
import com.bellego.domain.vo.ConsumptionVo;
import com.bellego.domain.vo.CouponVo;
import com.bellego.domain.vo.MemberCouponVo;
import com.bellego.domain.vo.MemberLevelVo;
import com.bellego.domain.vo.MemberVo;
import com.bellego.domain.vo.OperationLogVo;
import com.bellego.domain.vo.PointDetailVo;
import com.bellego.domain.vo.PointRuleVo;
import com.bellego.domain.vo.RoleVo;
import com.bellego.domain.vo.StoreVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class VoMapper {

    public MemberVo toMemberVo(Member source) {
        return copy(source, new MemberVo());
    }

    public MemberLevelVo toMemberLevelVo(MemberLevel source) {
        return copy(source, new MemberLevelVo());
    }

    public ConsumptionVo toConsumptionVo(MemberConsumption source) {
        return copy(source, new ConsumptionVo());
    }

    public CouponVo toCouponVo(Coupon source) {
        return copy(source, new CouponVo());
    }

    public MemberCouponVo toMemberCouponVo(MemberCoupon source) {
        return copy(source, new MemberCouponVo());
    }

    public PointRuleVo toPointRuleVo(PointRule source) {
        return copy(source, new PointRuleVo());
    }

    public PointDetailVo toPointDetailVo(PointDetail source) {
        return copy(source, new PointDetailVo());
    }

    public StoreVo toStoreVo(Store source) {
        return copy(source, new StoreVo());
    }

    public AdminVo toAdminVo(Admin source) {
        return copy(source, new AdminVo());
    }

    public RoleVo toRoleVo(Role source) {
        return copy(source, new RoleVo());
    }

    public OperationLogVo toOperationLogVo(OperationLog source) {
        return copy(source, new OperationLogVo());
    }

    private <S, T> T copy(S source, T target) {
        if (source == null) {
            return null;
        }
        BeanUtils.copyProperties(source, target);
        return target;
    }
}