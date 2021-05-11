package com.th.workbase.service.plan.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.equipment.CurrentPositionDto;
import com.th.workbase.bean.equipment.EquipmentMain;
import com.th.workbase.bean.equipment.MessageDto;
import com.th.workbase.bean.plan.*;
import com.th.workbase.bean.system.LoadDistanceDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysConfig;
import com.th.workbase.common.commonEnum.*;
import com.th.workbase.common.utils.CalculateDistanceUtil;
import com.th.workbase.mapper.equipment.CurrentPositionMapper;
import com.th.workbase.mapper.equipment.EquipmentMainMapper;
import com.th.workbase.mapper.equipment.MessageMapper;
import com.th.workbase.mapper.plan.*;
import com.th.workbase.mapper.system.LoadDistanceMapper;
import com.th.workbase.mapper.system.SysConfigMapper;
import com.th.workbase.service.common.DefineSwitchService;
import com.th.workbase.service.common.JPushService;
import com.th.workbase.service.equipment.EquipmentMainService;
import com.th.workbase.service.plan.PlanTaskService;
import com.th.workbase.service.system.SysDictService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

/**
 * @author tangj
 * @since 2021-03-02
 */
@Service
public class PlanTaskServiceImpl extends ServiceImpl<PlanTaskMapper, PlanTaskDto> implements PlanTaskService {
    @Resource
    PlanTaskMapper taskMapper;
    @Resource
    PlanDetailMapper planDetailMapper;
    @Resource
    PlanMapper planMapper;
    @Resource
    EquipmentMainMapper equipmentMapper;
    @Resource
    SysConfigMapper configMapper;
    @Resource
    MessageMapper messageMapper;
    @Autowired
    EquipmentMainService equipmentMainService;
    @Autowired
    JPushService jPushService;
    @Autowired
    DefineSwitchService defineSwitchService;
    @Resource
    CurrentPositionMapper currentPositionMapper;
    @Autowired
    PlanTaskService planTaskService;
    @Resource
    TaskToDoMapper taskToDoMapper;
    @Resource
    PlanTaskHistoryMapper taskHistoryMapper;
    @Resource
    LoadDistanceMapper distanceMapper;
    @Resource
    CartWorkTargetMapper cartWorkTargetMapper;
    @Autowired
    SysDictService sysDictService;

    @Override
    public ResponseResultDto generateTaskByPlan(String equipmentNo) {
        //获取当前配置信息,确认当前找车的模式
        SysConfig sysConfig = configMapper.selectOne(null);
        String pushMode = sysConfig.getPushMode();
        boolean trigger = "2".equals(pushMode);//是否是模式2
        List<Plan> planList = getCurrentSwitchPlans();
        if (planList != null && planList.size() > 0) {
            for (Plan plan : planList) {
                //依据主表id找到对应的计划明细表数据,找到当前电铲车对应的当日计划明细内容
                QueryWrapper<PlanDetail> planDetailWrapper = new QueryWrapper<>();
                planDetailWrapper.eq("plan_id", plan.getId());
                planDetailWrapper.eq("equipment_no", equipmentNo);
                planDetailWrapper.eq("is_use", "0");
                List<PlanDetail> details = planDetailMapper.selectList(planDetailWrapper);
                if (details != null && details.size() > 0) {
                    //遍历明细表数据,将明细表内容转化为任务表内容
                    String destination = plan.getDestination();
                    for (PlanDetail detail : details) {
                        PlanTaskDto task = new PlanTaskDto();
                        task.setFkPlanDetail(detail.getId());//设置计划明细表外键
                        task.setDestination(destination);//设置任务目的地
                        task.setPublisher(equipmentNo);//设置任务发布者为当前电铲车编码
                        task.setTaskState(TaskState.Create.getState());//设置任务状态为创建状态
                        task.setScheduleDate(plan.getScheduleDate());//设置任务日期
                        task.setShiftType(plan.getShiftType());//设置任务班次
                        task.setPriorityTrigger(trigger);
                        task.setCategory(detail.getCategory());
                        //设置运输距离
                        QueryWrapper<LoadDistanceDto> distanceWrapper = new QueryWrapper<>();
                        distanceWrapper.eq("SHOVEL_NO", equipmentNo);
                        distanceWrapper.eq("FIELD_NO", destination);
                        LoadDistanceDto distanceDto = distanceMapper.selectOne(distanceWrapper);
                        if (distanceDto != null) {
                            task.setDistance(distanceDto.getDistance());
                        }
                        PlanTaskHistoryDto taskHistory = new PlanTaskHistoryDto();
                        copyProperties(task, taskHistory);
                        for (int i = 0; i < detail.getCartsCount(); i++) {
                            taskMapper.insert(task);
                            taskHistory.setTaskId(task.getId());
                            taskHistoryMapper.insert(taskHistory);
                        }
                        detail.setIsUse(1);
                        planDetailMapper.updateById(detail);
                    }
                }
            }
        } else {
            return ResponseResultDto.ServiceError("计划不存在");
        }
        return ResponseResultDto.ok();
    }

    @Override
    public List<PlanTaskDto> getTaskByPublisher(String equipmentNo) {
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.eq("PUBLISHER", equipmentNo);
        taskWrapper.eq("SCHEDULE_DATE", split[0]);
        taskWrapper.eq("SHIFT_TYPE", split[1]);
        taskWrapper.in("TASK_STATE", TaskState.Create.getState()
                , TaskState.Publish.getState());
        return taskMapper.selectList(taskWrapper);
    }

    @Override
    public List<String> getDeviceNearBy(String equipmentNo, String destination, boolean enablePriority) {
        //根据equipmentNo取得当前电铲对应的经纬度
        QueryWrapper<CurrentPositionDto> currentPositionWrapper = new QueryWrapper<>();
        currentPositionWrapper.eq("EQUIPMENT_NO", equipmentNo);
        CurrentPositionDto currentEquipment = currentPositionMapper.selectOne(currentPositionWrapper);
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        List<EquipmentMain> equipmentMains;
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        List<PlanTaskDto> taskDtoList;
        if (enablePriority) {
            //找到电铲对应的矿坑
            QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
            equipmentWrapper.eq("EQUIPMENT_NO", equipmentNo);
            EquipmentMain equipmentMain = equipmentMapper.selectOne(equipmentWrapper);
            String oreField = equipmentMain.getOreField();//矿区编码
            //在任务表中找,今天有没有为这台电铲服务过的车辆
            taskWrapper.select("RECEIVER");
            taskWrapper.eq("SCHEDULE_DATE", split[0]);//当前日期
            taskWrapper.eq("SHIFT_TYPE", split[1]);//当前班次
            taskWrapper.eq("PUBLISHER", equipmentNo);//为当前电铲服务过
            taskWrapper.eq("TASK_STATE", TaskState.LeaveField.getState());//当前班次完成过任务,缩小范围,不会找到正在为此电铲工作的车辆,减少后续循环次数
            taskDtoList = taskMapper.selectList(taskWrapper);
            if (taskDtoList != null && taskDtoList.size() > 0) {
                //存在为当前电铲服务过的空闲车辆,任务只发给这个列表中的车辆,和距离等因素无关
                List<String> collect = taskDtoList.stream().map(PlanTaskDto::getReceiver).distinct().collect(Collectors.toList());
                collect = excludeCartUnavailable(collect);//排除正在工作中的车辆
                if (collect != null && collect.size() > 0) {
                    return collect;
                }
            }
            List<String> equipNoList;//设备编码的临时列表
            //不存在为当前电铲服务过的车辆,按区域找车
            if ("K1".equals(oreField)) {
                //新区只能找沃尔沃
                //找到所有工作中没有任务的沃尔沃
                equipmentWrapper.clear();
                equipmentWrapper.select("EQUIPMENT_NO");
                equipmentWrapper.eq("EQUIPMENT_STATUS", EquipmentStatus.Working.getStatus());//工作状态
                equipmentWrapper.eq("EQUIPMENT_TYPE", EquipmentType.Cart.getType());//大车类型
                equipmentWrapper.eq("DETAIL_TYPE", CartDetailType.Volvo.getType());//沃尔沃
                equipmentMains = equipmentMapper.selectList(equipmentWrapper);
                if (equipmentMains == null || equipmentMains.size() == 0) {
                    //没有在工作状态的沃尔沃,返回空
                    return null;
                }
                //在工作的沃尔沃设备编码列表
                equipNoList = equipmentMains.stream().map(EquipmentMain::getEquipmentNo).distinct().collect(Collectors.toList());
                //排除正在工作的车辆
                return excludeCartUnavailable(equipNoList);
            } else if ("BP".equals(destination)) {
                //如果目的地是北破,则优先找卡特,卡特找不到找沃尔沃
                //找工作中的卡特车
                equipmentWrapper.clear();
                equipmentWrapper.select("EQUIPMENT_NO");
                equipmentWrapper.eq("EQUIPMENT_STATUS", EquipmentStatus.Working.getStatus());//工作状态
                equipmentWrapper.eq("EQUIPMENT_TYPE", EquipmentType.Cart.getType());//大车类型
                equipmentWrapper.eq("DETAIL_TYPE", CartDetailType.Cat.getType());//卡特类型
                equipmentMains = equipmentMapper.selectList(equipmentWrapper);
                return findVolvoAsSubstitute(equipmentMains);
            } else {
                //除了新区默认找其他车辆,找不到其他车,沃尔沃在工作状态但是没有任务,就找沃尔沃
                //找到所有工作中没有任务的非沃尔沃
                equipmentWrapper.clear();
                equipmentWrapper.select("EQUIPMENT_NO");
                equipmentWrapper.eq("EQUIPMENT_STATUS", EquipmentStatus.Working.getStatus());//工作状态
                equipmentWrapper.eq("EQUIPMENT_TYPE", EquipmentType.Cart.getType());//大车类型
                equipmentWrapper.ne("DETAIL_TYPE", CartDetailType.Volvo.getType());//非沃尔沃
                equipmentMains = equipmentMapper.selectList(equipmentWrapper);
                return findVolvoAsSubstitute(equipmentMains);
            }
        }
        //查找全范围车辆
        return getCartsFromAllType(currentEquipment);
    }

    @Override
    public ResponseResultDto getCartTasks(String equipmentNo) {
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        //取得当前任务
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.eq("RECEIVER", equipmentNo);
        taskWrapper.lt("TASK_STATE", TaskState.LeaveField.getState());
        taskWrapper.gt("TASK_STATE", TaskState.Create.getState());
        taskWrapper.eq("SCHEDULE_DATE", split[0]);
        taskWrapper.eq("SHIFT_TYPE", split[1]);

        List<PlanTaskDto> taskList = taskMapper.selectList(taskWrapper);
        List<PlanTaskVo> planTaskVoList = cartTaskDto2TaskVo(taskList, "当前任务:");
        if (planTaskVoList.size() != 0) {
            return ResponseResultDto.ok().data("data", planTaskVoList);
        }
        //取得已创建和在执行临时任务
        QueryWrapper<TaskToDoDto> toDoWrapper = new QueryWrapper<>();
        toDoWrapper.eq("EXECUTOR", equipmentNo);
        toDoWrapper.ne("COMPLETE_STATE", TaskState.ToDoTaskCompleted.getState());
        toDoWrapper.orderByDesc("DT_CREA_DATE_TIME");
        List<TaskToDoDto> toDoDtoList = taskToDoMapper.selectList(toDoWrapper);
        if (toDoDtoList.size() > 0) {
            for (TaskToDoDto taskToDoDto : toDoDtoList) {
                String taskType = taskToDoDto.getTaskType();
                if (TaskType.Temp.getType().equals(taskType)) {
                    //指派的临时任务
                    PlanTaskVo taskVo = new PlanTaskVo();
                    taskVo.setTaskText("临时任务:" + taskToDoDto.getRemark());
                    taskVo.setTaskState(taskToDoDto.getCompleteState());
                    taskVo.setTaskType(TaskType.Temp.getType());
                    taskVo.setToDoId(taskToDoDto.getId());
                    planTaskVoList.add(taskVo);
                    return ResponseResultDto.ok().data("data", planTaskVoList);
                } else {
                    //指派的普通任务
                    //找到指定电铲对应的普通任务
                    String shovelNo = taskToDoDto.getTarget();
                    //找到当前班次当前电铲对应的任务
                    taskWrapper.clear();
                    taskWrapper.eq("SCHEDULE_DATE", split[0]);
                    taskWrapper.eq("SHIFT_TYPE", split[1]);
                    taskWrapper.eq("PUBLISHER", shovelNo);
                    taskWrapper.isNotNull("TO_DO_ID");
                    taskWrapper.isNull("RECEIVER");
                    List<PlanTaskDto> taskDtoList = taskMapper.selectList(taskWrapper);
                    if (taskDtoList.size() > 0) {
                        for (PlanTaskDto taskDto : taskDtoList) {
                            Integer taskId = taskDto.getId();
                            //若车辆接收过此任务的消息,则显示该任务
                            QueryWrapper<MessageDto> msgWrapper = new QueryWrapper<>();
                            msgWrapper.eq("FK_TASK_ID", taskId);
                            msgWrapper.eq("IS_USE", 0);
                            msgWrapper.eq("RECEIVER", equipmentNo);
                            int count = messageMapper.selectCount(msgWrapper);
                            if (count > 0) {
                                PlanTaskVo taskVo = new PlanTaskVo();
                                copyProperties(taskDto, taskVo);
                                taskVo.setTaskText("您有一个新的调度任务");
                                taskVo.setToDoId(taskToDoDto.getId());
                                taskVo.setTaskState(TaskState.Publish.getState());
                                taskVo.setTaskType(TaskType.Normal.getType());
                                planTaskVoList.add(taskVo);
                                return ResponseResultDto.ok().data("data", planTaskVoList);
                            }
                        }
                    }
                }
            }
        }
        //当前任务不存在,寻找未领取的任务
        taskWrapper.clear();
        taskWrapper.eq("SCHEDULE_DATE", split[0]);
        taskWrapper.eq("SHIFT_TYPE", split[1]);
        taskWrapper.isNull("RECEIVER");
        taskWrapper.eq("TASK_STATE", TaskState.Publish.getState());
        taskList = taskMapper.selectList(taskWrapper);//找到当前班次接收者为空的任务
        List<PlanTaskDto> result = new ArrayList<>();
        for (PlanTaskDto task : taskList) {
            Integer taskId = task.getId();
            QueryWrapper<MessageDto> msgWrapper = new QueryWrapper<>();
            msgWrapper.eq("FK_TASK_ID", taskId);
            msgWrapper.eq("IS_USE", 0);
            List<MessageDto> msgList = messageMapper.selectList(msgWrapper);
            //若车辆接收过此任务的消息,则显示该任务
            boolean match = msgList.stream().anyMatch(m -> m.getReceiver().equals(equipmentNo));
            if (match) {
                result.add(task);
            }
        }
        planTaskVoList = cartTaskDto2TaskVo(result, "您有新的任务");
        return ResponseResultDto.ok().data("data", planTaskVoList);
    }

    @Override
    public List<Plan> getCurrentSwitchPlans() {
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        //按照当前日期取得当前班次的计划主表
        QueryWrapper<Plan> planWrapper = new QueryWrapper<>();
        planWrapper.eq("SCHEDULE_DATE", split[0]);
        planWrapper.eq("SHIFT_TYPE", split[1]);
        return planMapper.selectList(planWrapper);
    }

    @Override
    public ResponseResultDto resendTask(String taskId) {
        //查询到对应的任务信息
        PlanTaskDto task = taskMapper.selectById(taskId);
        //将此任务之前对应的消息列表全部置为已读
        MessageDto msg = new MessageDto();
        msg.setIsUse(1);
        QueryWrapper<MessageDto> msgWrapper = new QueryWrapper<>();
        msgWrapper.eq("FK_TASK_ID", taskId);
        messageMapper.update(msg, msgWrapper);
        //找到附近可用的车辆,将任务发出去,因为任务是重新发送,不需要验证是否满足任务上限
        List<String> deviceNearBy = planTaskService.getDeviceNearBy(task.getPublisher(), task.getDestination(), task.getPriorityTrigger());
        if (deviceNearBy != null && deviceNearBy.size() > 0) {
            msg = new MessageDto();
            msg.setContent("您有新的任务");
            msg.setSender(task.getPublisher());
            msg.setType(MessageType.Business.getType());
            msg.setEquipmentType(EquipmentType.Cart.getType());
            for (String cartNo : deviceNearBy) {
                //检查当前车辆本班次最后服务过的电铲是否还有任务未被领取,如果有不给这个车发消息
                String defineSwitch = defineSwitchService.defineSwitch(new Date());
                String[] split = defineSwitch.split("_");

                QueryWrapper<CartWorkTarget> cartWorkTargetQueryWrapper = new QueryWrapper<>();
                cartWorkTargetQueryWrapper.eq("SCHEDULE_DATE", split[0]);
                cartWorkTargetQueryWrapper.eq("SHIFT_TYPE", split[1]);
                cartWorkTargetQueryWrapper.eq("CART_NO", cartNo);
                CartWorkTarget cartWorkTarget = cartWorkTargetMapper.selectOne(cartWorkTargetQueryWrapper);

                if (cartWorkTarget != null) {
                    String shovelNo = cartWorkTarget.getShovelNo();
                    //最后服务过的电铲和当前要推送任务对应的电铲如果是同一台电铲就可以发送消息,保证电铲可以催促车辆,也可以重新发送任务
                    if (!shovelNo.equals(task.getPublisher())) {
                        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
                        taskWrapper.eq("SCHEDULE_DATE", split[0]);
                        taskWrapper.eq("SHIFT_TYPE", split[1]);
                        taskWrapper.eq("PUBLISHER", shovelNo);
                        taskWrapper.isNull("RECEIVER");
                        List<PlanTaskDto> taskDtoList = taskMapper.selectList(taskWrapper);
                        if (taskDtoList.size() > 0) {
                            continue;
                        }
                    }
                }
                msg.setReceiver(cartNo);
                msg.setFkTaskId(task.getId());
                jPushService.sendMsg(msg);
            }
        }
        return ResponseResultDto.ok();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseResultDto acceptTask(PlanTaskDto taskDto) {
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        String cartNo = taskDto.getReceiver();//当前车号
        //如果待办主键存在,更新待办数据状态为已执行
        Integer toDoId = taskDto.getToDoId();
        if (toDoId != null) {
            TaskToDoDto toDoDto = new TaskToDoDto();
            toDoDto.setId(toDoId);
            if (TaskType.Temp.getType().equals(taskDto.getTaskType())) {
                //如果是临时任务,不再有后续操作,直接返回,如果是指派任务,处理方式同普通任务
                toDoDto.setCompleteState(TaskState.ToDoTaskProcessing.getState());
                taskToDoMapper.updateById(toDoDto);
                //将本车其余的未读消息删除
                QueryWrapper<MessageDto> msgWrapper = new QueryWrapper<>();
                msgWrapper.eq("IS_USE", 0);
                msgWrapper.eq("RECEIVER", cartNo);
                messageMapper.delete(msgWrapper);
                return ResponseResultDto.ok();
            }
            toDoDto.setCompleteState(TaskState.ToDoTaskCompleted.getState());
            taskToDoMapper.updateById(toDoDto);
        }
        //接受普通任务或指派任务
        Integer taskId = taskDto.getId();
        PlanTaskDto task = taskMapper.selectById(taskId);

        //确认大车工作的目标对象
        QueryWrapper<CartWorkTarget> targetWrapper = new QueryWrapper<>();
        targetWrapper.eq("SCHEDULE_DATE", split[0]);
        targetWrapper.eq("SHIFT_TYPE", split[1]);
        targetWrapper.eq("CART_NO", cartNo);
        CartWorkTarget cartWorkTarget = cartWorkTargetMapper.selectOne(targetWrapper);
        if (task == null) {
            QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
            if (cartWorkTarget != null) {
                String shovelNo = cartWorkTarget.getShovelNo();
                taskWrapper.eq("SCHEDULE_DATE", split[0]);
                taskWrapper.eq("SHIFT_TYPE", split[1]);
                taskWrapper.eq("PUBLISHER", shovelNo);
                taskWrapper.isNull("RECEIVER");
                List<PlanTaskDto> taskDtoList = taskMapper.selectList(taskWrapper);
                if (taskDtoList != null && taskDtoList.size() > 0) {
                    task = taskDtoList.get(0);
                }
            }else {
                taskWrapper.eq("SCHEDULE_DATE", split[0]);
                taskWrapper.eq("SHIFT_TYPE", split[1]);
                taskWrapper.isNull("RECEIVER");
                List<PlanTaskDto> taskDtoList = taskMapper.selectList(taskWrapper);
                if (taskDtoList != null && taskDtoList.size() > 0) {
                    task = taskDtoList.get(0);
                } else {
                    return ResponseResultDto.ServiceError("当前任务已经被其他人领取");
                }
            }
        }
        //在任务表按照任务id检查当前任务是否已有人接单
        if (StringUtils.isBlank(task.getReceiver())) {
            //当前任务未被领取,更新接收者为当前设备编码
            task.setReceiver(cartNo);
            task.setReceiveHandler(taskDto.getReceiveHandler());
            task.setTaskState(TaskState.Receive.getState());
            taskMapper.updateById(task);
            //记录任务历史过程
            PlanTaskHistoryDto taskHistory = new PlanTaskHistoryDto();
            copyProperties(task, taskHistory);
            taskHistory.setTaskId(task.getId());
            taskHistoryMapper.insert(taskHistory);
            MessageDto msg = new MessageDto();
            msg.setReceiver(task.getPublisher());
            msg.setEquipmentType(EquipmentType.Shovel.getType());
            String equipName = equipmentMainService.translateFromEquipNo2Name(cartNo);
            String content = equipName + "已经接受任务";
            msg.setContent(content);
            msg.setFkTaskId(task.getId());
            jPushService.sendMsg(msg);
            //更新大车对应的工作目标表
            if (cartWorkTarget != null) {
                cartWorkTarget.setShovelNo(task.getPublisher());
                cartWorkTargetMapper.updateById(cartWorkTarget);
            } else {
                cartWorkTarget = new CartWorkTarget();
                cartWorkTarget.setShovelNo(task.getPublisher());
                cartWorkTarget.setCartNo(cartNo);
                cartWorkTarget.setScheduleDate(split[0]);
                cartWorkTarget.setShiftType(split[1]);
                cartWorkTargetMapper.insert(cartWorkTarget);
            }
            QueryWrapper<MessageDto> msgWrapper = new QueryWrapper<>();
            msgWrapper.eq("IS_USE", 0);
            msgWrapper.eq("RECEIVER", cartNo);
            messageMapper.delete(msgWrapper);
            return ResponseResultDto.ok().data("msg", "任务领取成功");
        }
            /*
            如果A车辆今天已经为电铲一号工作过,在A车辆离开场地的时候,系统会自动给A车辆分配一个任务,
            若这个任务被其他车辆抢走了,就给这个车辆推送一个刚刚工作过的电铲的任务,若刚刚工作过的电铲已经没有了任务
            就找同一个矿区的其他电铲,逐渐扩大范围查找任务
             */
        if (cartWorkTarget != null) {
            String shovelNo = cartWorkTarget.getShovelNo();//存在最后工作过的电铲
            //找到此电铲对应的未被领取的任务
            boolean sendSuccess = getNotReceivedTaskPublishToCart(shovelNo, cartNo, null, null);
            if (sendSuccess) {
                return ResponseResultDto.ServiceError("当前任务已经被其他人领取");
            }
            //如果任务不存在,找这台电铲对应矿区的其余电铲进行遍历,看有没有同一个目的地的任务,如果有就发给这辆车
            //此处直接取未接到的任务的目的地,因为如果接到了任务就要前往这个目的地,所以不用找电铲对应的目的地
            String destination = task.getDestination();
            //找这台电铲对应的矿区
            QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
            equipmentWrapper.eq("EQUIPMENT_NO", shovelNo);
            EquipmentMain shovel = equipmentMapper.selectOne(equipmentWrapper);
            String oreField = shovel.getOreField();//取得电铲所在的矿区编码
            equipmentWrapper.clear();
            equipmentWrapper.select("EQUIPMENT_NO");
            equipmentWrapper.eq("ORE_FIELD", oreField);
            equipmentWrapper.eq("EQUIPMENT_STATUS", EquipmentStatus.Working.getStatus());
            List<EquipmentMain> equipmentList = equipmentMapper.selectList(equipmentWrapper);//找到当前矿区正在工作的电铲
            for (EquipmentMain equipmentMain : equipmentList) {
                //检查当前矿区有没有之前目的地的任务,有则优先发给这辆车
                shovelNo = equipmentMain.getEquipmentNo();
                sendSuccess = getNotReceivedTaskPublishToCart(shovelNo, cartNo, destination, null);
                if (sendSuccess) {
                    return ResponseResultDto.ServiceError("当前任务已经被其他人领取");
                }
            }
            for (EquipmentMain equipmentMain : equipmentList) {
                //没有之前目的地的任务,找当前矿区的车辆有没有其他矿破的任务
                shovelNo = equipmentMain.getEquipmentNo();
                sendSuccess = getNotReceivedTaskPublishToCart(shovelNo, cartNo, null, null);
                if (sendSuccess) {
                    return ResponseResultDto.ServiceError("当前任务已经被其他人领取");
                }
            }
            //以上条件都没找到,在当前班次任务列表中找到一个任务分配给当前车辆
            QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
            taskWrapper.eq("SCHEDULE_DATE", split[0]);
            taskWrapper.eq("SHIFT_TYPE", split[1]);
            taskWrapper.eq("ROWNUM", 1);
            taskWrapper.in("TASK_STATE", TaskState.Create.getState());
            taskDto = taskMapper.selectOne(taskWrapper);
            if (taskDto != null) {
                shovelNo = taskDto.getPublisher();
                getNotReceivedTaskPublishToCart(shovelNo, cartNo, null, null);
            }
        }
        //说明今天还没工作过,没领到任务就等待任务
        //将此消息置为已读
        QueryWrapper<MessageDto> msgWrapper = new QueryWrapper<>();
        msgWrapper.eq("FK_TASK_ID", taskId);
        msgWrapper.eq("RECEIVER", cartNo);
        MessageDto msg = new MessageDto();
        msg.setIsUse(1);
        messageMapper.update(msg, msgWrapper);
        return ResponseResultDto.ServiceError("当前任务已经被其他人领取");
    }

    @Override
    public ResponseResultDto supervise(String taskId) {
        PlanTaskDto taskDto = taskMapper.selectById(taskId);
        MessageDto msg = new MessageDto();
        msg.setReceiver(taskDto.getReceiver());
        String equipName = equipmentMainService.translateFromEquipNo2Name(taskDto.getPublisher());
        //翻译对应矿区名称
        QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
        equipmentWrapper.eq("EQUIPMENT_NO", taskDto.getPublisher());
        EquipmentMain equipmentMain = equipmentMapper.selectOne(equipmentWrapper);
        String oreField = equipmentMain.getOreField();//矿区编码
        String oreFieldName = sysDictService.queryDict("ore_field", oreField);
        msg.setContent("请尽快前往" + oreFieldName + equipName + "完成装车任务");
        msg.setEquipmentType(EquipmentType.Cart.getType());
        msg.setFkTaskId(taskDto.getId());
        jPushService.sendMsg(msg);
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto arriveShovel(String taskId) {
        PlanTaskDto task = taskMapper.selectById(taskId);//取得当前任务信息
        String shovelNo = task.getPublisher();
        String cartNo = task.getReceiver();//大车编码
        SysConfig sysConfig = configMapper.selectOne(null);//取得配置文件判断是否启用
        if (sysConfig.getEnableShovelDistance()) {
            //拿到当前电铲和大车的经纬度,进行距离比较
            QueryWrapper<CurrentPositionDto> positionWrapper = new QueryWrapper<>();
            positionWrapper.in("EQUIPMENT_NO", cartNo, shovelNo);
            List<CurrentPositionDto> positionDtoList = currentPositionMapper.selectList(positionWrapper);
            if (positionDtoList == null || positionDtoList.size() != 2) {
                return ResponseResultDto.ServiceError("位置信息异常");
            }
            double distance = CalculateDistanceUtil.getDistance(positionDtoList.get(0).getLat(),
                    positionDtoList.get(0).getLng(),
                    positionDtoList.get(1).getLat(),
                    positionDtoList.get(1).getLng()
            );
            if (distance > sysConfig.getShovelDetermineDistance()) {
                return ResponseResultDto.ServiceError("当前不在电铲附近,请靠近电铲具备装车条件后重试");
            }
        }
        //给电铲发送信息,通知电铲大车已经进场
        String cartName = equipmentMainService.translateFromEquipNo2Name(cartNo);
        MessageDto msg = new MessageDto();
        msg.setSender(cartNo);
        msg.setReceiver(shovelNo);
        msg.setContent(cartName + "已到达");
        msg.setEquipmentType(EquipmentType.Shovel.getType());
        msg.setFkTaskId(Integer.parseInt(taskId));
        jPushService.sendMsg(msg);
        //更改任务状态
        task.setTaskState(TaskState.ArriveShovel.getState());
        taskMapper.updateById(task);
        //记录任务历史过程
        PlanTaskHistoryDto taskHistory = new PlanTaskHistoryDto();
        copyProperties(task, taskHistory);
        taskHistory.setTaskId(task.getId());
        taskHistoryMapper.insert(taskHistory);
        return equipmentMainService.checkUnsaturatedDetailPlanAndSendTask();
    }

    @Override
    public ResponseResultDto forceCartArrive(String taskId) {
        //电铲强制大车进场,适用于GPS定位漂移,大车就在电铲附近的情况
        PlanTaskDto task = taskMapper.selectById(taskId);//取得当前任务信息
        String shovelNo = task.getPublisher();
        String cartNo = task.getReceiver();//大车编码
        //给大车发送信息,通知大车可以离开
        String shovelName = equipmentMainService.translateFromEquipNo2Name(shovelNo);
        MessageDto msg = new MessageDto();
        msg.setSender(shovelNo);
        msg.setReceiver(cartNo);
        msg.setContent(shovelName + "已确定您到达附近,请做好装车准备");
        msg.setEquipmentType(EquipmentType.Cart.getType());
        msg.setFkTaskId(Integer.parseInt(taskId));
        jPushService.sendMsg(msg);
        //更改任务状态
        task.setTaskState(TaskState.ArriveShovel.getState());
        taskMapper.updateById(task);
        //记录任务历史过程
        PlanTaskHistoryDto taskHistory = new PlanTaskHistoryDto();
        copyProperties(task, taskHistory);
        taskHistory.setTaskId(task.getId());
        taskHistoryMapper.insert(taskHistory);
        return ResponseResultDto.ok();
    }

    @Override
    @Transactional
    public ResponseResultDto loadComplete(PlanTaskDto taskDto) {
        String taskId = taskDto.getId().toString();
        String category = taskDto.getCategory();
        PlanTaskDto task = taskMapper.selectById(taskId);//取得当前任务信息
        String shovelNo = task.getPublisher();
        String cartNo = task.getReceiver();//大车编码
        //给大车发送信息,通知大车可以离开
        String cartName = equipmentMainService.translateFromEquipNo2Name(cartNo);
        MessageDto msg = new MessageDto();
        msg.setSender(shovelNo);
        msg.setReceiver(cartNo);
        msg.setContent(cartName + "装车完毕,可以离开");
        msg.setEquipmentType(EquipmentType.Cart.getType());
        if ("4".equals(category)) {
            //如果装的是岩石,新增一个任务,将当前任务的接受者置为null,将任务状态改为创建状态
            PlanTaskDto newTask = new PlanTaskDto();
            copyProperties(task, newTask);
            newTask.setId(null);
            newTask.setCategory(category);
            //更改任务状态
            newTask.setTaskState(TaskState.LoadComplete.getState());
            //将任务置为临时状态
            taskMapper.setTaskToTmp(taskId);
            //确定运送岩石场地位置
            QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
            equipmentWrapper.eq("EQUIPMENT_NO", "YP");
            EquipmentMain equipmentMain = equipmentMapper.selectOne(equipmentWrapper);
            if (!EquipmentStatus.BreakDown.getStatus().equals(equipmentMain.getEquipmentStatus())) {
                msg.setContent(cartName + "已装载岩石,请前往岩破");
                newTask.setDestination("YP");
            } else {
                equipmentWrapper.clear();
                equipmentWrapper.eq("EQUIPMENT_NO", shovelNo);
                equipmentMain = equipmentMapper.selectOne(equipmentWrapper);
                String oreField = equipmentMain.getOreField();
                if ("K2".equals(oreField)) {
                    msg.setContent(cartName + "已装载岩石,请前往南采排岩场");
                    newTask.setDestination("NC_PY");
                } else if ("K3".equals(oreField)) {
                    msg.setContent(cartName + "已装载岩石,请前往北采排岩场");
                    newTask.setDestination("BC_PY");
                }
            }
            taskMapper.insert(newTask);
            msg.setFkTaskId(newTask.getId());
            jPushService.sendMsg(msg);
            //记录任务历史过程
            PlanTaskHistoryDto taskHistory = new PlanTaskHistoryDto();
            copyProperties(newTask, taskHistory);
            taskHistory.setTaskId(newTask.getId());
            taskHistoryMapper.insert(taskHistory);
        } else {
            //更改任务状态
            task.setTaskState(TaskState.LoadComplete.getState());
            taskMapper.updateById(task);
            //记录任务历史过程
            PlanTaskHistoryDto taskHistory = new PlanTaskHistoryDto();
            copyProperties(task, taskHistory);
            taskHistory.setTaskId(task.getId());
            taskHistoryMapper.insert(taskHistory);
            msg.setFkTaskId(task.getId());
            jPushService.sendMsg(msg);
        }
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto refuseLeaveShovel(String taskId) {
        PlanTaskDto task = taskMapper.selectById(taskId);//取得当前任务信息
        String shovelNo = task.getPublisher();
        String cartNo = task.getReceiver();//大车编码
        //给电铲发送信息,通知电铲大车目前不能离开
        String cartName = equipmentMainService.translateFromEquipNo2Name(cartNo);
        MessageDto msg = new MessageDto();
        msg.setSender(cartNo);
        msg.setReceiver(shovelNo);
        msg.setContent(cartName + "目前不能离开");
        msg.setEquipmentType(EquipmentType.Shovel.getType());
        msg.setFkTaskId(Integer.parseInt(taskId));
        jPushService.sendMsg(msg);
        String category = task.getCategory();
        if ("4".equals(category)) {
            //删除运送岩石的任务
            taskMapper.deleteById(taskId);
            //将当前电铲当前班次对应的空闲任务分配给这辆车
            String defineSwitch = defineSwitchService.defineSwitch(new Date());
            String[] split = defineSwitch.split("_");
            QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
            taskWrapper.eq("SCHEDULE_DATE", split[0]);
            taskWrapper.eq("SHIFT_TYPE", split[1]);
            taskWrapper.eq("PUBLISHER", shovelNo);
            taskWrapper.eq("TASK_STATE", TaskState.TMP.getState());
            List<PlanTaskDto> taskDtoList = taskMapper.selectList(taskWrapper);
            PlanTaskDto taskTmp = taskDtoList.get(0);
            taskTmp.setReceiver(cartNo);
            taskTmp.setTaskState(TaskState.ArriveShovel.getState());
            taskTmp.setReceiveHandler(task.getReceiveHandler());
            taskMapper.updateById(taskTmp);
            //记录任务历史过程
            PlanTaskHistoryDto taskHistory = new PlanTaskHistoryDto();
            copyProperties(taskTmp, taskHistory);
            taskHistory.setTaskId(taskTmp.getId());
            taskHistoryMapper.insert(taskHistory);
            return ResponseResultDto.ok();
        }
        //更改任务状态
        task.setTaskState(TaskState.ArriveShovel.getState());
        taskMapper.updateById(task);
        //记录任务历史过程
        PlanTaskHistoryDto taskHistory = new PlanTaskHistoryDto();
        copyProperties(task, taskHistory);
        taskHistory.setTaskId(task.getId());
        taskHistoryMapper.insert(taskHistory);
        return ResponseResultDto.ok();
    }


    @Override
    public ResponseResultDto leaveShovel(String taskId) {
        //大车通知电铲,大车从电铲离开
        PlanTaskDto task = taskMapper.selectById(taskId);
        task.setTaskState(TaskState.LeaveShovel.getState());
        String shovelNo = task.getPublisher();
        String cartNo = task.getReceiver();
        taskMapper.updateById(task);
        //将当前任务对应电铲的临时任务中的一个置为创建状态
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        if ("4".equals(task.getCategory())){
            taskWrapper.eq("SCHEDULE_DATE", split[0]);
            taskWrapper.eq("SHIFT_TYPE", split[1]);
            taskWrapper.eq("PUBLISHER", shovelNo);
            taskWrapper.eq("TASK_STATE", TaskState.TMP.getState());
            List<PlanTaskDto> taskDtoList = taskMapper.selectList(taskWrapper);
            PlanTaskDto taskTmp = taskDtoList.get(0);
            taskTmp.setTaskState(TaskState.Create.getState());
            taskMapper.updateById(taskTmp);
        }
        //记录任务历史过程
        PlanTaskHistoryDto taskHistory = new PlanTaskHistoryDto();
        copyProperties(task, taskHistory);
        taskHistory.setTaskId(task.getId());
        taskHistoryMapper.insert(taskHistory);
        String cartName = equipmentMainService.translateFromEquipNo2Name(cartNo);
        MessageDto shovelMsg = new MessageDto();
        shovelMsg.setSender(cartNo);
        shovelMsg.setReceiver(shovelNo);
        shovelMsg.setContent(cartName + "已离开");
        shovelMsg.setEquipmentType(EquipmentType.Shovel.getType());
        shovelMsg.setFkTaskId(Integer.parseInt(taskId));
        jPushService.sendMsg(shovelMsg);
        //给场地推送消息告诉车辆即将到达
        MessageDto fieldMsg = new MessageDto();
        fieldMsg.setSender(cartNo);
        fieldMsg.setReceiver(task.getDestination());
        String shovelName = equipmentMainService.translateFromEquipNo2Name(shovelNo);
        QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
        equipmentWrapper.eq("EQUIPMENT_NO", shovelNo);
        EquipmentMain shovel = equipmentMapper.selectOne(equipmentWrapper);
        String oreField = shovel.getOreField();//矿区编码
        String fieldName = sysDictService.queryDict("ore_field", oreField);//矿区名称
        fieldMsg.setContent(cartName + "已从" + fieldName + shovelName + "离开,即将到达本场地");
        fieldMsg.setEquipmentType(EquipmentType.Field.getType());
        fieldMsg.setFkTaskId(Integer.parseInt(taskId));
        jPushService.sendMsg(fieldMsg);
        //推送下一个任务给周围的车辆
        //找到当前班次创建状态的任务列表,取出其中一个
        taskWrapper.clear();
        taskWrapper.eq("SCHEDULE_DATE", split[0]);
        taskWrapper.eq("SHIFT_TYPE", split[1]);
        taskWrapper.in("TASK_STATE", TaskState.Create.getState(),
                TaskState.Publish.getState());
        taskWrapper.eq("PUBLISHER", shovelNo);
        List<PlanTaskDto> taskList = taskMapper.selectList(taskWrapper);
        if (taskList.size() == 0) {
            return ResponseResultDto.ok().data("msg", "已无新的任务");
        }
        //在周围存在可以接单的车辆时,遍历车辆编码将任务发出去
        List<PlanTaskDto> newTask = taskList.subList(0, 1);
        equipmentMainService.shovelPublishTask(shovelNo, newTask);
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto arriveField(String taskId) {
        //找到当前任务
        PlanTaskDto taskDto = taskMapper.selectById(taskId);
        //找到目的地设备编码
        String destination = taskDto.getDestination();
        //找到大车编码
        String cartNo = taskDto.getReceiver();
        //找到设备对应的名称
        String cartName = equipmentMainService.translateFromEquipNo2Name(cartNo);
        MessageDto msg = new MessageDto();
        msg.setSender(cartNo);
        msg.setReceiver(destination);
        msg.setContent(cartName + "已经到达,请求卸车");
        msg.setEquipmentType(EquipmentType.Field.getType());
        msg.setIsUse(1);
        msg.setFkTaskId(Integer.parseInt(taskId));
        jPushService.sendMsg(msg);
        taskDto.setTaskState(TaskState.ArriveField.getState());
        taskMapper.updateById(taskDto);
        //记录任务历史过程
        PlanTaskHistoryDto taskHistory = new PlanTaskHistoryDto();
        copyProperties(taskDto, taskHistory);
        taskHistory.setTaskId(taskDto.getId());
        taskHistoryMapper.insert(taskHistory);
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto allowUnloading(String taskId) {
        //找到当前任务
        PlanTaskDto taskDto = taskMapper.selectById(taskId);
        //找到目的地设备编码
        String destination = taskDto.getDestination();
        //找到大车编码
        String cartNo = taskDto.getReceiver();
        //找到设备对应的名称
        QueryWrapper<EquipmentMain> equipWrapper = new QueryWrapper<>();
        equipWrapper.eq("EQUIPMENT_NO", cartNo);
        List<EquipmentMain> equipmentMains = equipmentMapper.selectList(equipWrapper);
        String cartName = equipmentMains.get(0).getEquipmentName();
        MessageDto msg = new MessageDto();
        msg.setSender(destination);
        msg.setReceiver(cartNo);
        msg.setContent(cartName + "可以卸车");
        msg.setEquipmentType(EquipmentType.Cart.getType());
        msg.setIsUse(1);
        msg.setFkTaskId(Integer.parseInt(taskId));
        jPushService.sendMsg(msg);
        taskDto.setTaskState(TaskState.Unload.getState());
        taskMapper.updateById(taskDto);
        //记录任务历史过程
        PlanTaskHistoryDto taskHistory = new PlanTaskHistoryDto();
        copyProperties(taskDto, taskHistory);
        taskHistory.setTaskId(taskDto.getId());
        taskHistoryMapper.insert(taskHistory);
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto leaveField(String taskId) {
        PlanTaskDto taskDto = taskMapper.selectById(taskId);//找到当前任务
        //若此任务来自指派,那么将指派任务置为已完成
        Integer toDoId = taskDto.getToDoId();
        if (toDoId != null) {
            TaskToDoDto toDoDto = taskToDoMapper.selectById(toDoId);
            toDoDto.setCompleteState(TaskState.ToDoTaskCompleted.getState());
            taskToDoMapper.updateById(toDoDto);
        }
        String destination = taskDto.getDestination();//找到目的地设备编码
        String cartNo = taskDto.getReceiver();//找到大车编码
        String cartName = equipmentMainService.translateFromEquipNo2Name(cartNo);//找到大车对应的名称
        MessageDto msg = new MessageDto();
        msg.setSender(cartNo);
        msg.setReceiver(destination);
        msg.setContent(cartName + "卸车完毕即将离开");
        msg.setEquipmentType(EquipmentType.Field.getType());
        msg.setIsUse(1);
        msg.setFkTaskId(Integer.parseInt(taskId));
        jPushService.sendMsg(msg);
        taskDto.setTaskState(TaskState.LeaveField.getState());
        taskMapper.updateById(taskDto);
        //记录任务历史过程
        PlanTaskHistoryDto taskHistory = new PlanTaskHistoryDto();
        copyProperties(taskDto, taskHistory);
        taskHistory.setTaskId(taskDto.getId());
        taskHistoryMapper.insert(taskHistory);
        return getNextTask(cartNo);//获取下一个任务发给当前车辆
    }

    @Override
    public ResponseResultDto getNextTask(String cartNo) {
        //删除消息表中司机的未读消息,保证别的任务不会被推送给这个司机
        QueryWrapper<MessageDto> msgWrapper = new QueryWrapper<>();
        msgWrapper.eq("IS_USE", 0);
        msgWrapper.eq("RECEIVER", cartNo);
        messageMapper.delete(msgWrapper);
        //在待接受任务中查找当前车辆有没有待接受的任务,目前指定车辆去找指定的电铲
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        QueryWrapper<TaskToDoDto> taskToDoWrapper = new QueryWrapper<>();
        taskToDoWrapper.eq("EXECUTOR", cartNo);
        taskToDoWrapper.eq("COMPLETE_STATE", TaskState.ToDoTaskCreate.getState());
        TaskToDoDto taskToDo = taskToDoMapper.selectOne(taskToDoWrapper);
        boolean sendSuccess;
        Integer toDoId = null;
        if (taskToDo != null) {
            if (TaskType.Temp.getType().equals(taskToDo.getTaskType())) {
                //临时任务,给大车发送信息
                MessageDto msg = new MessageDto();
                msg.setContent("临时任务:" + taskToDo.getRemark());
                msg.setSender(taskToDo.getHandlerId());
                msg.setType(MessageType.Business.getType());
                msg.setEquipmentType(EquipmentType.Cart.getType());
                msg.setReceiver(cartNo);
                msg.setFkTaskId(0);
                jPushService.sendMsg(msg);
                return ResponseResultDto.ok();
            }
            toDoId = taskToDo.getId();
        }
        //确认大车工作的目标对象
        QueryWrapper<CartWorkTarget> targetWrapper = new QueryWrapper<>();
        targetWrapper.eq("SCHEDULE_DATE", split[0]);
        targetWrapper.eq("SHIFT_TYPE", split[1]);
        targetWrapper.eq("CART_NO", cartNo);
        CartWorkTarget cartWorkTarget = cartWorkTargetMapper.selectOne(targetWrapper);
        if (cartWorkTarget != null) {
            String shovelNo = cartWorkTarget.getShovelNo();//要去工作的电铲
            //找到此电铲对应的未被领取的任务
            sendSuccess = getNotReceivedTaskPublishToCart(shovelNo, cartNo, null, toDoId);
            if (sendSuccess) {
                return ResponseResultDto.ok();
            }
            //如果任务不存在,找这台电铲对应矿区的其余电铲进行遍历,看有没有同一个目的地的任务,如果有就发给这辆车
            //找这台电铲对应的矿区
            QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
            equipmentWrapper.eq("EQUIPMENT_NO", shovelNo);
            EquipmentMain shovel = equipmentMapper.selectOne(equipmentWrapper);
            String oreField = shovel.getOreField();//取得电铲所在的矿区编码
            equipmentWrapper.clear();
            equipmentWrapper.select("EQUIPMENT_NO");
            equipmentWrapper.eq("ORE_FIELD", oreField);
            equipmentWrapper.eq("EQUIPMENT_STATUS", EquipmentStatus.Working.getStatus());
            List<EquipmentMain> equipmentList = equipmentMapper.selectList(equipmentWrapper);//找到当前矿区正在工作的电铲
            //找到之前电铲对应的目的地
            QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
            taskWrapper.eq("SCHEDULE_DATE", split[0]);
            taskWrapper.eq("SHIFT_TYPE", split[1]);
            taskWrapper.eq("PUBLISHER", shovelNo);
            taskWrapper.orderByDesc("DT_UPDATE_DATE_TIME");
            List<PlanTaskDto> taskDtoList = taskMapper.selectList(taskWrapper);
            if (taskDtoList != null && taskDtoList.size() > 0) {
                PlanTaskDto taskDto = taskDtoList.get(0);//按更新时间取最新的一个
                for (EquipmentMain equipmentMain : equipmentList) {
                    //检查当前矿区有没有之前目的地的任务,有则优先发给这辆车
                    String equipmentNo = equipmentMain.getEquipmentNo();
                    sendSuccess = getNotReceivedTaskPublishToCart(equipmentNo, cartNo, taskDto.getDestination(), null);
                    if (sendSuccess) {
                        return ResponseResultDto.ok();
                    }
                }
            }
            for (EquipmentMain equipmentMain : equipmentList) {
                //没有之前目的地的任务,找当前矿区的车辆有没有其他矿破的任务
                String equipmentNo = equipmentMain.getEquipmentNo();
                sendSuccess = getNotReceivedTaskPublishToCart(equipmentNo, cartNo, null, null);
                if (sendSuccess) {
                    return ResponseResultDto.ok();
                }
            }
        }
        //在当前班次任务列表中找到一个任务分配给当前车辆
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.eq("SCHEDULE_DATE", split[0]);
        taskWrapper.eq("SHIFT_TYPE", split[1]);
        taskWrapper.in("TASK_STATE", TaskState.Create.getState(), TaskState.Publish.getState());
        List<PlanTaskDto> taskDtoList = taskMapper.selectList(taskWrapper);
        if (taskDtoList.size() > 0) {
            PlanTaskDto taskDto = taskDtoList.get(0);
            String shovelNo = taskDto.getPublisher();
            getNotReceivedTaskPublishToCart(shovelNo, cartNo, null, null);
            return ResponseResultDto.ok();
        }
        return ResponseResultDto.ServiceError("目前已没有可执行的任务");
    }

    //找到电铲未被领取的任务,发送给指定车辆
    @Override
    public boolean getNotReceivedTaskPublishToCart(String shovelNo, String cartNo, String destination, Integer todoId) {
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.eq("SCHEDULE_DATE", split[0]);
        taskWrapper.eq("SHIFT_TYPE", split[1]);
        taskWrapper.in("TASK_STATE", TaskState.Create.getState(), TaskState.Publish.getState());
        taskWrapper.eq("PUBLISHER", shovelNo);
        if (StringUtils.isNotBlank(destination)) {
            taskWrapper.eq("DESTINATION", destination);
        }
        List<PlanTaskDto> taskList = taskMapper.selectList(taskWrapper);
        if (taskList.size() > 0) {
            //将这个任务发送给指定的车辆
            List<PlanTaskDto> taskDtoList = taskList.subList(0, 1);
            PlanTaskDto taskDto = taskDtoList.get(0);
            List<PlanTaskVo> planTaskVoList = cartTaskDto2TaskVo(taskDtoList, "您有新的任务");
            PlanTaskVo planTaskVo = planTaskVoList.get(0);
            MessageDto msg = new MessageDto();
            msg.setContent(planTaskVo.getTaskText());
            msg.setSender(shovelNo);
            msg.setType(MessageType.Business.getType());
            msg.setEquipmentType(EquipmentType.Cart.getType());
            msg.setReceiver(cartNo);
            msg.setFkTaskId(taskDto.getId());
            jPushService.sendMsg(msg);
            if (todoId != null) {
                taskDto.setToDoId(todoId);
            }
            taskDto.setTaskState(TaskState.Publish.getState());
            taskMapper.updateById(taskDto);
            return true;
        }
        return false;
    }

    @Override
    public ResponseResultDto getTaskHistory(PlanTaskDto task, int current, int pageSize) {
        Page<PlanTaskDto> page = new Page<>(current, pageSize);
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.eq("SCHEDULE_DATE", task.getScheduleDate());
        taskWrapper.eq("SHIFT_TYPE", task.getShiftType());
        taskWrapper.orderByAsc("TASK_STATE");
        taskWrapper.orderByDesc("DT_UPDATE_DATE_TIME");
        String equipmentType = task.getEquipmentType();
        if (EquipmentType.Cart.getType().equals(equipmentType)) {
            taskWrapper.eq("RECEIVE_HANDLER", task.getReceiveHandler());
        } else if (EquipmentType.Shovel.getType().equals(equipmentType)) {
            taskWrapper.eq("PUBLISHER", task.getPublisher());
        } else if (EquipmentType.Field.getType().equals(equipmentType)) {
            taskWrapper.eq("DESTINATION", task.getDestination());
        } else {
            return ResponseResultDto.ServiceError("请传入要查询的设备类型");
        }
        taskWrapper.ne("IS_DEL",1);
        Page<PlanTaskDto> planTaskDtoPage = taskMapper.selectPage(page, taskWrapper);
        //查询已完成任务数
        taskWrapper.eq("TASK_STATE", TaskState.LeaveField.getState());
        Integer completed = taskMapper.selectCount(taskWrapper);
        List<PlanTaskDto> taskDtoList = planTaskDtoPage.getRecords();
        List<PlanTaskVo> result = new ArrayList<>();
        for (PlanTaskDto taskDto : taskDtoList) {
            PlanTaskVo taskVo = new PlanTaskVo();
            copyProperties(taskDto, taskVo);
            String destination = taskDto.getDestination();
            String destinationName = equipmentMainService.translateFromEquipNo2Name(destination);//场地名称
            taskVo.setDestinationName(destinationName);
            String shovelNo = taskDto.getPublisher();
            String shovelName = equipmentMainService.translateFromEquipNo2Name(shovelNo);//电铲名称
            taskVo.setShovelName(shovelName);
            String category = taskDto.getCategory();
            String categoryName = sysDictService.queryDict("raw_type", category);
            taskVo.setCategoryName(categoryName);
            result.add(taskVo);
        }
        return ResponseResultDto.ok().data("data", result).data("total", planTaskDtoPage.getTotal()).data("completed", completed);
    }

    @Override
    public ResponseResultDto getTmpTaskHistory(PlanTaskDto task) {
        String scheduleDate = task.getScheduleDate();
        String shiftType = task.getShiftType();
        String receiver = task.getReceiver();
        QueryWrapper<TaskToDoDto> toDoWrapper = new QueryWrapper<>();
        toDoWrapper.eq("SCHEDULE_DATE", scheduleDate);
        toDoWrapper.eq("SHIFT_TYPE", shiftType);
        toDoWrapper.eq("EXECUTOR", receiver);
        toDoWrapper.eq("TASK_TYPE", TaskType.Temp.getType());
        List<TaskToDoDto> toDoDtoList = taskToDoMapper.selectList(toDoWrapper);
        for (TaskToDoDto toDoDto : toDoDtoList) {
            String completeState = toDoDto.getCompleteState();
            if (TaskState.ToDoTaskCreate.getState().equals(completeState)) {
                toDoDto.setCompleteState("待领取");
            } else if (TaskState.ToDoTaskProcessing.getState().equals(completeState)) {
                toDoDto.setCompleteState("执行中");
            } else if (TaskState.ToDoTaskCompleted.getState().equals(completeState)) {
                toDoDto.setCompleteState("已完成");
            }
        }
        return ResponseResultDto.ok().data("data", toDoDtoList);
    }


    @Override
    public ResponseResultDto getShovelTasks(String equipmentNo) {
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.eq("PUBLISHER", equipmentNo);
        taskWrapper.in("TASK_STATE", TaskState.Publish.getState(), TaskState.Receive.getState()
                , TaskState.LoadComplete.getState()
                , TaskState.ArriveShovel.getState()
        );
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        taskWrapper.eq("SCHEDULE_DATE", split[0]);//当前日期
        taskWrapper.eq("SHIFT_TYPE", split[1]);//当前班次
        taskWrapper.orderByDesc("TASK_STATE");
        List<PlanTaskDto> planTaskDtoList = taskMapper.selectList(taskWrapper);
        List<PlanTaskVo> planTaskVoList = shovelTask2TaskVo(planTaskDtoList);
        return ResponseResultDto.ok().data("data", planTaskVoList);
    }


    @Override
    public ResponseResultDto getFieldTasks(String equipmentNo) {
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        taskWrapper.eq("SCHEDULE_DATE", split[0]);
        taskWrapper.eq("SHIFT_TYPE", split[1]);
        taskWrapper.eq("DESTINATION", equipmentNo);
        taskWrapper.in("TASK_STATE", TaskState.LeaveShovel.getState()
                , TaskState.Unload.getState(), TaskState.ArriveField.getState());
        List<PlanTaskDto> planTaskDtoList = taskMapper.selectList(taskWrapper);
        List<PlanTaskVo> planTaskVoList = fieldTask2TaskVo(planTaskDtoList);
        return ResponseResultDto.ok().data("data", planTaskVoList);
    }


    @Override
    public ResponseResultDto getPlanTaskByPage(PlanTaskDto task, int current, int pageSize) {
        Page<PlanTaskDto> page = new Page<>(current, pageSize);
        String shiftType = task.getShiftType();//班次
        String scheduleDate = task.getScheduleDate();//查询日期
        Integer receiveHandler = task.getReceiveHandler();//任务接收端执行人
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(shiftType)) {
            taskWrapper.eq("SHIFT_TYPE", shiftType);
        }
        if (StringUtils.isNotBlank(scheduleDate)) {
            taskWrapper.eq("SCHEDULE_DATE", scheduleDate);
        }
        if (receiveHandler != null) {
            taskWrapper.eq("RECEIVE_HANDLER", receiveHandler);
        }
        taskWrapper.orderByAsc("TASK_STATE");
        taskWrapper.orderByDesc("DT_UPDATE_DATE_TIME");
        Page<PlanTaskDto> planTaskDtoPage = taskMapper.selectPage(page, taskWrapper);
        return ResponseResultDto.ok().data("data", planTaskDtoPage.getRecords()).data("total", planTaskDtoPage.getTotal());
    }


    public List<PlanTaskVo> fieldTask2TaskVo(List<PlanTaskDto> planTaskDtoList) {
        List<PlanTaskVo> planTaskVoList = new ArrayList<>();
        for (PlanTaskDto taskDto : planTaskDtoList) {
            PlanTaskVo taskVo = new PlanTaskVo();
            taskVo.setId(taskDto.getId());
            taskVo.setTaskState(taskDto.getTaskState());
            String receiver = taskDto.getReceiver();
            //找到设备对应的名称
            String equipName = equipmentMainService.translateFromEquipNo2Name(receiver);
            taskVo.setTaskText(equipName);
            planTaskVoList.add(taskVo);
        }
        return planTaskVoList;
    }

    public List<PlanTaskVo> shovelTask2TaskVo(List<PlanTaskDto> planTaskDtoList) {
        List<PlanTaskVo> planTaskVoList = new ArrayList<>();
        for (PlanTaskDto taskDto : planTaskDtoList) {
            PlanTaskVo taskVo = new PlanTaskVo();
            taskVo.setId(taskDto.getId());
            taskVo.setTaskState(taskDto.getTaskState());
            String receiver = taskDto.getReceiver();
            String equipName = null;
            if (StringUtils.isNotBlank(receiver)) {
                //找到设备对应的名称
                equipName = equipmentMainService.translateFromEquipNo2Name(receiver);
            }
            //拼接文字内容
            String content = "任务待接受";
            TaskState taskState = TaskState.getTaskState(taskDto.getTaskState());
            if (taskState != null) {
                switch (taskState) {
                    case Receive:
                        content = equipName + "_已经接受任务";
                        break;
                    case ArriveShovel:
                        content = equipName + "_已经到达附近";
                        break;
                    case LoadComplete:
                        content = equipName + "_装车完成";
                        break;
                }
            }
            taskVo.setTaskText(content);
            planTaskVoList.add(taskVo);
        }
        return planTaskVoList;
    }


    public List<PlanTaskVo> cartTaskDto2TaskVo(List<PlanTaskDto> taskList, String prefixContent) {
        List<PlanTaskVo> planTaskVoList = new ArrayList<>();
        String content = prefixContent;
        if (taskList != null && taskList.size() > 0) {
            for (PlanTaskDto taskDto : taskList) {
                PlanTaskVo taskVo = new PlanTaskVo();
                copyProperties(taskDto, taskVo);
                //任务被领取之后才显示任务内容
                if (StringUtils.isNotBlank(taskDto.getReceiver())) {
                    String shovelNo = taskDto.getPublisher();
                    String publisherName = equipmentMainService.translateFromEquipNo2Name(shovelNo);
                    //翻译对应矿区名称
                    QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
                    equipmentWrapper.eq("EQUIPMENT_NO", shovelNo);
                    EquipmentMain equipmentMain = equipmentMapper.selectOne(equipmentWrapper);
                    String oreField = equipmentMain.getOreField();//矿区编码
                    String oreFieldName = sysDictService.queryDict("ore_field", oreField);
                    // B从TASK中的category去取,在字典翻译,
                    String category = taskDto.getCategory();
                    String categoryName = sysDictService.queryDict("raw_type", category);
                    // C是task表的destination,在EquipmentMain中翻译
                    String destination = taskDto.getDestination();
                    String destinationName = equipmentMainService.translateFromEquipNo2Name(destination);
                    content += "请到" + "_" + oreFieldName + publisherName + "_装_" + categoryName + "_,之后请送往_" + destinationName;
                    if ("任务变更".equals(prefixContent)) {
                        content = content.replaceAll("_", "");
                    }
                }
                taskVo.setTaskText(content);
                taskVo.setTaskType(TaskType.Normal.getType());
                planTaskVoList.add(taskVo);
            }
        }
        return planTaskVoList;
    }


    private List<String> findVolvoAsSubstitute(List<EquipmentMain> equipmentMains) {
        //没有可用车辆时去找沃尔沃
        List<String> equipNoList;
        if (equipmentMains != null && equipmentMains.size() > 0) {
            equipNoList = equipmentMains.stream().map(EquipmentMain::getEquipmentNo).distinct().collect(Collectors.toList());
            //排除正在工作的设备
            equipNoList = excludeCartUnavailable(equipNoList);
            if (equipNoList != null && equipNoList.size() > 0) {
                return equipNoList;
            }
        }
        //在交接班到上班的时间段内,即早晨7.40-9.30晚上16.40-18.00找不到第一梯队的车就直接返回,这样避免争抢其他矿区的专属车辆资源
        SysConfig sysConfig = configMapper.selectOne(null);
        LocalTime now = LocalTime.now();//取得当前时间
        String timeStr = now.toString();
        String nightStartTime = sysConfig.getNightStartTime();
        String dayStartTime = sysConfig.getDayStartTime();
        if ((timeStr.compareTo(dayStartTime) > 0 && "09:31".compareTo(timeStr) > 0)
                || (timeStr.compareTo(nightStartTime) > 0 && "18:01".compareTo(timeStr) > 0)) {
            return null;
        }
        //没有在工作状态的非沃尔沃车辆,去找沃尔沃
        QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
        equipmentWrapper.select("EQUIPMENT_NO");
        equipmentWrapper.eq("EQUIPMENT_STATUS", EquipmentStatus.Working.getStatus());//工作状态
        equipmentWrapper.eq("EQUIPMENT_TYPE", EquipmentType.Cart.getType());//大车类型
        equipmentWrapper.eq("DETAIL_TYPE", CartDetailType.Volvo.getType());//沃尔沃
        equipmentMains = equipmentMapper.selectList(equipmentWrapper);
        if (equipmentMains == null || equipmentMains.size() == 0) {
            return null;
        } else {
            equipNoList = equipmentMains.stream().map(EquipmentMain::getEquipmentNo).collect(Collectors.toList());
            //排除正在工作的设备
            return excludeCartUnavailable(equipNoList);
        }
    }

    private List<String> getCartsFromAllType(CurrentPositionDto currentEquipment) {
        //全范围无限制找到距离当前设备最近的车辆
        //取得当前在线车辆列表
        QueryWrapper<EquipmentMain> equipWrapper = new QueryWrapper<>();
        equipWrapper.eq("EQUIPMENT_TYPE", EquipmentType.Cart.getType());
        equipWrapper.eq("EQUIPMENT_STATUS", EquipmentStatus.Working.getStatus());
        List<EquipmentMain> equipList = equipmentMapper.selectList(equipWrapper);
        if (equipList == null || equipList.size() == 0) {//在线车辆数为0,直接返回空
            return null;
        }
        List<String> equipNoList = equipList.stream().map(EquipmentMain::getEquipmentNo).collect(Collectors.toList());//取得当前在线车辆设备号
        //排除正在工作的车辆
        equipNoList = excludeCartUnavailable(equipNoList);
        if (equipNoList == null || equipNoList.size() == 0) {
            return null;
        }
        //将车辆列表按距离排序
        return getCartsOrderByDistance(equipNoList, currentEquipment);
    }

    private List<String> excludeCartUnavailable(List<String> equitmentNoList) {
        //找到当前班次列表中正在工作的车辆,排除掉
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        taskWrapper.select("RECEIVER");
        taskWrapper.eq("SCHEDULE_DATE", split[0]);//当前日期
        taskWrapper.eq("SHIFT_TYPE", split[1]);//当前班次
        taskWrapper.in("RECEIVER", equitmentNoList);//当前车辆列表中
        taskWrapper.ne("TASK_STATE", TaskState.LeaveField.getState());//正在工作
        List<PlanTaskDto> taskDtoList = taskMapper.selectList(taskWrapper);
        //找到故障车辆排除掉
        QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
        equipmentWrapper.select("EQUIPMENT_NO");
        equipmentWrapper.eq("EQUIPMENT_TYPE", EquipmentType.Cart.getType());
        equipmentWrapper.eq("EQUIPMENT_STATUS", EquipmentStatus.BreakDown.getStatus());
        List<EquipmentMain> equipmentBreakDown = equipmentMapper.selectList(equipmentWrapper);
        if (equipmentBreakDown.size() > 0) {
            List<String> breakDownNo = equipmentBreakDown.stream().map(EquipmentMain::getEquipmentNo).collect(Collectors.toList());
            equitmentNoList = equitmentNoList.stream()
                    .filter(e -> (!breakDownNo.contains(e)))
                    .distinct().collect(Collectors.toList());
        }
        if (taskDtoList != null && taskDtoList.size() > 0) {
            List<String> exceptWorking = taskDtoList.stream().map(PlanTaskDto::getReceiver).distinct().collect(Collectors.toList());
            equitmentNoList = equitmentNoList.stream()
                    .filter(e -> (!exceptWorking.contains(e)))
                    .distinct().collect(Collectors.toList());
        }
        //找到正在执行临时任务的车辆,排除
        QueryWrapper<TaskToDoDto> toDoWrapper = new QueryWrapper<>();
        toDoWrapper.select("EXECUTOR");
        toDoWrapper.eq("COMPLETE_STATE", TaskState.ToDoTaskProcessing);
        List<TaskToDoDto> toDoDtoList = taskToDoMapper.selectList(toDoWrapper);
        if (toDoDtoList != null && toDoDtoList.size() > 0) {
            List<String> exceptTempWorking = toDoDtoList.stream().map(TaskToDoDto::getExecutor).collect(Collectors.toList());
            equitmentNoList = equitmentNoList.stream()
                    .filter(e -> (!exceptTempWorking.contains(e)))
                    .distinct().collect(Collectors.toList());
        }
        return equitmentNoList;
    }

    /**
     * 此方法找最短距离的核心思想是两台设备经纬度之差最小
     *
     * @param equipNoList      待判断距离的车辆
     * @param currentEquipment 当前电铲设备
     * @return 由近至远的车辆序号
     */
    private List<String> getCartsOrderByDistance(List<String> equipNoList, CurrentPositionDto currentEquipment) {
        //查找车辆的位置数据
        QueryWrapper<CurrentPositionDto> currentPositionDtoQueryWrapper = new QueryWrapper<>();
        currentPositionDtoQueryWrapper.in("EQUIPMENT_NO", equipNoList);
        List<CurrentPositionDto> latestPositionResult = currentPositionMapper.selectList(currentPositionDtoQueryWrapper);
        //将在线车辆的最新位置数据存储在集合中
        Map<String, Double> map = latestPositionResult.stream()
                .collect(Collectors.toMap(CurrentPositionDto::getEquipmentNo, c -> c.getLat() + c.getLng()));
        Map<String, Double> difMap = new HashMap<>();
        //确定车辆与设备位置差值
        double currLng = currentEquipment.getLng();
        double currentLat = currentEquipment.getLat();
        double sum = currLng + currentLat;
        map.forEach((cartNo, pos) -> difMap.put(cartNo, Math.abs(pos - sum)));
        //按差值升序排序
        List<String> list = new ArrayList<>();
        List<Map.Entry<String, Double>> collect = difMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        for (Map.Entry<String, Double> sortedMap : collect) {
            list.add(sortedMap.getKey());
        }
        return list;
    }


}
