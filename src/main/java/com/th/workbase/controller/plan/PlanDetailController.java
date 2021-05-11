package com.th.workbase.controller.plan;


import com.th.workbase.bean.plan.PlanDetail;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.service.plan.PlanDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author cc
 * @since 2021-02-26
 */
@RestController
@Api(tags = {"计划详细表管理"})
@RequestMapping("/planDetail")
public class PlanDetailController {
    @Autowired
    PlanDetailService planDetailService;

    @ApiOperation(value = "获取电铲计划完成情况", notes = "依据设备编码获取对应的计划内容")
    @GetMapping("/getByShovelNo/{equipmentNo}")
    @ApiImplicitParam(name = "equipmentNo", value = "设备编码", dataType = "string", required = true, example = "SHOVELS_1")
    public ResponseResultDto getPlanDetailByShovelNo(@PathVariable("equipmentNo") String equipmentNo) {
        return planDetailService.getPlanDetailByShovelNo(equipmentNo);
    }

    @ApiOperation(value = "获取场地计划完成情况", notes = "依据设备编码获取对应的计划内容")
    @GetMapping("/getByFieldNo/{equipmentNo}")
    @ApiImplicitParam(name = "equipmentNo", value = "设备编码", dataType = "string", required = true, example = "SHOVELS_1")
    public ResponseResultDto getPlanDetailByFieldNo(@PathVariable("equipmentNo") String equipmentNo) {
        return planDetailService.getPlanDetailByFieldNo(equipmentNo);
    }

    @ApiOperation(value = "获取场地对应的计划内容", notes = "获取场地对应的计划内容")
    @GetMapping("/getDetailByEquipNo/{equipmentNo}")
    @ApiImplicitParam(name = "equipmentNo", value = "设备编码", dataType = "string", required = true, example = "KP_1")
    public ResponseResultDto getDetailByEquipNo(@PathVariable("equipmentNo") String equipmentNo) {
        return planDetailService.getDetailByEquipNo(equipmentNo);
    }


    @ApiOperation(value = "删除一条详细计划", notes = "删除一条详细计划")
    @DeleteMapping("/planDetail/{detailId}")
    @ApiImplicitParam(name = "detailId", value = "详细计划主键", dataType = "string", required = true, example = "1")
    public ResponseResultDto deleteDetailPlan(@PathVariable("detailId") String detailId) {
        return planDetailService.deleteDetailPlan(detailId);
    }

    @ApiOperation(value = "调整一条详细计划", notes = "调整一条详细计划")
    @PutMapping("/planDetail")
    public ResponseResultDto updateDetailPlan(@RequestBody PlanDetail detail) {
        return planDetailService.updateDetailPlan(detail);
    }

}

