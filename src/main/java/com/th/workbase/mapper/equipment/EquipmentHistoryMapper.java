package com.th.workbase.mapper.equipment;

import com.th.workbase.bean.equipment.EquipmentHistoryDto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author cc
 * @since 2021-02-28
 */
public interface EquipmentHistoryMapper extends BaseMapper<EquipmentHistoryDto> {

    List<EquipmentHistoryDto> selectPositionInLastMinute(@Param("equipmentNo") String equipmentNo, @Param("previousMinute")String previousMinute);

    List<EquipmentHistoryDto> getHistoryPosition(EquipmentHistoryDto equipmentHistoryDto);
}
