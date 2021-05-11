package com.th.workbase.service.equipment.impl;

import com.th.workbase.bean.equipment.EquipmentMain;
import com.th.workbase.bean.equipment.WorkStatusHistoryDto;
import com.th.workbase.common.commonEnum.EquipmentType;
import com.th.workbase.mapper.equipment.WorkStatusHistoryMapper;
import com.th.workbase.service.equipment.WorkStatusHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * @author tangj
 * @since 2021-03-03
 */
@Service
public class WorkStatusHistoryServiceImpl extends ServiceImpl<WorkStatusHistoryMapper, WorkStatusHistoryDto> implements WorkStatusHistoryService {
    @Resource
    WorkStatusHistoryMapper workStatusHistoryMapper;

    /**
     * 在设备改变状态时,在设备历史记录表中添加信息
     */
    @Override
    public void addStatusData(EquipmentMain equipment) {
        WorkStatusHistoryDto workStatusHistoryDto = new WorkStatusHistoryDto();
        workStatusHistoryDto.setHandlerId(equipment.getHandlerId());
        workStatusHistoryDto.setEquipmentNo(equipment.getEquipmentNo());
        workStatusHistoryDto.setStatus(equipment.getEquipmentStatus());
        workStatusHistoryDto.setEquipmentType(equipment.getEquipmentType());
        workStatusHistoryDto.setRemark(equipment.getRemark());
        workStatusHistoryMapper.insert(workStatusHistoryDto);
    }
}
