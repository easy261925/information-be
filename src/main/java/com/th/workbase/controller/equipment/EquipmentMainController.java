package com.th.workbase.controller.equipment;


import com.th.workbase.bean.equipment.EquipmentMain;
import com.th.workbase.bean.equipment.vo.StatisticVo;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.service.equipment.EquipmentMainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author cc
 * @since 2021-02-24
 */
@RestController
@RequestMapping("/equipment")
@Api(tags = {"设备管理"})
public class EquipmentMainController {

    @Autowired
    private EquipmentMainService equipmentMainService;

    @ApiOperation(value = "新增设备", notes = "新增设备接口")
    @PostMapping("/equipment")
    public ResponseResultDto createEquipment(@RequestBody @ApiParam(name = "设备对象", value = "传入json格式", required = true) EquipmentMain equipmentMain, HttpServletRequest request, HttpServletResponse response) {
        return equipmentMainService.createEquipment(request, equipmentMain);
    }

    @ApiOperation(value = "获取设备", notes = "获取设备接口")
    @GetMapping("/equipment")
    public ResponseResultDto getEquipment(HttpServletRequest request, int current, int pageSize, @ApiIgnore EquipmentMain equipmentMain, HttpServletResponse response) {
        return equipmentMainService.getEquipment(request, equipmentMain, current, pageSize);
    }

    @ApiOperation(value = "获取设备信息", notes = "通过设备ID取得设备当前信息")
    @GetMapping("/equipmentByDeviceId/{deviceId}")
    @ApiImplicitParam(name = "equipmentNo", value = "设备编码", dataType = "string", required = true, example = "SHOVELS_1")
    public ResponseResultDto getEquipmentByDeviceId(@PathVariable("deviceId") String deviceId) {
        return equipmentMainService.getEquipmentByDeviceId(deviceId);
    }

    @ApiOperation(value = "删除设备", notes = "删除设备接口")
    @DeleteMapping("/equipment/{id}")
    public ResponseResultDto deleteEquipment(@PathVariable("id") Integer id, HttpServletResponse response) {
        return equipmentMainService.deleteEquipment(id);
    }

    @ApiOperation(value = "修改设备", notes = "修改设备接口")
    @PutMapping("/equipment")
    public ResponseResultDto updateEquipment(@RequestBody @ApiParam(name = "设备对象", value = "传入json格式", required = true) EquipmentMain equipmentMain, HttpServletRequest request) {
        return equipmentMainService.updateEquipment(request, equipmentMain);
    }

    @ApiOperation(value = "修改电铲工作状态", notes = "修改电铲工作状态")
    @PutMapping("/shovelStatus")
    public ResponseResultDto updateShovelStatus(@RequestBody EquipmentMain equipmentMain) {
        return equipmentMainService.updateShovelStatus(equipmentMain);
    }

    @ApiOperation(value = "修改大车工作状态", notes = "修改大车工作状态")
    @PutMapping("/cartStatus")
    public ResponseResultDto updateCartStatus(@RequestBody EquipmentMain equipmentMain) {
        return equipmentMainService.updateCartStatus(equipmentMain);
    }

    @ApiOperation(value = "修改场地工作状态", notes = "修改场地工作状态")
    @PutMapping("/fieldStatus")
    public ResponseResultDto updateFieldStatus(@RequestBody EquipmentMain equipmentMain) {
        return equipmentMainService.updateFieldStatus(equipmentMain);
    }

    @ApiOperation(value = "获取在线设备列表", notes = "获取在线设备列表")
    @GetMapping("/equipmentsOnline")
    public ResponseResultDto getEquipmentsOnline() {
        return equipmentMainService.getEquipmentsOnline();
    }

    @ApiOperation(value = "按班次获取大车周转率", notes = "获取设备周转率")
    @GetMapping("/turnoverByEquipNo")
    public ResponseResultDto getTurnoverByCartNo(StatisticVo statisticParam) {
        return equipmentMainService.getTurnoverByCartNo(statisticParam);
    }

    @ApiOperation(value = "按人员获取周转率", notes = "获取人员周转率")
    @GetMapping("/turnoverByHandler")
    public ResponseResultDto getTurnoverByHandler(StatisticVo statisticParam) {
        return equipmentMainService.getTurnoverByHandler(statisticParam);
    }

    @ApiOperation(value = "按车型获取周转率", notes = "获取车型周转率")
    @GetMapping("/turnoverByType")
    public ResponseResultDto getTurnoverByType(StatisticVo statisticParam) {
        return equipmentMainService.getTurnoverByType(statisticParam);
    }

    @ApiOperation(value = "统计电铲工作量", notes = "统计电铲工作量")
    @GetMapping("/countShovelWork")
    public ResponseResultDto countShovelWork(StatisticVo statisticParam) {
        return equipmentMainService.countShovelWork(statisticParam);
    }

    @ApiOperation(value = "统计大车工作量", notes = "统计大车工作量")
    @GetMapping("/countCartWork")
    public ResponseResultDto countCartWork(StatisticVo statisticParam) {
        return equipmentMainService.countCartWork(statisticParam);
    }

    @ApiOperation(value = "获取可用的电铲", notes = "获取可用的电铲")
    @GetMapping("/availableShovel")
    public ResponseResultDto getAvailableShovel() {
        return equipmentMainService.getAvailableShovel();
    }

    @ApiOperation(value = "获取可用的大车", notes = "获取可用的大车")
    @GetMapping("/availableCart")
    public ResponseResultDto getAvailableCart() {
        return equipmentMainService.getAvailableCart();
    }

    @ApiOperation(value = "获取可用的场地", notes = "获取可用的场地")
    @GetMapping("/availableField")
    public ResponseResultDto getAvailableField() {
        return equipmentMainService.getAvailableField();
    }

    @ApiOperation(value = "获取有任务的电铲", notes = "计划中提到的电铲")
    @GetMapping("/getShovelWithTask")
    public ResponseResultDto getShovelWithTask() {
        return equipmentMainService.getShovelWithTask();
    }

    @ApiOperation(value = "更新推送字段", notes = "更新推送字段")
    @PutMapping("/pushEnable")
    public ResponseResultDto updateEquipmentPushEnable(@RequestBody EquipmentMain equipmentMain) {
        return equipmentMainService.updateEquipmentPushEnable(equipmentMain);
    }
    @ApiOperation(value = "查询设备推送字段", notes = "查询设备推送字段")
    @GetMapping("/pushEnable")
    public ResponseResultDto getEquipmentPushEnable( String equipmentNo) {
        return equipmentMainService.getEquipmentPushEnable(equipmentNo);
    }
}

