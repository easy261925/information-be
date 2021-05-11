package com.th.workbase.service.equipment;

import com.th.workbase.bean.equipment.EquipmentMain;
import com.th.workbase.bean.equipment.WorkStatusHistoryDto;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 设备历史记录
 * @author tangj
 * @since 2021-03-03
 */
public interface WorkStatusHistoryService extends IService<WorkStatusHistoryDto> {
    //改变设备状态时,添加一条历史状态数据
    void addStatusData(EquipmentMain equipment);
}
