package com.th.workbase.service.equipment;

import com.th.workbase.bean.equipment.EquipmentHistoryDto;
import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.system.ResponseResultDto;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author cc
 * @since 2021-02-28
 */
public interface EquipmentHistoryService extends IService<EquipmentHistoryDto> {

    ResponseResultDto createEquipmentHistory(HttpServletRequest request, EquipmentHistoryDto equipmentHistoryDto);

    ResponseResultDto getEquipmentHistory(HttpServletRequest request, EquipmentHistoryDto equipmentHistoryDto);
}
