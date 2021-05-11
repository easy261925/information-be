package com.th.workbase.service.plan;

import com.th.workbase.bean.plan.Plan;
import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.vo.PlanVo;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author cc
 * @since 2021-02-26
 */
public interface PlanService extends IService<Plan> {

    ResponseResultDto createPlan(HttpServletRequest request, PlanVo planVo);

    ResponseResultDto getPlan(HttpServletRequest request, PlanVo planVo);

    ResponseResultDto deletePlan(String planId);
}
