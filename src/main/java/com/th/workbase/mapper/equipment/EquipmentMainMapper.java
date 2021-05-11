package com.th.workbase.mapper.equipment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.th.workbase.bean.equipment.EquipmentMain;
import com.th.workbase.bean.equipment.vo.StatisticVo;

import java.util.List;

/**
 * @author cc
 * @since 2021-02-24
 */
public interface EquipmentMainMapper extends BaseMapper<EquipmentMain> {
    List<EquipmentMain> getEquipmentCurrentPosition();

    /**
     * 按大车统计周转率
     */
    List<StatisticVo> getTurnoverByCartNo(StatisticVo statisticParam);

    /**
     * 按人员统计周转率
     */
    List<StatisticVo> getTurnoverByHandler(StatisticVo statisticParam);

    /**
     * 按大车车型统计周转率
     */
    List<StatisticVo> getTurnoverByType(StatisticVo statisticParam);

    /**
     * 统计电铲工作量
     */
    List<StatisticVo> countShovelWork(StatisticVo statisticParam);


    /**
     * 统计大车工作量
     */
    List<StatisticVo> countCartWork(StatisticVo statisticParam);
}
