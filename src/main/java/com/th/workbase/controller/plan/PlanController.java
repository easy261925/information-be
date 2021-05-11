package com.th.workbase.controller.plan;


import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.vo.PlanVo;
import com.th.workbase.config.annotation.InLogAnnotation;
import com.th.workbase.service.plan.PlanService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author cc
 * @since 2021-02-26
 */
@RestController
@RequestMapping("/plan")
public class PlanController {

    @Autowired
    private PlanService planService;

    @ApiOperation(value = "增加/修改计划", notes = "修改计划接口")
    @PostMapping("/plan")
    public ResponseResultDto createPlan(@RequestBody @ApiParam(name = "计划对象", value = "传入json格式", required = true) PlanVo planVo, HttpServletRequest request) {
        return planService.createPlan(request, planVo);
    }

    @ApiOperation(value = "获取计划", notes = "获取计划接口")
    @GetMapping("/plan")
    public ResponseResultDto getPlan(HttpServletRequest request, @ApiIgnore PlanVo planVo) {
        return planService.getPlan(request, planVo);
    }

    @ApiOperation(value = "删除计划", notes = "删除计划")
    @DeleteMapping("/plan/{planId}")
    public ResponseResultDto deletePlan(@PathVariable("planId") String planId) {
        return planService.deletePlan(planId);
    }


}

