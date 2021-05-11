package com.th.workbase.controller.equipment;


import com.th.workbase.bean.equipment.EquipmentHistoryDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.service.equipment.EquipmentHistoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author cc
 * @since 2021-02-28
 */
@RestController
@RequestMapping("/history")
public class EquipmentHistoryController {

    @Autowired
    private EquipmentHistoryService equipmentHistoryService;

    @ApiOperation(value = "新增设备历史记录", notes = "新增设备历史记录接口")
    @PostMapping("/history")
    public ResponseResultDto createEquipmentHistory(@RequestBody @ApiParam(name = "设备历史对象", value = "传入json格式", required = true) EquipmentHistoryDto equipmentHistoryDto, HttpServletRequest request) {
        return equipmentHistoryService.createEquipmentHistory(request, equipmentHistoryDto);
    }

    @ApiOperation(value = "获取设备历史记录", notes = "获取设备历史记录接口")
    @PostMapping("/histories")
    public ResponseResultDto getEquipment(HttpServletRequest request,@RequestBody EquipmentHistoryDto equipmentHistoryDto, HttpServletResponse response) {
        return equipmentHistoryService.getEquipmentHistory(request, equipmentHistoryDto);
    }
}

