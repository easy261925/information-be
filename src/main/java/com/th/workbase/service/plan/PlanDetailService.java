package com.th.workbase.service.plan;

import com.th.workbase.bean.plan.PlanDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.system.ResponseResultDto;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author cc
 * @since 2021-02-26
 */
public interface PlanDetailService extends IService<PlanDetail> {

    ResponseResultDto getPlanDetailByShovelNo(String equipmentNo);

    ResponseResultDto getDetailByEquipNo(String equipmentNo);

    ResponseResultDto getPlanDetailByFieldNo(String equipmentNo);

    ResponseResultDto deleteDetailPlan(String detailId);

    ResponseResultDto updateDetailPlan(PlanDetail detail);
}
