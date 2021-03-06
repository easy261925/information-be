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
            breakDownVo.setEquipmentName(equipmentName);//????????????
            String title = equipmentName;
            String faultDetailType = breakDownDto.getFaultDetailType();
            if (EquipmentType.Cart.getType().equals(equipmentType)) {
                breakDownVo.setEquipmentTypeName("??????");//????????????
                String detailType = equipmentMain.getDetailType();
                String detailTypeName = sysDictService.queryDict("carts_type", detailType);
                breakDownVo.setEquipmentDetailTypeName(detailTypeName);//???????????????
                String faultDetailTypeName = sysDictService.queryDict("cart_fault_detail_types", faultDetailType);
                breakDownVo.setFaultDetailName(faultDetailTypeName);
            } else if (EquipmentType.Shovel.getType().equals(equipmentType)) {
                String oreField = equipmentMain.getOreField();//????????????
                String fieldName = sysDictService.queryDict("ore_field", oreField);//????????????
                breakDownVo.setOreFieldName(fieldName);//????????????
                title = fieldName + equipmentName;
                breakDownVo.setEquipmentTypeName("??????");//????????????
                String detailType = equipmentMain.getDetailType();
                String detailTypeName = sysDictService.queryDict("shovel_type", detailType);
                breakDownVo.setEquipmentDetailTypeName(detailTypeName);//???????????????
                String faultDetailTypeName = sysDictService.queryDict("shovel_fault_detail_types", faultDetailType);
                breakDownVo.setFaultDetailName(faultDetailTypeName);
            } else {
                breakDownVo.setEquipmentTypeName("??????");//????????????
                String faultDetailTypeName = sysDictService.queryDict("site_fault_detail_types", faultDetailType);
                breakDownVo.setFaultDetailName(faultDetailTypeName);
            }
            SysUserDto user = userMapper.selectById(breakDownDto.getHandlerId());
            breakDownVo.setHandlerName(user.getUserName());//?????????
            breakDownVo.setHandlerPhone(user.getPhone());//???????????????
            title += "????????????";
            breakDownVo.setTitle(title);//????????????
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
        String targetCart = breakDownDto.getEquipmentNo();//????????????????????????,??????????????????????????????
        EquipmentBreakDownDto breakDownData = breakDownMapper.selectById(breakDownDto.getId());
        breakDownData.setFaultState(FaultState.Processing.getState());
        breakDownMapper.updateById(breakDownData);
        String currentCart = breakDownData.getEquipmentNo();//????????????????????????
        //???????????????????????????????????????????????????,??????????????????????????????????????????
        QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
        equipmentWrapper.eq("EQUIPMENT_NO", currentCart);
        equipmentWrapper.eq("EQUIPMENT_STATUS", EquipmentStatus.BreakDown.getStatus());
        EquipmentMain breakDownCart = equipmentMapper.selectOne(equipmentWrapper);
        if (breakDownCart == null) {
            return ResponseResultDto.ServiceError("?????????????????????????????????,???????????????");
        }
        //??????????????????
        ProcessBreakHistoryDto breakHistoryDto = new ProcessBreakHistoryDto();
        breakHistoryDto.setEquipmentDown(currentCart);
        breakHistoryDto.setEquipmentUp(targetCart);
        breakHistoryDto.setScheduleDate(split[0]);
        breakHistoryDto.setShiftType(split[1]);
        breakHistoryDto.setBreakDownId(breakDownDto.getId());
        breakHistoryMapper.insert(breakHistoryDto);
        //??????????????????????????????????????????????????????
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.eq("RECEIVER", targetCart);
        taskWrapper.lt("TASK_STATE", TaskState.LeaveField.getState());
        taskWrapper.eq("SCHEDULE_DATE", split[0]);
        taskWrapper.eq("SHIFT_TYPE", split[1]);
        int count = taskMapper.selectCount(taskWrapper);
        if (count > 0) {
            return ResponseResultDto.ServiceError("?????????????????????????????????,?????????????????????");
        }
        //??????????????????????????????????????????????????????
        QueryWrapper<TaskToDoDto> toDoWrapper = new QueryWrapper<>();
        toDoWrapper.eq("EXECUTOR", targetCart);
        toDoWrapper.in("COMPLETE_STATE", TaskState.ToDoTaskCreate.getState(), TaskState.ToDoTaskProcessing.getState());
        count = toDoMapper.selectCount(toDoWrapper);
        if (count > 0) {
            return ResponseResultDto.ServiceError("????????????????????????????????????,?????????????????????");
        }
        equipmentWrapper.clear();
        equipmentWrapper.eq("EQUIPMENT_NO", targetCart);
        EquipmentMain equipmentMain = equipmentMapper.selectOne(equipmentWrapper);
        String handlerId = equipmentMain.getHandlerId();//????????????????????????
        //???????????????????????????????????????????????????
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
            String shovelNo = taskDto.getPublisher();//???????????????????????????
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
            String taskState = taskDto.getTaskState();//????????????
            if (TaskState.LeaveShovel.getState().compareTo(taskState) > 0) {
                String currentCartName = equipmentMainService.translateFromEquipNo2Name(currentCart);
                String targetCartName = equipmentMainService.translateFromEquipNo2Name(targetCart);
                //?????????????????????????????????,??????????????????
                MessageDto shovelMsg = new MessageDto();
                shovelMsg.setSender(currentCart);
                shovelMsg.setReceiver(shovelNo);
                shovelMsg.setContent(currentCartName + "????????????" + targetCartName + ",?????????????????????");
                shovelMsg.setEquipmentType(EquipmentType.Shovel.getType());
                shovelMsg.setFkTaskId(0);
                jPushService.sendMsg(shovelMsg);
            } else if (TaskState.LeaveField.getState().compareTo(taskState) > 0) {
                String currentCartName = equipmentMainService.translateFromEquipNo2Name(currentCart);
                String targetCartName = equipmentMainService.translateFromEquipNo2Name(targetCart);
                //?????????????????????????????????,??????????????????
                MessageDto fieldMsg = new MessageDto();
                fieldMsg.setSender(currentCart);
                fieldMsg.setReceiver(taskDto.getDestination());
                fieldMsg.setContent(currentCartName + "????????????" + targetCartName + ",?????????????????????");
                fieldMsg.setEquipmentType(EquipmentType.Field.getType());
                fieldMsg.setFkTaskId(0);
                jPushService.sendMsg(fieldMsg);
            }

        }
        //?????????????????????????????????????????????,??????????????????????????????????????????
        toDoWrapper.clear();
        toDoWrapper.eq("EXECUTOR", currentCart);
        toDoWrapper.in("COMPLETE_STATE", TaskState.ToDoTaskCreate.getState(), TaskState.ToDoTaskProcessing.getState());
        List<TaskToDoDto> toDoDtoList = toDoMapper.selectList(toDoWrapper);
        for (TaskToDoDto toDoDto : toDoDtoList) {
            toDoDto.setExecutor(targetCart);
            toDoMapper.updateById(toDoDto);
        }
        if (toDoDtoList.size() == 0 && taskDto == null) {
            //?????????????????????????????????????????????????????????
            QueryWrapper<CartWorkTarget> cartWorkTargetQueryWrapper = new QueryWrapper<>();
            cartWorkTargetQueryWrapper.eq("CART_NO", currentCart);
            cartWorkTargetQueryWrapper.eq("SCHEDULE_DATE", split[0]);
            cartWorkTargetQueryWrapper.eq("SHIFT_TYPE", split[1]);
            CartWorkTarget currentCartWorkTarget = cartWorkTargetMapper.selectOne(cartWorkTargetQueryWrapper);
            if (currentCartWorkTarget != null) {
                String shovelNo = currentCartWorkTarget.getShovelNo();//???????????????????????????
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
        //??????????????????????????????
        MessageDto cartMsg = new MessageDto();
        cartMsg.setSender(currentCart);
        cartMsg.setReceiver(targetCart);
        cartMsg.setContent("??????????????????,?????????????????????");
        cartMsg.setEquipmentType(EquipmentType.Cart.getType());
        cartMsg.setFkTaskId(0);
        jPushService.sendMsg(cartMsg);

        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto processFieldBreakDown(EquipmentBreakDownDto breakDownDto) {
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        String targetField = breakDownDto.getEquipmentNo();//????????????????????????,??????????????????????????????
        EquipmentBreakDownDto breakDownData = breakDownMapper.selectById(breakDownDto.getId());
        breakDownData.setFaultState(FaultState.Processing.getState());
        breakDownMapper.updateById(breakDownData);
        String currentField = breakDownData.getEquipmentNo();//????????????????????????
        //???????????????????????????????????????????????????,??????????????????????????????????????????
        QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
        equipmentWrapper.eq("EQUIPMENT_NO", currentField);
        equipmentWrapper.eq("EQUIPMENT_STATUS", EquipmentStatus.BreakDown.getStatus());
        EquipmentMain breakDownField = equipmentMapper.selectOne(equipmentWrapper);
        if (breakDownField == null) {
            return ResponseResultDto.ServiceError("?????????????????????????????????,???????????????");
        }
        //??????????????????
        ProcessBreakHistoryDto breakHistoryDto = new ProcessBreakHistoryDto();
        breakHistoryDto.setEquipmentDown(currentField);
        breakHistoryDto.setEquipmentUp(targetField);
        breakHistoryDto.setScheduleDate(split[0]);
        breakHistoryDto.setShiftType(split[1]);
        breakHistoryDto.setBreakDownId(breakDownDto.getId());
        breakHistoryMapper.insert(breakHistoryDto);
        //??????????????????,???????????????????????????,?????????????????????
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.eq("DESTINATION", currentField);
        taskWrapper.lt("TASK_STATE", TaskState.LeaveField.getState());
        taskWrapper.eq("SCHEDULE_DATE", split[0]);
        taskWrapper.eq("SHIFT_TYPE", split[1]);
        PlanTaskDto task = new PlanTaskDto();
        List<PlanTaskDto> taskDtoList = taskMapper.selectList(taskWrapper);
        task.setDestination(targetField);
        taskMapper.update(task, taskWrapper);
        //?????????????????????????????????????????????????????????????????????????????????,???????????????
        QueryWrapper<Plan> planWrapper = new QueryWrapper<>();
        planWrapper.eq("SCHEDULE_DATE", split[0]);
        planWrapper.eq("SHIFT_TYPE", split[1]);
        planWrapper.eq("DESTINATION", currentField);
        Plan plan = new Plan();
        plan.setDestination(targetField);
        planMapper.update(plan, planWrapper);//???????????????????????????
        //??????????????????????????????????????????????????????
        List<String> cartToNotify = taskDtoList.stream()
                .filter(t -> StringUtils.isNotBlank(t.getReceiver())).map(PlanTaskDto::getReceiver)
                .collect(Collectors.toList());
        String FieldName = equipmentMainService.translateFromEquipNo2Name(targetField);
        MessageDto msg = new MessageDto();
        msg.setContent("?????????????????????????????????,????????????????????????" + FieldName);
        msg.setEquipmentType(EquipmentType.Cart.getType());
        msg.setSender(currentField);
        msg.setFkTaskId(0);
        for (String cartNo : cartToNotify) {
            msg.setReceiver(cartNo);
            jPushService.sendMsg(msg);
        }
        //?????????????????????????????????
        msg.setSender(currentField);
        msg.setReceiver(targetField);
        msg.setContent("??????????????????,?????????????????????");
        msg.setEquipmentType(EquipmentType.Field.getType());
        msg.setFkTaskId(0);
        jPushService.sendMsg(msg);
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto processShovelBreakDown(EquipmentBreakDownDto breakDownDto) {
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        String targetShovel = breakDownDto.getEquipmentNo();//????????????????????????,??????????????????????????????,??????????????????????????????
        EquipmentBreakDownDto breakDownData = breakDownMapper.selectById(breakDownDto.getId());
        breakDownData.setFaultState(FaultState.Processing.getState());
        breakDownMapper.updateById(breakDownData);
        String currentShovel = breakDownData.getEquipmentNo();//?????????????????????
        //???????????????????????????????????????????????????,??????????????????????????????????????????
        QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
        equipmentWrapper.eq("EQUIPMENT_NO", currentShovel);
        equipmentWrapper.eq("EQUIPMENT_STATUS", EquipmentStatus.BreakDown.getStatus());
        EquipmentMain breakDownShovel = equipmentMapper.selectOne(equipmentWrapper);
        if (breakDownShovel == null) {
            return ResponseResultDto.ServiceError("?????????????????????????????????,???????????????");
        }
        //??????????????????
        ProcessBreakHistoryDto breakHistoryDto = new ProcessBreakHistoryDto();
        breakHistoryDto.setEquipmentDown(currentShovel);
        breakHistoryDto.setEquipmentUp(targetShovel);
        breakHistoryDto.setScheduleDate(split[0]);
        breakHistoryDto.setShiftType(split[1]);
        breakHistoryDto.setBreakDownId(breakDownDto.getId());
        breakHistoryMapper.insert(breakHistoryDto);
        //??????????????????????????????
        List<Plan> plans = planTaskService.getCurrentSwitchPlans();
        QueryWrapper<PlanDetail> detailWrapper = new QueryWrapper<>();
        for (Plan plan : plans) {
            //?????????????????????,????????????????????????????????????????????????????????????
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
            taskWrapper.eq("SCHEDULE_DATE", split[0]);//????????????
            taskWrapper.eq("SHIFT_TYPE", split[1]);//????????????
            taskWrapper.lt("TASK_STATE", TaskState.LeaveShovel.getState());//???????????????????????????
            taskWrapper.eq("PUBLISHER", currentShovel);//?????????????????????????????????
            List<PlanTaskDto> taskDtoList = taskMapper.selectList(taskWrapper);
            if (taskDtoList.size() > 0) {
                //????????????????????????id
                List<Integer> detailIdList = taskDtoList.stream()
                        .map(PlanTaskDto::getFkPlanDetail)
                        .distinct()
                        .collect(Collectors.toList());
                detailWrapper.clear();
                detailWrapper.in("ID", detailIdList);
                PlanDetail detail = new PlanDetail();
                detail.setEquipmentNo(targetShovel);
                detailMapper.update(detail, detailWrapper);//???????????????????????????????????????????????????
                PlanTaskDto task = new PlanTaskDto();
                task.setPublisher(targetShovel);
                taskMapper.update(task, taskWrapper);//????????????????????????????????????
                //??????????????????????????????????????????,?????????????????????
                taskDtoList.forEach(t -> t.setPublisher(targetShovel));
                taskDtoList = taskDtoList.stream().filter(t -> StringUtils.isNotBlank(t.getReceiver())).collect(Collectors.toList());
                List<PlanTaskVo> planTaskVoList = planTaskService.cartTaskDto2TaskVo(taskDtoList, "????????????");
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
            shovelMsg.setContent("??????????????????,?????????????????????");
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
