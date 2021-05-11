package com.th.workbase.service.equipment.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.equipment.EquipmentBreakDownDto;
import com.th.workbase.bean.equipment.EquipmentMain;
import com.th.workbase.bean.equipment.MessageDto;
import com.th.workbase.bean.equipment.ProcessBreakHistoryDto;
import com.th.workbase.bean.equipment.vo.EquipmentBreakDownVo;
import com.th.workbase.bean.plan.*;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysUserDto;
import com.th.workbase.common.commonEnum.EquipmentStatus;
import com.th.workbase.common.commonEnum.EquipmentType;
import com.th.workbase.common.commonEnum.FaultState;
import com.th.workbase.common.commonEnum.TaskState;
import com.th.workbase.mapper.equipment.EquipmentBreakDownMapper;
import com.th.workbase.mapper.equipment.EquipmentMainMapper;
import com.th.workbase.mapper.equipment.ProcessBreakHistoryMapper;
import com.th.workbase.mapper.plan.*;
import com.th.workbase.mapper.system.SysUserMapper;
import com.th.workbase.service.common.DefineSwitchService;
import com.th.workbase.service.common.JPushService;
import com.th.workbase.service.equipment.EquipmentBreakDownService;
import com.th.workbase.service.equipment.EquipmentMainService;
import com.th.workbase.service.plan.PlanTaskService;
import com.th.workbase.service.system.SysDictService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

/**
 * @author tangj
 * @since 2021-03-18
 */
@Service
public class EquipmentBreakDownServiceImpl extends ServiceImpl<EquipmentBreakDownMapper, EquipmentBreakDownDto> implements EquipmentBreakDownService {

    @Resource
    EquipmentBreakDownMapper breakDownMapper;
    @Autowired
    DefineSwitchService defineSwitchService;
    @Autowired
    EquipmentMainService equipmentMainService;
    @Resource
    EquipmentMainMapper equipmentMapper;
    @Resource
    SysUserMapper userMapper;
    @Autowired
    PlanTaskService planTaskService;
    @Resource
    PlanDetailMapper detailMapper;
    @Resource
    PlanTaskMapper taskMapper;
    @Autowired
    JPushService jPushService;
    @Resource
    TaskToDoMapper toDoMapper;
    @Resource
    PlanMapper planMapper;
    @Resource
    CartWorkTargetMapper cartWorkTargetMapper;
    @Resource
    ProcessBreakHistoryMapper breakHistoryMapper;
    @Autowired
    SysDictService sysDictService;

    @Override
    public ResponseResultDto getNewBreakDown() {
        QueryWrapper<EquipmentBreakDownDto> breakDownWrapper = new QueryWrapper<>();
        breakDownWrapper.eq("FAULT_STATE", FaultState.Create.getState());
        List<EquipmentBreakDownDto> breakDownDtoPage = breakDownMapper.selectList(breakDownWrapper);
        List<EquipmentBreakDownVo> vos = breakDto2Vo(breakDownDtoPage);
        return ResponseResultDto.ok().data("data", vos);
    }

    @Override
    public ResponseResultDto getBreakDownList(EquipmentBreakDownDto breakDownDto, int current, int pageSize) {
        QueryWrapper<EquipmentBreakDownDto> breakDownWrapper = new QueryWrapper<>();
        Page<EquipmentBreakDownDto> page = new Page<>(current, pageSize);
        String faultState = breakDownDto.getFaultState();
        String faultType = breakDownDto.getFaultType();
        String scheduleDate = breakDownDto.getScheduleDate();
        String shiftType = breakDownDto.getShiftType();
        if (StringUtils.isNotBlank(faultState)) {
            breakDownWrapper.eq("FAULT_STATE", faultState);
        }
        if (StringUtils.isNotBlank(faultType)) {
            breakDownWrapper.eq("FAULT_TYPE", faultType);
        }
        if (StringUtils.isNotBlank(scheduleDate)) {
            breakDownWrapper.eq("SCHEDULE_DATE", scheduleDate);
        }
        if (StringUtils.isNotBlank(shiftType)) {
            breakDownWrapper.eq("SHIFT_TYPE", shiftType);
        }
        Page<EquipmentBreakDownDto> breakDownDtoPage = breakDownMapper.selectPage(page, breakDownWrapper);
        return ResponseResultDto.ok().data("data", breakDto2Vo(breakDownDtoPage.getRecords())).data("total", breakDownDtoPage.getTotal());
    }

    @Override
    public List<EquipmentBreakDownVo> breakDto2Vo(List<EquipmentBreakDownDto> dtoList) {
        List<EquipmentBreakDownVo> voList = new ArrayList<>();
        for (EquipmentBreakDownDto breakDownDto : dtoList) {
            EquipmentBreakDownVo breakDownVo = new EquipmentBreakDownVo();
            copyProperties(breakDownDto, breakDownVo);
            String equipmentNo = breakDownDto.getEquipmentNo();
            QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
            equipmentWrapper.eq("EQUIPMENT_NO", equipmentNo);
            EquipmentMain equipmentMain = equipmentMapper.selectOne(equipmentWrapper);
            String equipmentType = equipmentMain.getEquipmentType();
            breakDownVo.setEquipmentType(equipmentType);
            String equipmentName = equipmentMain.getEquipmentName();
            breakDownVo.setEquipmentName(equipmentName);//设备名称
            String title = equipmentName;
            String faultDetailType = breakDownDto.getFaultDetailType();
            if (EquipmentType.Cart.getType().equals(equipmentType)) {
                breakDownVo.setEquipmentTypeName("大车");//设备类型
                String detailType = equipmentMain.getDetailType();
                String detailTypeName = sysDictService.queryDict("carts_type", detailType);
                breakDownVo.setEquipmentDetailTypeName(detailTypeName);//设备子类型
                String faultDetailTypeName = sysDictService.queryDict("cart_fault_detail_types", faultDetailType);
                breakDownVo.setFaultDetailName(faultDetailTypeName);
            } else if (EquipmentType.Shovel.getType().equals(equipmentType)) {
                String oreField = equipmentMain.getOreField();//矿区编码
                String fieldName = sysDictService.queryDict("ore_field", oreField);//矿区名称
                breakDownVo.setOreFieldName(fieldName);//所属矿区
                title = fieldName + equipmentName;
                breakDownVo.setEquipmentTypeName("电铲");//设备类型
                String detailType = equipmentMain.getDetailType();
                String detailTypeName = sysDictService.queryDict("shovel_type", detailType);
                breakDownVo.setEquipmentDetailTypeName(detailTypeName);//设备子类型
                String faultDetailTypeName = sysDictService.queryDict("shovel_fault_detail_types", faultDetailType);
                breakDownVo.setFaultDetailName(faultDetailTypeName);
            } else {
                breakDownVo.setEquipmentTypeName("场地");//设备类型
                String faultDetailTypeName = sysDictService.queryDict("site_fault_detail_types", faultDetailType);
                breakDownVo.setFaultDetailName(faultDetailTypeName);
            }
            SysUserDto user = userMapper.selectById(breakDownDto.getHandlerId());
            breakDownVo.setHandlerName(user.getUserName());//上报人
            breakDownVo.setHandlerPhone(user.getPhone());//上报人电话
            title += "发生故障";
            breakDownVo.setTitle(title);//信息标题
            voList.add(breakDownVo);
        }
        return voList;
    }

    @Override
    public ResponseResultDto ignoreBreakDown(EquipmentBreakDownDto breakDownDto) {
        breakDownMapper.updateById(breakDownDto);
        return ResponseResultDto.ok();
    }


    @Override
    public ResponseResultDto processCartBreakDown(EquipmentBreakDownDto breakDownDto) {
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        String targetCart = breakDownDto.getEquipmentNo();//前台借用这个字段,传入的是要替换的大车
        EquipmentBreakDownDto breakDownData = breakDownMapper.selectById(breakDownDto.getId());
        breakDownData.setFaultState(FaultState.Processing.getState());
        breakDownMapper.updateById(breakDownData);
        String currentCart = breakDownData.getEquipmentNo();//当前故障大车编码
        //检查故障车辆目前是否仍处于故障状态,如果不在故障状态取消这次处理
        QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
        equipmentWrapper.eq("EQUIPMENT_NO", currentCart);
        equipmentWrapper.eq("EQUIPMENT_STATUS", EquipmentStatus.BreakDown.getStatus());
        EquipmentMain breakDownCart = equipmentMapper.selectOne(equipmentWrapper);
        if (breakDownCart == null) {
            return ResponseResultDto.ServiceError("当前车辆故障状态已恢复,不需要处理");
        }
        //记录替换历史
        ProcessBreakHistoryDto breakHistoryDto = new ProcessBreakHistoryDto();
        breakHistoryDto.setEquipmentDown(currentCart);
        breakHistoryDto.setEquipmentUp(targetCart);
        breakHistoryDto.setScheduleDate(split[0]);
        breakHistoryDto.setShiftType(split[1]);
        breakHistoryDto.setBreakDownId(breakDownDto.getId());
        breakHistoryMapper.insert(breakHistoryDto);
        //检查目标车辆有没有正在执行的普通任务
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.eq("RECEIVER", targetCart);
        taskWrapper.lt("TASK_STATE", TaskState.LeaveField.getState());
        taskWrapper.eq("SCHEDULE_DATE", split[0]);
        taskWrapper.eq("SHIFT_TYPE", split[1]);
        int count = taskMapper.selectCount(taskWrapper);
        if (count > 0) {
            return ResponseResultDto.ServiceError("指配的车辆正在执行任务,请指配其他车辆");
        }
        //检查目标车辆有没有正在执行的临时任务
        QueryWrapper<TaskToDoDto> toDoWrapper = new QueryWrapper<>();
        toDoWrapper.eq("EXECUTOR", targetCart);
        toDoWrapper.in("COMPLETE_STATE", TaskState.ToDoTaskCreate.getState(), TaskState.ToDoTaskProcessing.getState());
        count = toDoMapper.selectCount(toDoWrapper);
        if (count > 0) {
            return ResponseResultDto.ServiceError("指配的车辆有其他指配任务,请指配其他车辆");
        }
        equipmentWrapper.clear();
        equipmentWrapper.eq("EQUIPMENT_NO", targetCart);
        EquipmentMain equipmentMain = equipmentMapper.selectOne(equipmentWrapper);
        String handlerId = equipmentMain.getHandlerId();//取得目标车辆司机
        //检查故障大车当前班次是否在执行任务
        taskWrapper.clear();
        taskWrapper.eq("RECEIVER", currentCart);
        taskWrapper.lt("TASK_STATE", TaskState.LeaveField.getState());
        taskWrapper.eq("SCHEDULE_DATE", split[0]);
        taskWrapper.eq("SHIFT_TYPE", split[1]);
        PlanTaskDto taskDto = taskMapper.selectOne(taskWrapper);
        if (taskDto != null) {
            taskDto.setReceiver(targetCart);
            taskDto.setReceiveHandler(Integer.parseInt(handlerId));
            taskMapper.updateById(taskDto);
            String shovelNo = taskDto.getPublisher();//故障车辆对应的电铲
            QueryWrapper<CartWorkTarget> cartWorkTargetQueryWrapper = new QueryWrapper<>();
            cartWorkTargetQueryWrapper.eq("CART_NO", targetCart);
            cartWorkTargetQueryWrapper.eq("SCHEDULE_DATE", split[0]);
            cartWorkTargetQueryWrapper.eq("SHIFT_TYPE", split[1]);
            CartWorkTarget cartWorkTarget = cartWorkTargetMapper.selectOne(cartWorkTargetQueryWrapper);
            if (cartWorkTarget != null) {
                cartWorkTarget.setShovelNo(shovelNo);
                cartWorkTargetMapper.updateById(cartWorkTarget);
            } else {
                cartWorkTarget = new CartWorkTarget();
                cartWorkTarget.setScheduleDate(split[0]);
                cartWorkTarget.setShiftType(split[1]);
                cartWorkTarget.setCartNo(targetCart);
                cartWorkTarget.setShovelNo(shovelNo);
                cartWorkTargetMapper.insert(cartWorkTarget);
            }
            String taskState = taskDto.getTaskState();//任务状态
            if (TaskState.LeaveShovel.getState().compareTo(taskState) > 0) {
                String currentCartName = equipmentMainService.translateFromEquipNo2Name(currentCart);
                String targetCartName = equipmentMainService.translateFromEquipNo2Name(targetCart);
                //任务状态在离开电铲之前,通知电铲换车
                MessageDto shovelMsg = new MessageDto();
                shovelMsg.setSender(currentCart);
                shovelMsg.setReceiver(shovelNo);
                shovelMsg.setContent(currentCartName + "已更换为" + targetCartName + ",请准备开始工作");
                shovelMsg.setEquipmentType(EquipmentType.Shovel.getType());
                shovelMsg.setFkTaskId(0);
                jPushService.sendMsg(shovelMsg);
            } else if (TaskState.LeaveField.getState().compareTo(taskState) > 0) {
                String currentCartName = equipmentMainService.translateFromEquipNo2Name(currentCart);
                String targetCartName = equipmentMainService.translateFromEquipNo2Name(targetCart);
                //任务状态在离开场地之前,通知场地换车
                MessageDto fieldMsg = new MessageDto();
                fieldMsg.setSender(currentCart);
                fieldMsg.setReceiver(taskDto.getDestination());
                fieldMsg.setContent(currentCartName + "已更换为" + targetCartName + ",请准备开始工作");
                fieldMsg.setEquipmentType(EquipmentType.Field.getType());
                fieldMsg.setFkTaskId(0);
                jPushService.sendMsg(fieldMsg);
            }

        }
        //检查故障大车是否在执行临时任务,将临时任务承担者换到指定车辆
        toDoWrapper.clear();
        toDoWrapper.eq("EXECUTOR", currentCart);
        toDoWrapper.in("COMPLETE_STATE", TaskState.ToDoTaskCreate.getState(), TaskState.ToDoTaskProcessing.getState());
        List<TaskToDoDto> toDoDtoList = toDoMapper.selectList(toDoWrapper);
        for (TaskToDoDto toDoDto : toDoDtoList) {
            toDoDto.setExecutor(targetCart);
            toDoMapper.updateById(toDoDto);
        }
        if (toDoDtoList.size() == 0 && taskDto == null) {
            //将故障车辆对应的电铲指配到新的目标车辆
            QueryWrapper<CartWorkTarget> cartWorkTargetQueryWrapper = new QueryWrapper<>();
            cartWorkTargetQueryWrapper.eq("CART_NO", currentCart);
            cartWorkTargetQueryWrapper.eq("SCHEDULE_DATE", split[0]);
            cartWorkTargetQueryWrapper.eq("SHIFT_TYPE", split[1]);
            CartWorkTarget currentCartWorkTarget = cartWorkTargetMapper.selectOne(cartWorkTargetQueryWrapper);
            if (currentCartWorkTarget != null) {
                String shovelNo = currentCartWorkTarget.getShovelNo();//故障车辆对应的电铲
                cartWorkTargetQueryWrapper.clear();
                cartWorkTargetQueryWrapper.eq("CART_NO", targetCart);
                cartWorkTargetQueryWrapper.eq("SCHEDULE_DATE", split[0]);
                cartWorkTargetQueryWrapper.eq("SHIFT_TYPE", split[1]);
                CartWorkTarget targetCartWorkTarget = cartWorkTargetMapper.selectOne(cartWorkTargetQueryWrapper);
                if (targetCartWorkTarget != null) {
                    targetCartWorkTarget.setShovelNo(shovelNo);
                    cartWorkTargetMapper.updateById(targetCartWorkTarget);
                } else {
                    targetCartWorkTarget = new CartWorkTarget();
                    targetCartWorkTarget.setScheduleDate(split[0]);
                    targetCartWorkTarget.setShiftType(split[1]);
                    targetCartWorkTarget.setCartNo(targetCart);
                    targetCartWorkTarget.setShovelNo(shovelNo);
                    cartWorkTargetMapper.insert(targetCartWorkTarget);
                }
            }
            return planTaskService.getNextTask(targetCart);
        }
        //将新任务通知目标车辆
        MessageDto cartMsg = new MessageDto();
        cartMsg.setSender(currentCart);
        cartMsg.setReceiver(targetCart);
        cartMsg.setContent("您有新的任务,请准备开始工作");
        cartMsg.setEquipmentType(EquipmentType.Cart.getType());
        cartMsg.setFkTaskId(0);
        jPushService.sendMsg(cartMsg);

        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto processFieldBreakDown(EquipmentBreakDownDto breakDownDto) {
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        String targetField = breakDownDto.getEquipmentNo();//前台借用这个字段,传入的是要替换的场地
        EquipmentBreakDownDto breakDownData = breakDownMapper.selectById(breakDownDto.getId());
        breakDownData.setFaultState(FaultState.Processing.getState());
        breakDownMapper.updateById(breakDownData);
        String currentField = breakDownData.getEquipmentNo();//当前故障场地编码
        //检查故障场地目前是否仍处于故障状态,如果不在故障状态取消这次处理
        QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
        equipmentWrapper.eq("EQUIPMENT_NO", currentField);
        equipmentWrapper.eq("EQUIPMENT_STATUS", EquipmentStatus.BreakDown.getStatus());
        EquipmentMain breakDownField = equipmentMapper.selectOne(equipmentWrapper);
        if (breakDownField == null) {
            return ResponseResultDto.ServiceError("当前场地故障状态已恢复,不需要处理");
        }
        //记录替换历史
        ProcessBreakHistoryDto breakHistoryDto = new ProcessBreakHistoryDto();
        breakHistoryDto.setEquipmentDown(currentField);
        breakHistoryDto.setEquipmentUp(targetField);
        breakHistoryDto.setScheduleDate(split[0]);
        breakHistoryDto.setShiftType(split[1]);
        breakHistoryDto.setBreakDownId(breakDownDto.getId());
        breakHistoryMapper.insert(breakHistoryDto);
        //查找当前班次,故障场地对应的任务,替换为目标场地
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.eq("DESTINATION", currentField);
        taskWrapper.lt("TASK_STATE", TaskState.LeaveField.getState());
        taskWrapper.eq("SCHEDULE_DATE", split[0]);
        taskWrapper.eq("SHIFT_TYPE", split[1]);
        PlanTaskDto task = new PlanTaskDto();
        List<PlanTaskDto> taskDtoList = taskMapper.selectList(taskWrapper);
        task.setDestination(targetField);
        taskMapper.update(task, taskWrapper);
        //查找故障场地当前班次还没有生成任务的子计划对应的主计划,更新目的地
        QueryWrapper<Plan> planWrapper = new QueryWrapper<>();
        planWrapper.eq("SCHEDULE_DATE", split[0]);
        planWrapper.eq("SHIFT_TYPE", split[1]);
        planWrapper.eq("DESTINATION", currentField);
        Plan plan = new Plan();
        plan.setDestination(targetField);
        planMapper.update(plan, planWrapper);//直接更换计划目的地
        //通知前往故障场地的大车前往新的目的地
        List<String> cartToNotify = taskDtoList.stream()
                .filter(t -> StringUtils.isNotBlank(t.getReceiver())).map(PlanTaskDto::getReceiver)
                .collect(Collectors.toList());
        String FieldName = equipmentMainService.translateFromEquipNo2Name(targetField);
        MessageDto msg = new MessageDto();
        msg.setContent("您的最终目的地发生变化,装车完毕后请前往" + FieldName);
        msg.setEquipmentType(EquipmentType.Cart.getType());
        msg.setSender(currentField);
        msg.setFkTaskId(0);
        for (String cartNo : cartToNotify) {
            msg.setReceiver(cartNo);
            jPushService.sendMsg(msg);
        }
        //通知目标场地有新的任务
        msg.setSender(currentField);
        msg.setReceiver(targetField);
        msg.setContent("您有新的任务,请准备开始工作");
        msg.setEquipmentType(EquipmentType.Field.getType());
        msg.setFkTaskId(0);
        jPushService.sendMsg(msg);
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto processShovelBreakDown(EquipmentBreakDownDto breakDownDto) {
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        String targetShovel = breakDownDto.getEquipmentNo();//前台借用这个字段,传入的是要替换的电铲,替换当前电铲的电铲号
        EquipmentBreakDownDto breakDownData = breakDownMapper.selectById(breakDownDto.getId());
        breakDownData.setFaultState(FaultState.Processing.getState());
        breakDownMapper.updateById(breakDownData);
        String currentShovel = breakDownData.getEquipmentNo();//被替换的电铲号
        //检查故障电铲目前是否仍处于故障状态,如果不在故障状态取消这次处理
        QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
        equipmentWrapper.eq("EQUIPMENT_NO", currentShovel);
        equipmentWrapper.eq("EQUIPMENT_STATUS", EquipmentStatus.BreakDown.getStatus());
        EquipmentMain breakDownShovel = equipmentMapper.selectOne(equipmentWrapper);
        if (breakDownShovel == null) {
            return ResponseResultDto.ServiceError("当前电铲故障状态已恢复,不需要处理");
        }
        //记录替换历史
        ProcessBreakHistoryDto breakHistoryDto = new ProcessBreakHistoryDto();
        breakHistoryDto.setEquipmentDown(currentShovel);
        breakHistoryDto.setEquipmentUp(targetShovel);
        breakHistoryDto.setScheduleDate(split[0]);
        breakHistoryDto.setShiftType(split[1]);
        breakHistoryDto.setBreakDownId(breakDownDto.getId());
        breakHistoryMapper.insert(breakHistoryDto);
        //找到当前班次所有计划
        List<Plan> plans = planTaskService.getCurrentSwitchPlans();
        QueryWrapper<PlanDetail> detailWrapper = new QueryWrapper<>();
        for (Plan plan : plans) {
            //找到所有子计划,未生成任务的子计划设备编码改为新的电铲号
            Integer planId = plan.getId();
            detailWrapper.clear();
            detailWrapper.eq("PLAN_ID", planId);
            detailWrapper.eq("EQUIPMENT_NO", currentShovel);
            List<PlanDetail> details = detailMapper.selectList(detailWrapper);
            if (details.size() == 0) {
                continue;
            }
            for (PlanDetail detail : details) {
                Integer isUse = detail.getIsUse();
                if (isUse == 0) {
                    detail.setEquipmentNo(targetShovel);
                    detailMapper.updateById(detail);
                }
            }
            QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
            taskWrapper.eq("SCHEDULE_DATE", split[0]);//当前日期
            taskWrapper.eq("SHIFT_TYPE", split[1]);//当前班次
            taskWrapper.lt("TASK_STATE", TaskState.LeaveShovel.getState());//离开电铲之前的任务
            taskWrapper.eq("PUBLISHER", currentShovel);//发布者是当前的故障电铲
            List<PlanTaskDto> taskDtoList = taskMapper.selectList(taskWrapper);
            if (taskDtoList.size() > 0) {
                //拿到所有的子计划id
                List<Integer> detailIdList = taskDtoList.stream()
                        .map(PlanTaskDto::getFkPlanDetail)
                        .distinct()
                        .collect(Collectors.toList());
                detailWrapper.clear();
                detailWrapper.in("ID", detailIdList);
                PlanDetail detail = new PlanDetail();
                detail.setEquipmentNo(targetShovel);
                detailMapper.update(detail, detailWrapper);//更新未完成部分的子计划中的电铲编码
                PlanTaskDto task = new PlanTaskDto();
                task.setPublisher(targetShovel);
                taskMapper.update(task, taskWrapper);//更新未完成部分的任务信息
                //将任务列表的推送者改到新电铲,保证翻译的正确
                taskDtoList.forEach(t -> t.setPublisher(targetShovel));
                taskDtoList = taskDtoList.stream().filter(t -> StringUtils.isNotBlank(t.getReceiver())).collect(Collectors.toList());
                List<PlanTaskVo> planTaskVoList = planTaskService.cartTaskDto2TaskVo(taskDtoList, "任务变更");
                for (PlanTaskVo planTaskVo : planTaskVoList) {
                    String cartNo = planTaskVo.getReceiver();
                    MessageDto msg = new MessageDto();
                    msg.setSender(targetShovel);
                    msg.setReceiver(cartNo);
                    msg.setContent(planTaskVo.getTaskText());
                    msg.setEquipmentType(EquipmentType.Cart.getType());
                    msg.setFkTaskId(planTaskVo.getId());
                    jPushService.sendMsg(msg);
                }
            } else {
                continue;
            }
            MessageDto shovelMsg = new MessageDto();
            shovelMsg.setSender(currentShovel);
            shovelMsg.setReceiver(targetShovel);
            shovelMsg.setContent("您有新的任务,请准备开始工作");
            shovelMsg.setEquipmentType(EquipmentType.Shovel.getType());
            shovelMsg.setFkTaskId(0);
            jPushService.sendMsg(shovelMsg);
        }
        QueryWrapper<CartWorkTarget> cartWorkTargetQueryWrapper = new QueryWrapper<>();
        cartWorkTargetQueryWrapper.eq("SHOVEL_NO", currentShovel);
        cartWorkTargetQueryWrapper.eq("SCHEDULE_DATE", split[0]);
        cartWorkTargetQueryWrapper.eq("SHIFT_TYPE", split[1]);
        CartWorkTarget cartWorkTarget = new CartWorkTarget();
        cartWorkTarget.setShovelNo(targetShovel);
        cartWorkTargetMapper.update(cartWorkTarget, cartWorkTargetQueryWrapper);
        return ResponseResultDto.ok();
    }
}
