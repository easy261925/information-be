package com.th.workbase.service.equipment;

import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.equipment.EquipmentBreakDownDto;
import com.th.workbase.bean.equipment.vo.EquipmentBreakDownVo;
import com.th.workbase.bean.system.ResponseResultDto;

import java.util.List;

/**
 * @author tangj
 * @since 2021-03-18
 */
public interface EquipmentBreakDownService extends IService<EquipmentBreakDownDto> {
    /**
     * 轮询新的故障/维护信息
     */
    ResponseResultDto getNewBreakDown();

    /**
     * 故障/维护信息列表
     *
     * @param breakDownDto 查询条件
     * @param current      当前页
     * @param pageSize     每页显示条数
     */
    ResponseResultDto getBreakDownList(EquipmentBreakDownDto breakDownDto, int current, int pageSize);

    /**
     * 调度处理电铲故障具体操作
     */
    ResponseResultDto processShovelBreakDown(EquipmentBreakDownDto breakDownDto);

    /**
     * 调度忽略本条故障/维护信息
     */
    ResponseResultDto ignoreBreakDown(EquipmentBreakDownDto breakDownDto);

    /**
     * 调度处理大车故障具体操作
     */
    ResponseResultDto processCartBreakDown(EquipmentBreakDownDto breakDownDto);

    /**
     * 调度处理场地故障具体操作
     */
    ResponseResultDto processFieldBreakDown(EquipmentBreakDownDto breakDownDto);

    List<EquipmentBreakDownVo> breakDto2Vo(List<EquipmentBreakDownDto> dtoList);
}
