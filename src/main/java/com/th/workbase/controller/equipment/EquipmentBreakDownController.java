package com.th.workbase.controller.equipment;


import com.th.workbase.bean.equipment.EquipmentBreakDownDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.service.equipment.EquipmentBreakDownService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author tangj
 * @since 2021-03-18
 */
@RestController
@RequestMapping("/breakDown")
public class EquipmentBreakDownController {
    @Autowired
    EquipmentBreakDownService equipmentBreakDownService;


    @ApiOperation(value = "故障设备轮询", notes = "故障设备轮询")
    @GetMapping("/new")
    public ResponseResultDto getNewBreakDown() {
        return equipmentBreakDownService.getNewBreakDown();
    }

    @ApiOperation(value = "故障设备查询", notes = "故障设备查询")
    @GetMapping("/all")
    public ResponseResultDto getBreakDownList(EquipmentBreakDownDto breakDownDto, int current, int pageSize) {
        return equipmentBreakDownService.getBreakDownList(breakDownDto,current, pageSize);
    }

    //调度处理异常
    @ApiOperation(value = "调度忽略异常", notes = "调度忽略异常")
    @PutMapping("/ignore")
    public ResponseResultDto ignoreBreakDown(@RequestBody EquipmentBreakDownDto breakDownDto){
        return equipmentBreakDownService.ignoreBreakDown(breakDownDto);
    }

    @ApiOperation(value = "调度处理电铲异常", notes = "调度处理电铲异常")
    @PutMapping("/processShovel")
    public ResponseResultDto processShovelBreakDown(@RequestBody EquipmentBreakDownDto breakDownDto){
        return equipmentBreakDownService.processShovelBreakDown(breakDownDto);
    }

    @ApiOperation(value = "调度处理大车异常", notes = "调度处理大车异常")
    @PutMapping("/processCart")
    public ResponseResultDto processCartBreakDown(@RequestBody EquipmentBreakDownDto breakDownDto){
        return equipmentBreakDownService.processCartBreakDown(breakDownDto);
    }

    @ApiOperation(value = "调度处理场地异常", notes = "调度处理场地异常")
    @PutMapping("/processField")
    public ResponseResultDto processFieldBreakDown(@RequestBody EquipmentBreakDownDto breakDownDto){
        return equipmentBreakDownService.processFieldBreakDown(breakDownDto);
    }

}

