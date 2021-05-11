package com.th.workbase.service.equipment.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.equipment.EquipmentBreakDownDto;
import com.th.workbase.bean.equipment.EquipmentMain;
import com.th.workbase.bean.equipment.MessageDto;
import com.th.workbase.bean.equipment.vo.EquipmentBreakDownVo;
import com.th.workbase.bean.equipment.vo.StatisticVo;
import com.th.workbase.bean.plan.*;
import com.th.workbase.bean.system.LoadDistanceDto;
import com.th.workbase.bean.system.LoadWeightDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysConfig;
import com.th.workbase.common.commonEnum.*;
import com.th.workbase.common.utils.WebSocketUtil;
import com.th.workbase.mapper.equipment.EquipmentBreakDownMapper;
import com.th.workbase.mapper.equipment.EquipmentMainMapper;
import com.th.workbase.mapper.plan.CartWorkTargetMapper;
import com.th.workbase.mapper.plan.PlanDetailMapper;
import com.th.workbase.mapper.plan.PlanTaskMapper;
import com.th.workbase.mapper.plan.TaskToDoMapper;
import com.th.workbase.mapper.system.LoadDistanceMapper;
import com.th.workbase.mapper.system.LoadWeightMapper;
import com.th.workbase.mapper.system.SysConfigMapper;
import com.th.workbase.service.common.DefineSwitchService;
import com.th.workbase.service.common.JPushService;
import com.th.workbase.service.equipment.EquipmentBreakDownService;
import com.th.workbase.service.equipment.EquipmentMainService;
import com.th.workbase.service.equipment.WorkStatusHistoryService;
import com.th.workbase.service.plan.PlanTaskService;
import com.th.workbase.service.system.SysDictService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cc
 * @since 2021-02-24
 */
@Service
public class EquipmentMainServiceImpl extends ServiceImpl<EquipmentMainMapper, EquipmentMain> implements EquipmentMainService {
    @Resource
    EquipmentMainMapper equipmentMapper;
    @Resource
    WorkStatusHistoryService workStatusHistoryService;
    @Autowired
    PlanTaskService planTaskService;
    @Resource
    PlanDetailMapper planDetailMapper;
    @Autowired
    JPushService jPushService;
    @Resource
    PlanTaskMapper taskMapper;
    @Autowired
    DefineSwitchService defineSwitchService;
    @Resource
    SysConfigMapper configMapper;
    @Resource
    EquipmentBreakDownMapper breakDownMapper;
    @Resource
    TaskToDoMapper toDoMapper;
    @Autowired
    EquipmentBreakDownService equipmentBreakDownService;
    @Resource
    LoadWeightMapper loadWeightMapper;
    @Autowired
    SysDictService sysDictService;
    @Resource
    CartWorkTargetMapper cartWorkTargetMapper;
    @Resource
    LoadDistanceMapper distanceMapper;


    @Override
    public ResponseResultDto createEquipment(HttpServletRequest request, EquipmentMain equipmentMain) {
        if (equipmentMain.getEquipmentName() == null || equipmentMain.getEquipmentNo() == null || equipmentMain.getEquipmentType() == null) {
            return ResponseResultDto.ServiceError("参数错误");
        }

        QueryWrapper<EquipmentMain> equipmentMainQueryWrapper = new QueryWrapper<>();
        equipmentMainQueryWrapper.eq("EQUIPMENT_NAME", equipmentMain.getEquipmentName())
                .or()
                .eq("EQUIPMENT_NO", equipmentMain.getEquipmentNo());
        EquipmentMain current = baseMapper.selectOne(equipmentMainQueryWrapper);
        if (current != null) {
            return ResponseResultDto.ServiceError("该设备已添加过，请重新尝试");
        }
        baseMapper.insert(equipmentMain);
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto getEquipment(HttpServletRequest request, EquipmentMain equipmentMain, int current, int pageSize) {
        QueryWrapper<EquipmentMain> equipmentMainQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(equipmentMain.getEquipmentNo())) {
            equipmentMainQueryWrapper.eq("EQUIPMENT_NO", equipmentMain.getEquipmentNo());
        }
        if (StringUtils.isNotBlank(equipmentMain.getEquipmentName())) {
            equipmentMainQueryWrapper.like("EQUIPMENT_NAME", equipmentMain.getEquipmentName());
        }
        if (StringUtils.isNotBlank(equipmentMain.getEquipmentType())) {
            equipmentMainQueryWrapper.eq("EQUIPMENT_TYPE", equipmentMain.getEquipmentType());
        }
        equipmentMainQueryWrapper.orderByAsc("EQUIPMENT_NO");
        Page<EquipmentMain> page = new Page<>(current, pageSize);
        Page<EquipmentMain> records = baseMapper.selectPage(page, equipmentMainQueryWrapper);
        List<EquipmentMain> equipmentMainList = records.getRecords();
        return ResponseResultDto.ok().data("data", equipmentMainList).data("total", records.getTotal());
    }

    @Override
    public ResponseResultDto deleteEquipment(Integer id) {
        if (id == null) {
            return ResponseResultDto.ServiceError("参数错误");
        }
        EquipmentMain current = baseMapper.selectById(id);
        if (current == null) {
            return ResponseResultDto.ServiceError("没有找到此设备");
        }
        if ("1".equals(current.getRegistered())) {
            return ResponseResultDto.ServiceError("此设备已被绑定，请解除绑定后重试");
        }
        baseMapper.deleteById(id);
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto updateEquipment(HttpServletRequest request, EquipmentMain equipmentMain) {

        if (StringUtils.isNotBlank(equipmentMain.getEquipmentName()) && StringUtils.isNotBlank(equipmentMain.getEquipmentNo())) {
            QueryWrapper<EquipmentMain> equipmentMainQueryWrapper = new QueryWrapper<>();
            equipmentMainQueryWrapper.ne("ID", equipmentMain.getId());
            equipmentMainQueryWrapper.and(t -> t.eq("EQUIPMENT_NAME", equipmentMain.getEquipmentName())
                    .or()
                    .eq("EQUIPMENT_NO", equipmentMain.getEquipmentNo()));
            List<EquipmentMain> current = baseMapper.selectList(equipmentMainQueryWrapper);
            if (current.size() > 0) {
                return ResponseResultDto.ServiceError("该设备已添加过，请重新尝试");
            }
        }
        baseMapper.updateById(equipmentMain);
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto getEquipmentByDeviceId(String deviceId) {
        QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
        equipmentWrapper.eq("DEVICE_ID", deviceId);
        EquipmentMain equipment = equipmentMapper.selectOne(equipmentWrapper);
        if (equipment == null) {
            return ResponseResultDto.ok().data("msg", "未找到设备编码对应的设备");
        }

        if (equipment.getEquipmentStatus().equals(EquipmentStatus.Offline.getStatus())) {
            //检查故障表中是否有这个设备,如果有这个设备,就将设备状态置为故障,否则设备状态为休息
            QueryWrapper<EquipmentBreakDownDto> breakdownWrapper = new QueryWrapper<>();
            breakdownWrapper.eq("EQUIPMENT_NO", equipment.getEquipmentNo());
            breakdownWrapper.in("FAULT_STATE", FaultState.Create.getState(), FaultState.Processing.getState());
            Integer count = breakDownMapper.selectCount(breakdownWrapper);
            if (count > 0) {
                equipment.setEquipmentStatus(EquipmentStatus.BreakDown.getStatus());
            } else {
                equipment.setEquipmentStatus(EquipmentStatus.Resting.getStatus());
            }
        }

        String equipmentType = equipment.getEquipmentType();//设备类型编码
        String typeName = "";
        if (EquipmentType.Shovel.getType().equals(equipmentType)) {
            String oreField = equipment.getOreField();//矿区编码
            String fieldName = sysDictService.queryDict("ore_field", oreField);//矿区名称
            equipment.setOreFieldName(fieldName);
            typeName = sysDictService.queryDict("shovel_type", equipment.getDetailType());
        } else if (EquipmentType.Cart.getType().equals(equipmentType)) {
            typeName = sysDictService.queryDict("carts_type", equipment.getDetailType());
        }
        equipment.setDetailTypeName(typeName);
        return ResponseResultDto.ok().data("data", equipment);
    }

    @Override
    public ResponseResultDto updateShovelStatus(EquipmentMain equipment) {
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        String equipmentNo = equipment.getEquipmentNo();
        String equipmentStatus = equipment.getEquipmentStatus();
        //检查这个设备目前有没有任务正在执行,如果正在执行任务不允许改为休息
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.gt("TASK_STATE", TaskState.Create.getState());
        taskWrapper.lt("TASK_STATE", TaskState.LeaveField.getState());
        taskWrapper.eq("PUBLISHER", equipmentNo);
        taskWrapper.eq("SCHEDULE_DATE", split[0]);
        taskWrapper.eq("SHIFT_TYPE", split[1]);
        Integer count = taskMapper.selectCount(taskWrapper);
        if (count > 0 && EquipmentStatus.Resting.getStatus().equals(equipmentStatus)) {
            return ResponseResultDto.ServiceError("目前有正在执行的任务,不可以休息");
        }
        //按设备编码更新设备状态
        QueryWrapper<EquipmentMain> equipQuery = new QueryWrapper<>();
        equipQuery.eq("EQUIPMENT_NO", equipmentNo);
        equipmentMapper.update(equipment, equipQuery);
        equipment.setEquipmentType(EquipmentType.Shovel.getType());
        workStatusHistoryService.addStatusData(equipment);//更新设备历史工作状态

        EquipmentStatus status = EquipmentStatus.getEquipmentStatus(equipmentStatus);
        if (status == null) {
            return ResponseResultDto.ServiceError("设备状态为空");
        }
        switch (status) {
            case Working:
                //如果当前故障列表中此设备的故障状态不是忽略和结束,将故障状态置为结束
                faultRecovery(equipmentNo);
                //电铲工作状态变为工作中时,依据计划生成任务,找到附近上班状态的大车,将任务推送给车辆
                //依据当前计划,生成对应的任务
                planTaskService.generateTaskByPlan(equipmentNo);
                //找到任务发布设备在当前时间对应创建和已发布状态的任务
                List<PlanTaskDto> taskList = planTaskService.getTaskByPublisher(equipmentNo);
                if (taskList == null || taskList.size() == 0) {
                    return ResponseResultDto.ok().data("msg", "当前任务列表为空,无可发送的任务");//没有可以发送的任务,直接返回
                }
                shovelPublishTask(equipmentNo, taskList);
                break;
            case BreakDown:
                recordFault(equipment);
                break;
        }
        return ResponseResultDto.ok();
    }

    public void recordFault(EquipmentMain equipment) {
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        if ("YP".equals(equipment.getEquipmentNo())){
            //检查当前前往排岩的任务,依据电铲位置更新目的地
            QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
            taskWrapper.eq("SCHEDULE_DATE",split[0]);
            taskWrapper.eq("SHIFT_TYPE",split[1]);
            taskWrapper.eq("DESTINATION","YP");
            taskWrapper.gt("TASK_STATE",TaskState.Publish.getState());
            taskWrapper.lt("TASK_STATE",TaskState.LeaveField.getState());
            List<PlanTaskDto> taskDtoList = taskMapper.selectList(taskWrapper);
            for (PlanTaskDto taskDto : taskDtoList) {
                String shovelNo = taskDto.getPublisher();
                QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
                equipmentWrapper.eq("EQUIPMENT_NO",shovelNo);
                EquipmentMain equipmentMain = equipmentMapper.selectOne(equipmentWrapper);
                String oreField = equipmentMain.getOreField();
                if ("K3".equals(oreField)){
                    //北采前往北排岩场
                    taskDto.setDestination("BC_PY");
                    taskMapper.updateById(taskDto);
                }else {
                    //新区和南采前往南排岩场
                    taskDto.setDestination("NC_PY");
                    taskMapper.updateById(taskDto);
                }
                String cartNo = taskDto.getReceiver();
                MessageDto msg = new MessageDto();
                msg.setReceiver(cartNo);
                msg.setSender(shovelNo);
                msg.setType(MessageType.Business.getType());
                msg.setEquipmentType(EquipmentType.Cart.getType());
                msg.setContent("任务目的地发生变更");
                msg.setFkTaskId(taskDto.getId());
                //此处与前端约定,收到此标志位后,前端回传位置时带着任务id,保证可以在接近南北排岩场地时自动更新状态
                msg.setTitle("3_"+taskDto.getId());
                jPushService.sendMsg(msg);
            }
        }
        //电铲工作状态变为故障,更新电铲状态,将故障写入故障表
        EquipmentBreakDownDto breakDownDto = new EquipmentBreakDownDto();
        breakDownDto.setEquipmentNo(equipment.getEquipmentNo());
        breakDownDto.setHandlerId(equipment.getHandlerId());
        breakDownDto.setRemark(equipment.getRemark());
        breakDownDto.setScheduleDate(split[0]);
        breakDownDto.setShiftType(split[1]);
        breakDownDto.setEquipmentType(EquipmentType.Shovel.getType());
        breakDownDto.setFaultType(equipment.getEquipmentStatus());
        breakDownDto.setFaultDetailType(equipment.getFaultDetailType());
        breakDownDto.setFaultState(FaultState.Create.getState());
        breakDownMapper.insert(breakDownDto);
        List<EquipmentBreakDownDto> dtoList = new ArrayList<>();
        dtoList.add(breakDownDto);
        List<EquipmentBreakDownVo> vos = equipmentBreakDownService.breakDto2Vo(dtoList);
        EquipmentBreakDownVo breakDownVo = vos.get(0);
        String breakDownJsonStr = JSONObject.toJSONString(breakDownVo);
        WebSocketUtil.sendMessageToAll(breakDownJsonStr);
    }

    public Integer countTaskInHand(Integer planDetailId) {
        //检查当前设备对应的在执行任务数是否满足上限,若已经是上限,不再发送新的任务,需要等待在执行任务结束之后才会发布新任务
        //依据计划明细主键和状态查找在执行任务数
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.eq("FK_PLAN_DETAIL", planDetailId);
        //这里推送状态要算作在执行任务,要不然所有的任务都会被推送出去,推送任务无人接单可以使用重新发送的功能
        taskWrapper.gt("TASK_STATE", TaskState.Create.getState());
        taskWrapper.lt("TASK_STATE", TaskState.LeaveShovel.getState());
        return taskMapper.selectCount(taskWrapper);
    }

    //电铲发布任务
    @Override
    public ResponseResultDto shovelPublishTask(String equipmentNo, List<PlanTaskDto> taskList) {
        //每个可执行的任务都对应一个计划详情里面的任务上限,所以要对每个任务进行检查是否可以发出去,
        // 补充计划的计划内容规定的任务上限可能会不同,因此查询的时候要带着计划详情的外键
        String uuid = UUID.randomUUID().toString();//生成一个随机字符串,本轮推送的消息都要带上这个字符串标识,前台可以判断是同一次的任务请求
        for (PlanTaskDto task : taskList) {
            Integer planDetailId = task.getFkPlanDetail();
            PlanDetail planDetail = planDetailMapper.selectById(planDetailId);
            if (planDetail == null) {
                return ResponseResultDto.ServiceError("计划明细不存在");
            }
            Integer taskMaximum = planDetail.getTaskMaximum();
            Integer cartsToFind = planDetail.getCartsToFind();
            //判断是否发送任务
            Integer taskCount = countTaskInHand(planDetailId);//计划明细在执行的任务数
            if (taskCount >= taskMaximum) {
                continue;//当前任务数已达到上限,不会自动发送任务
            }
            //将任务发给最近的几辆车
            List<String> deviceNearBy = planTaskService.getDeviceNearBy(equipmentNo, task.getDestination(), task.getPriorityTrigger());
            if (deviceNearBy == null || deviceNearBy.size() == 0) {
                return ResponseResultDto.ok().data("msg", "附近没有可用车辆");//周围没有上线工作的大车,直接返回
            }
            if (deviceNearBy.size() > cartsToFind) {
                deviceNearBy = deviceNearBy.subList(0, cartsToFind);
            }
            String content = "您有新的任务";
            MessageDto msg = new MessageDto();
            msg.setContent(content);
            msg.setSender(equipmentNo);
            msg.setType(MessageType.Business.getType());
            msg.setEquipmentType(EquipmentType.Cart.getType());
            task.setTaskState(TaskState.Publish.getState());
            taskMapper.updateById(task);
            for (String equipNo : deviceNearBy) {
                //检查当前车辆本班次最后服务过的非当前任务对应的电铲是否还有任务未被领取,如果有不给这个车发消息
                String defineSwitch = defineSwitchService.defineSwitch(new Date());
                String[] split = defineSwitch.split("_");
                QueryWrapper<CartWorkTarget> cartWorkTargetQueryWrapper = new QueryWrapper<>();
                cartWorkTargetQueryWrapper.eq("SCHEDULE_DATE", split[0]);
                cartWorkTargetQueryWrapper.eq("SHIFT_TYPE", split[1]);
                cartWorkTargetQueryWrapper.eq("CART_NO", equipNo);
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
                        Integer count = taskMapper.selectCount(taskWrapper);
                        if (count > 0) {
                            continue;
                        }
                    }
                }
                msg.setUuid(uuid);//用于前端判断刷新时机
                msg.setReceiver(equipNo);
                msg.setFkTaskId(task.getId());
                jPushService.sendMsg(msg);
            }
        }
        return ResponseResultDto.ok();
    }

    //依据子计划中的任务上限,检查当前有没有不饱和的子计划,若有则给大车发出任务
    @Override
    public ResponseResultDto checkUnsaturatedDetailPlanAndSendTask() {
        List<Plan> plans = planTaskService.getCurrentSwitchPlans();
        //获取当前配置信息,确认当前找车的模式
        SysConfig sysConfig = configMapper.selectOne(null);
        String pushMode = sysConfig.getPushMode();
        boolean trigger = "2".equals(pushMode);
        for (Plan plan : plans) {
            //找到所有子计划
            Integer planId = plan.getId();
            QueryWrapper<PlanDetail> detailWrapper = new QueryWrapper<>();
            detailWrapper.eq("PLAN_ID", planId);
            List<PlanDetail> detailList = planDetailMapper.selectList(detailWrapper);
            for (PlanDetail detail : detailList) {
                Integer detailId = detail.getId();
                //子计划未生成任务的要生成任务
                List<PlanTaskDto> taskList = new ArrayList<>();
                if (detail.getIsUse() != 1) {
                    for (int i = 0; i < detail.getCartsCount(); i++) {
                        PlanTaskDto task = new PlanTaskDto();
                        task.setFkPlanDetail(detailId);//设置计划明细表外键
                        task.setDestination(plan.getDestination());//设置任务目的地
                        task.setPublisher(detail.getEquipmentNo());//设置任务发布者为电铲车编码
                        task.setTaskState(TaskState.Create.getState());//设置任务状态为创建状态
                        task.setScheduleDate(plan.getScheduleDate());//设置任务日期
                        task.setShiftType(plan.getShiftType());//设置任务班次
                        task.setPriorityTrigger(trigger);
                        task.setCategory(detail.getCategory());
                        //设置运输距离
                        QueryWrapper<LoadDistanceDto> distanceWrapper = new QueryWrapper<>();
                        distanceWrapper.eq("SHOVEL_NO", detail.getEquipmentNo());
                        distanceWrapper.eq("FIELD_NO", plan.getDestination());
                        LoadDistanceDto distanceDto = distanceMapper.selectOne(distanceWrapper);
                        if (distanceDto != null) {
                            task.setDistance(distanceDto.getDistance());
                        }
                        taskMapper.insert(task);
                        taskList.add(task);
                    }
                    detail.setIsUse(1);
                    planDetailMapper.updateById(detail);
                } else {
                    //拿到今天状态为推送和创建状态的任务
                    QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
                    taskWrapper.eq("FK_PLAN_DETAIL", detailId);
                    taskWrapper.in("TASK_STATE", TaskState.Create.getState(), TaskState.Publish.getState());
                    taskList = taskMapper.selectList(taskWrapper);
                }
                shovelPublishTask(detail.getEquipmentNo(), taskList);
            }
        }
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto getEquipmentsOnline() {
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        List<EquipmentMain> equipmentMains = equipmentMapper.getEquipmentCurrentPosition();
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        for (EquipmentMain equipment : equipmentMains) {
            String equipmentType = equipment.getEquipmentType();//设备大类
            if (EquipmentType.Cart.getType().equals(equipmentType)) {
                String detailName = sysDictService.queryDict("carts_type", equipment.getDetailType());
                equipment.setDetailTypeName(detailName);
                //找到当前工作对应的电铲
                taskWrapper.clear();
                taskWrapper.eq("SCHEDULE_DATE", split[0]);
                taskWrapper.eq("SHIFT_TYPE", split[1]);
                taskWrapper.lt("TASK_STATE", TaskState.LeaveField.getState());
                taskWrapper.eq("RECEIVER", equipment.getEquipmentNo());
                PlanTaskDto taskDto = taskMapper.selectOne(taskWrapper);
                if (taskDto != null) {
                    String shovelNo = taskDto.getPublisher();
                    String shovelName = translateFromEquipNo2Name(shovelNo);
                    equipment.setCurrentShovel(shovelName);
                }
            } else if (EquipmentType.Shovel.getType().equals(equipmentType)) {
                String detailName = sysDictService.queryDict("shovel_type", equipment.getDetailType());
                equipment.setDetailTypeName(detailName);
                //电铲对应的车辆列表及车辆状态
                taskWrapper.clear();
                taskWrapper.eq("SCHEDULE_DATE", split[0]);
                taskWrapper.eq("SHIFT_TYPE", split[1]);
                taskWrapper.lt("TASK_STATE", TaskState.LeaveField.getState());
                taskWrapper.eq("PUBLISHER", equipment.getEquipmentNo());
                taskWrapper.isNotNull("RECEIVER");
                List<PlanTaskDto> taskDtoList = taskMapper.selectList(taskWrapper);
                equipment.setTaskList(taskDtoList);
            } else if (EquipmentType.Field.getType().equals(equipmentType)) {
                //场地对应的车辆列表及车辆状态
                taskWrapper.clear();
                taskWrapper.eq("SCHEDULE_DATE", split[0]);
                taskWrapper.eq("SHIFT_TYPE", split[1]);
                taskWrapper.lt("TASK_STATE", TaskState.LeaveField.getState());
                taskWrapper.eq("DESTINATION", equipment.getEquipmentNo());
                taskWrapper.isNotNull("RECEIVER");
                List<PlanTaskDto> taskDtoList = taskMapper.selectList(taskWrapper);
                equipment.setTaskList(taskDtoList);
            }
        }
        return ResponseResultDto.ok().data("data", equipmentMains);
    }


    @Override
    public ResponseResultDto getTurnoverByCartNo(StatisticVo statisticParam) {
        //找到查询范围,日期,班次,设备编码
        List<StatisticVo> statisticVoList = equipmentMapper.getTurnoverByCartNo(statisticParam);

        for (int i = 0; i < statisticVoList.size(); i++) {
            StatisticVo statisticVo = statisticVoList.get(i);
            statisticVo.setId(i);
            String cartNo = statisticVo.getCartNo();
            //查询大车类型
            QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
            equipmentWrapper.eq("EQUIPMENT_NO", cartNo);
            EquipmentMain equipmentMain = equipmentMapper.selectOne(equipmentWrapper);
            String cartType = equipmentMain.getDetailType();
            statisticVo.setCartType(cartType);
            String cartTypeName = sysDictService.queryDict("carts_type", cartType);
            statisticVo.setCartTypeName(cartTypeName);
            statisticVo.setCartName(equipmentMain.getEquipmentName());

            //取得对应矿石种类的载重量
            QueryWrapper<LoadWeightDto> weightWrapper = new QueryWrapper<>();
            weightWrapper.eq("CART_TYPE", cartType);
            LoadWeightDto loadWeightDto = loadWeightMapper.selectOne(weightWrapper);

            Integer mineWeight = loadWeightDto.getOreWeight();//拉矿石单车重量
            Integer rockWeight = loadWeightDto.getStockWeight();//拉岩石单车重量

            Integer rockDistance = statisticVo.getRockDistance();//拉岩石总距离
            if (rockDistance != null) {
                statisticVo.setRockTurnOver((double) (rockDistance * rockWeight));
            }
            Integer mineDistance = statisticVo.getMineDistance();//拉矿石总距离
            if (mineDistance != null) {
                statisticVo.setMineTurnOver((double) (mineDistance * mineWeight));
            }
        }
        return ResponseResultDto.ok().data("data", statisticVoList);
    }

    @Override
    public ResponseResultDto getAvailableShovel() {
        QueryWrapper<EquipmentMain> equipWrapper = new QueryWrapper<>();
        equipWrapper.select("EQUIPMENT_NO", "EQUIPMENT_NAME", "EQUIPMENT_STATUS", "ORE_FIELD");
        equipWrapper.in("EQUIPMENT_STATUS", EquipmentStatus.Working.getStatus(), EquipmentStatus.Resting.getStatus());
        equipWrapper.eq("EQUIPMENT_TYPE", EquipmentType.Shovel.getType());
        List<EquipmentMain> equipmentMains = equipmentMapper.selectList(equipWrapper);
        return ResponseResultDto.ok().data("data", equipmentMains);
    }

    @Override
    public ResponseResultDto getShovelWithTask() {
        //找到当前班次对应的所有详细计划
        List<Plan> plans = planTaskService.getCurrentSwitchPlans();
        List<PlanDetail> detailPlanList = new ArrayList<>();
        for (Plan plan : plans) {
            Integer planId = plan.getId();
            QueryWrapper<PlanDetail> detailWrapper = new QueryWrapper<>();
            detailWrapper.eq("PLAN_ID", planId);
            List<PlanDetail> details = planDetailMapper.selectList(detailWrapper);
            detailPlanList.addAll(details);
        }
        List<String> shovelNoList = detailPlanList.stream().map(PlanDetail::getEquipmentNo).distinct().collect(Collectors.toList());
        List<EquipmentMain> equipmentMains = new ArrayList<>();
        for (String shovelNo : shovelNoList) {
            QueryWrapper<EquipmentMain> equipWrapper = new QueryWrapper<>();
            equipWrapper.select("EQUIPMENT_NO", "EQUIPMENT_NAME", "ORE_FIELD");
            equipWrapper.eq("EQUIPMENT_NO", shovelNo);
            EquipmentMain shovel = equipmentMapper.selectOne(equipWrapper);
            String oreField = shovel.getOreField();
            String oreFieldName = sysDictService.queryDict("ore_field", oreField);
            shovel.setOreFieldName(oreFieldName);
            equipmentMains.add(shovel);
        }
        return ResponseResultDto.ok().data("data", equipmentMains);
    }

    @Override
    public ResponseResultDto getAvailableCart() {
        //找到所有工作状态的大车
        QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
        equipmentWrapper.in("EQUIPMENT_STATUS", EquipmentStatus.Working.getStatus(), EquipmentStatus.Resting.getStatus());
        equipmentWrapper.eq("EQUIPMENT_TYPE", EquipmentType.Cart.getType());
        List<EquipmentMain> equipmentList = equipmentMapper.selectList(equipmentWrapper);

        //找到所有有普通任务的大车
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.lt("TASK_STATE", TaskState.LeaveField.getState());
        taskWrapper.eq("SCHEDULE_DATE", split[0]);
        taskWrapper.eq("SHIFT_TYPE", split[1]);
        List<PlanTaskDto> taskDtoList = taskMapper.selectList(taskWrapper);
        if (taskDtoList.size() > 0) {
            List<String> cartInNormalWork = taskDtoList.stream().map(PlanTaskDto::getReceiver).collect(Collectors.toList());
            equipmentList = equipmentList.stream()
                    .filter(e -> !cartInNormalWork.contains(e.getEquipmentNo()))
                    .collect(Collectors.toList());
        }
        //找到所有有临时任务的大车
        QueryWrapper<TaskToDoDto> toDoWrapper = new QueryWrapper<>();
        toDoWrapper.in("COMPLETE_STATE", TaskState.ToDoTaskCreate.getState(), TaskState.ToDoTaskProcessing.getState());
        List<TaskToDoDto> toDoDtoList = toDoMapper.selectList(toDoWrapper);
        if (toDoDtoList.size() > 0) {
            List<String> cartInTmpWork = toDoDtoList.stream().map(TaskToDoDto::getExecutor).collect(Collectors.toList());
            equipmentList = equipmentList.stream()
                    .filter(e -> !cartInTmpWork.contains(e.getEquipmentNo()))
                    .collect(Collectors.toList());
        }
        return ResponseResultDto.ok().data("data", equipmentList);
    }

    @Override
    public ResponseResultDto getAvailableField() {
        //取得所有正在工作状态的场地
        QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
        equipmentWrapper.in("EQUIPMENT_STATUS", EquipmentStatus.Working.getStatus(), EquipmentStatus.Resting.getStatus());
        equipmentWrapper.eq("EQUIPMENT_TYPE", EquipmentType.Field.getType());
        List<EquipmentMain> equipmentList = equipmentMapper.selectList(equipmentWrapper);
        return ResponseResultDto.ok().data("data", equipmentList);
    }

    @Override
    public ResponseResultDto getTurnoverByHandler(StatisticVo statisticParam) {
        //找到查询范围,日期,指定人员
        List<StatisticVo> equipmentMainVos = equipmentMapper.getTurnoverByHandler(statisticParam);
        //按司机id进行分组后,按车型计算周转率
        Map<String, List<StatisticVo>> collect = equipmentMainVos.stream()
                .collect(Collectors.groupingBy(StatisticVo::getHandlerId));
        List<StatisticVo> resultList = new ArrayList<>();
        collect.forEach((handlerId, list) -> {
            StatisticVo result = new StatisticVo();
            result.setHandlerId(handlerId);//取得操作人
            result.setId(resultList.size());//给前台一个不重复的id
            resultList.add(result);
            for (StatisticVo statisticVo : list) {
                String cartNo = statisticVo.getCartNo();//大车编码
                //取得大车详细类型
                QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
                equipmentWrapper.eq("EQUIPMENT_NO", cartNo);
                EquipmentMain equipmentMain = equipmentMapper.selectOne(equipmentWrapper);
                String detailType = equipmentMain.getDetailType();
                //取得对应矿石种类的载重量
                QueryWrapper<LoadWeightDto> weightWrapper = new QueryWrapper<>();
                weightWrapper.eq("CART_TYPE", detailType);
                LoadWeightDto loadWeightDto = loadWeightMapper.selectOne(weightWrapper);
                Integer mineWeight = loadWeightDto.getOreWeight();//拉矿石单车重量
                Integer rockWeight = loadWeightDto.getStockWeight();//拉岩石单车重量

                Integer rockDistance = statisticVo.getRockDistance();//拉岩石总距离
                if (rockDistance != null) {
                    if (result.getRockTurnOver() != null) {
                        result.setRockTurnOver(result.getRockTurnOver() + rockDistance * rockWeight);
                    } else {
                        result.setRockTurnOver((double) (rockDistance * rockWeight));
                    }
                }
                Integer mineDistance = statisticVo.getMineDistance();//拉矿石总距离
                if (mineDistance != null) {
                    if (result.getMineTurnOver() != null) {
                        result.setMineTurnOver(result.getMineTurnOver() + mineDistance * mineWeight);
                    } else {
                        result.setMineTurnOver((double) (mineDistance * mineWeight));
                    }
                }
            }
        });
        //翻译司机姓名
        for (StatisticVo statisticVo : resultList) {
            String handlerId = statisticVo.getHandlerId();
            String handlerName = equipmentMainVos.stream()
                    .filter(e -> e.getHandlerId().equals(handlerId))
                    .collect(Collectors.toList())
                    .get(0)
                    .getHandlerName();
            statisticVo.setHandlerName(handlerName);
        }
        return ResponseResultDto.ok().data("data", resultList);
    }


    @Override
    public ResponseResultDto getTurnoverByType(StatisticVo statisticParam) {
        //找到查询范围,日期,指定人员
        List<StatisticVo> statisticVoList = equipmentMapper.getTurnoverByType(statisticParam);
        for (StatisticVo statisticVo : statisticVoList) {
            String detailType = statisticVo.getCartType();//拿到详细类型
            //取得对应矿石种类的载重量
            QueryWrapper<LoadWeightDto> weightWrapper = new QueryWrapper<>();
            weightWrapper.eq("CART_TYPE", detailType);
            LoadWeightDto loadWeightDto = loadWeightMapper.selectOne(weightWrapper);
            Integer mineWeight = loadWeightDto.getOreWeight();//拉矿石单车重量
            Integer rockWeight = loadWeightDto.getStockWeight();//拉岩石单车重量

            Integer rockDistance = statisticVo.getRockDistance();//拉岩石总距离
            if (rockDistance != null) {
                statisticVo.setRockTurnOver((double) (rockDistance * rockWeight));
            }
            Integer mineDistance = statisticVo.getMineDistance();//拉矿石总距离
            if (mineDistance != null) {
                statisticVo.setMineTurnOver((double) (mineDistance * mineWeight));
            }
        }
        return ResponseResultDto.ok().data("data", statisticVoList);
    }

    @Override
    public ResponseResultDto countShovelWork(StatisticVo statisticParam) {
        //找到查询范围,日期,指定电铲
        List<StatisticVo> statisticVoList = equipmentMapper.countShovelWork(statisticParam);
        //按电铲进行分组
        Map<String, List<StatisticVo>> collect = statisticVoList.stream()
                .collect(Collectors.groupingBy(StatisticVo::getShovelNo));
        List<StatisticVo> resultList = new ArrayList<>();
        collect.forEach((shovelNo, list) -> {
            StatisticVo result = new StatisticVo();
            result.setShovelNo(shovelNo);//取得电铲
            result.setId(resultList.size());//给前台一个不重复的id
            resultList.add(result);
            for (StatisticVo statisticVo : list) {
                String cartNo = statisticVo.getCartNo();//大车编码
                //取得大车详细类型
                QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
                equipmentWrapper.eq("EQUIPMENT_NO", cartNo);
                EquipmentMain equipmentMain = equipmentMapper.selectOne(equipmentWrapper);
                String detailType = equipmentMain.getDetailType();
                //取得对应矿石种类的载重量
                QueryWrapper<LoadWeightDto> weightWrapper = new QueryWrapper<>();
                weightWrapper.eq("CART_TYPE", detailType);
                LoadWeightDto loadWeightDto = loadWeightMapper.selectOne(weightWrapper);
                Integer mineWeight = loadWeightDto.getOreWeight();//拉矿石单车重量
                Integer rockWeight = loadWeightDto.getStockWeight();//拉岩石单车重量

                Integer rockCount = statisticVo.getRockCount();//拉岩石总车数
                if (rockCount != null) {
                    if (result.getRockTons() != null) {
                        result.setRockTons(result.getRockTons() + rockCount * rockWeight);
                    } else {
                        result.setRockTons(rockCount * rockWeight);
                    }
                }
                Integer mineCount = statisticVo.getMineCount();//拉矿石车数
                if (mineCount != null) {
                    if (result.getMineTons() != null) {
                        result.setMineTons(result.getMineTons() + mineCount * mineWeight);
                    } else {
                        result.setMineTons(mineCount * mineWeight);
                    }
                }
            }
        });
        //翻译电铲名称
        for (StatisticVo statisticVo : resultList) {
            String shovelNo = statisticVo.getShovelNo();
            String shovelName = translateFromEquipNo2Name(shovelNo);
            statisticVo.setShovelName(shovelName);
        }
        return ResponseResultDto.ok().data("data", resultList);
    }

    @Override
    public ResponseResultDto countCartWork(StatisticVo statisticParam) {
        //找到查询范围,日期,指定大车
        List<StatisticVo> statisticVoList = equipmentMapper.countCartWork(statisticParam);
        for (int i = 0; i < statisticVoList.size(); i++) {
            StatisticVo statisticVo = statisticVoList.get(i);
            statisticVo.setId(i);
            String cartNo = statisticVo.getCartNo();
            String cartName = translateFromEquipNo2Name(cartNo);
            statisticVo.setCartName(cartName);
            //查询大车类型
            QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
            equipmentWrapper.eq("EQUIPMENT_NO", cartNo);
            EquipmentMain equipmentMain = equipmentMapper.selectOne(equipmentWrapper);
            statisticVo.setCartType(equipmentMain.getDetailType());
            statisticVo.setCartName(equipmentMain.getEquipmentName());
            String detailType = equipmentMain.getDetailType();
            //取得对应矿石种类的载重量
            QueryWrapper<LoadWeightDto> weightWrapper = new QueryWrapper<>();
            weightWrapper.eq("CART_TYPE", detailType);
            LoadWeightDto loadWeightDto = loadWeightMapper.selectOne(weightWrapper);

            Integer mineWeight = loadWeightDto.getOreWeight();//拉矿石单车重量
            Integer rockWeight = loadWeightDto.getStockWeight();//拉岩石单车重量

            Integer rockCount = statisticVo.getRockCount();//拉岩石车数
            if (rockCount != null) {
                statisticVo.setRockTurnOver((double) (rockCount * rockWeight));
            }
            Integer mineCount = statisticVo.getMineCount();//拉矿石车数
            if (mineCount != null) {
                statisticVo.setMineTurnOver((double) (mineCount * mineWeight));
            }
        }
        return ResponseResultDto.ok().data("data", statisticVoList);
    }

    @Override
    public ResponseResultDto updateEquipmentPushEnable(EquipmentMain equipmentMain) {
        String equipmentNo = equipmentMain.getEquipmentNo();
        QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
        equipmentWrapper.eq("EQUIPMENT_NO", equipmentNo);
        equipmentMapper.update(equipmentMain, equipmentWrapper);
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto getEquipmentPushEnable(String equipmentNo) {
        QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
        equipmentWrapper.select("EQUIPMENT_NO", "PUSH_ENABLE");
        equipmentWrapper.eq("EQUIPMENT_NO", equipmentNo);
        EquipmentMain equipmentMain = equipmentMapper.selectOne(equipmentWrapper);
        return ResponseResultDto.ok().data("data", equipmentMain);
    }


    @Override
    public ResponseResultDto updateCartStatus(EquipmentMain equipmentMain) {
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        String equipmentNo = equipmentMain.getEquipmentNo();
        String status = equipmentMain.getEquipmentStatus();
        //检查这个设备目前有没有任务正在执行,如果正在执行任务不允许改为休息
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.gt("TASK_STATE", TaskState.Create.getState());
        taskWrapper.lt("TASK_STATE", TaskState.LeaveField.getState());
        taskWrapper.eq("RECEIVER", equipmentNo);
        taskWrapper.eq("SCHEDULE_DATE", split[0]);
        taskWrapper.eq("SHIFT_TYPE", split[1]);
        Integer count = taskMapper.selectCount(taskWrapper);
        if (count > 0 && EquipmentStatus.Resting.getStatus().equals(status)) {
            return ResponseResultDto.ServiceError("目前有正在执行的任务,不可以休息");
        }

        QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
        equipmentWrapper.eq("EQUIPMENT_NO", equipmentNo);
        equipmentMapper.update(equipmentMain, equipmentWrapper);//更新大车状态
        equipmentMain.setEquipmentType(EquipmentType.Cart.getType());
        workStatusHistoryService.addStatusData(equipmentMain);//写入历史表
        EquipmentStatus equipmentStatus = EquipmentStatus.getEquipmentStatus(status);
        if (equipmentStatus != null) {
            //针对不同状态,采取不同操作
            switch (equipmentStatus) {
                case Working:
                    //如果当前故障列表中此设备的故障状态不是忽略和结束,将故障状态置为结束
                    faultRecovery(equipmentNo);
                    break;
                case BreakDown://故障
                    recordFault(equipmentMain);
                    break;
            }
        }
        return ResponseResultDto.ok();
    }

    public void faultRecovery(String equipmentNo) {
        EquipmentBreakDownDto breakDownDto = new EquipmentBreakDownDto();
        QueryWrapper<EquipmentBreakDownDto> breakWrapper = new QueryWrapper<>();
        breakWrapper.eq("EQUIPMENT_NO", equipmentNo);
        breakWrapper.in("FAULT_STATE", FaultState.Create.getState(), FaultState.Processing.getState());
        breakDownDto.setFaultState(FaultState.End.getState());
        breakDownMapper.update(breakDownDto, breakWrapper);
    }

    @Override
    public ResponseResultDto updateFieldStatus(EquipmentMain equipmentMain) {
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        String equipmentNo = equipmentMain.getEquipmentNo();
        String status = equipmentMain.getEquipmentStatus();
        //检查这个设备目前有没有任务正在执行,如果正在执行任务不允许改为休息
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.gt("TASK_STATE", TaskState.Create.getState());
        taskWrapper.lt("TASK_STATE", TaskState.LeaveField.getState());
        taskWrapper.eq("DESTINATION", equipmentNo);
        taskWrapper.eq("SCHEDULE_DATE", split[0]);
        taskWrapper.eq("SHIFT_TYPE", split[1]);
        Integer count = taskMapper.selectCount(taskWrapper);
        if (count > 0 && EquipmentStatus.Resting.getStatus().equals(status)) {
            return ResponseResultDto.ServiceError("目前有正在执行的任务,不可以休息");
        }
        QueryWrapper<EquipmentMain> equipmentWrapper = new QueryWrapper<>();
        equipmentWrapper.eq("EQUIPMENT_NO", equipmentNo);
        EquipmentMain equipmentData = equipmentMapper.selectOne(equipmentWrapper);
        if (equipmentData == null) {
            return ResponseResultDto.ServiceError("更新的设备编码不存在");
        }
        EquipmentStatus equipmentStatus = EquipmentStatus.getEquipmentStatus(equipmentMain.getEquipmentStatus());
        if (equipmentStatus != null) {
            //针对不同状态,采取不同操作
            switch (equipmentStatus) {
                case Working:
                    //如果当前故障列表中此设备的故障状态不是忽略和结束,将故障状态置为结束
                    faultRecovery(equipmentMain.getEquipmentNo());
                    break;
                case BreakDown://故障
                    recordFault(equipmentMain);
                    break;
            }
        }
        equipmentData.setEquipmentStatus(equipmentMain.getEquipmentStatus());
        equipmentMapper.updateById(equipmentData);
        return ResponseResultDto.ok();
    }

    public String translateFromEquipNo2Name(String equipmentNo) {
        QueryWrapper<EquipmentMain> equipWrapper = new QueryWrapper<>();
        equipWrapper.eq("EQUIPMENT_NO", equipmentNo);
        List<EquipmentMain> publisher = equipmentMapper.selectList(equipWrapper);
        return publisher.get(0).getEquipmentName();
    }
}
