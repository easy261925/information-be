package com.th.workbase.service.equipment;

import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.equipment.EquipmentMain;
import com.th.workbase.bean.equipment.vo.StatisticVo;
import com.th.workbase.bean.plan.PlanTaskDto;
import com.th.workbase.bean.system.ResponseResultDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author cc
 * @since 2021-02-24
 */
public interface EquipmentMainService extends IService<EquipmentMain> {

    ResponseResultDto createEquipment(HttpServletRequest request, EquipmentMain equipmentMain);

    ResponseResultDto getEquipment(HttpServletRequest request, EquipmentMain equipmentMain, int current, int pageSize);

    ResponseResultDto deleteEquipment(Integer id);

    ResponseResultDto updateEquipment(HttpServletRequest request, EquipmentMain equipmentMain);

    /**
     * 通过设备id获得设备信息
     *
     * @param deviceId 设备id
     */
    ResponseResultDto getEquipmentByDeviceId(String deviceId);

    /**
     * 更新电铲状态
     */
    ResponseResultDto updateShovelStatus(EquipmentMain equipmentMain);

    /**
     * 翻译设备名称
     *
     * @param equipmentNo 设备编码
     */
    String translateFromEquipNo2Name(String equipmentNo);

    /**
     * 电铲推送任务
     */
    ResponseResultDto shovelPublishTask(String equipmentNo, List<PlanTaskDto> taskList);

    /**
     * 更改大车状态
     */
    ResponseResultDto updateCartStatus(EquipmentMain equipmentMain);

    /**
     * 更改场地状态
     */
    ResponseResultDto updateFieldStatus(EquipmentMain equipmentMain);

    /**
     * 检查不饱和的任务,推送任务消息
     */
    ResponseResultDto checkUnsaturatedDetailPlanAndSendTask();

    ResponseResultDto getEquipmentsOnline();

    ResponseResultDto getTurnoverByCartNo(StatisticVo statisticParam);

    ResponseResultDto getAvailableShovel();

    ResponseResultDto getAvailableCart();

    ResponseResultDto getAvailableField();

    ResponseResultDto getTurnoverByHandler(StatisticVo statisticParam);

    ResponseResultDto getTurnoverByType(StatisticVo statisticParam);

    ResponseResultDto getShovelWithTask();

    ResponseResultDto countShovelWork(StatisticVo statisticParam);

    ResponseResultDto countCartWork(StatisticVo statisticParam);

    ResponseResultDto updateEquipmentPushEnable(EquipmentMain equipmentMain);

    ResponseResultDto getEquipmentPushEnable(String equipmentNo);
}
