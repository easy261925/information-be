package com.th.workbase.service.plan.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.equipment.MessageDto;
import com.th.workbase.bean.plan.Plan;
import com.th.workbase.bean.plan.PlanDetail;
import com.th.workbase.bean.plan.PlanTaskDto;
import com.th.workbase.bean.plan.PlanTaskHistoryDto;
import com.th.workbase.bean.system.LoadDistanceDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysConfig;
import com.th.workbase.bean.vo.PlanDetailVo;
import com.th.workbase.common.commonEnum.EquipmentType;
import com.th.workbase.common.commonEnum.TaskState;
import com.th.workbase.mapper.plan.PlanDetailMapper;
import com.th.workbase.mapper.plan.PlanMapper;
import com.th.workbase.mapper.plan.PlanTaskHistoryMapper;
import com.th.workbase.mapper.plan.PlanTaskMapper;
import com.th.workbase.mapper.system.LoadDistanceMapper;
import com.th.workbase.mapper.system.SysConfigMapper;
import com.th.workbase.service.common.DefineSwitchService;
import com.th.workbase.service.common.JPushService;
import com.th.workbase.service.plan.PlanDetailService;
import com.th.workbase.service.system.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import sun.security.ssl.HandshakeInStream;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

/**
 * @author cc
 * @since 2021-02-26
 */
@Service
public class PlanDetailServiceImpl extends ServiceImpl<PlanDetailMapper, PlanDetail> implements PlanDetailService {
    @Resource
    PlanDetailMapper planDetailMapper;
    @Resource
    PlanMapper planMapper;
    @Resource
    PlanTaskMapper planTaskMapper;
    @Autowired
    DefineSwitchService defineSwitchService;
    @Autowired
    SysDictService sysDictService;
    @Autowired
    JPushService jPushService;
    @Resource
    SysConfigMapper configMapper;
    @Resource
    LoadDistanceMapper distanceMapper;
    @Resource
    PlanTaskMapper taskMapper;
    @Resource
    PlanTaskHistoryMapper taskHistoryMapper;

    @Override
    public ResponseResultDto getPlanDetailByShovelNo(String equipmentNo) {
        //查询当日当班次所有计划
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        QueryWrapper<Plan> planWrapper = new QueryWrapper<>();
        planWrapper.eq("SHIFT_TYPE", split[1]);
        planWrapper.eq("SCHEDULE_DATE", split[0]);
        List<Plan> plans = planMapper.selectList(planWrapper);
        //查询当前班次此电铲对应的所有子计划
        List<PlanDetail> planDetailList = new ArrayList<>();
        for (Plan plan : plans) {
            QueryWrapper<PlanDetail> planDetailWrapper = new QueryWrapper<>();
            planDetailWrapper.eq("EQUIPMENT_NO", equipmentNo);
            planDetailWrapper.eq("plan_id", plan.getId());
            List<PlanDetail> planDetails = planDetailMapper.selectList(planDetailWrapper);
            if (planDetails.size() == 0) {
                continue;
            }
            planDetailList.addAll(planDetails);//取得所有的子计划
        }
        //若子计划不存在,返回一个全为0的默认值
        PlanDetailVo target = new PlanDetailVo();
        target.setCartsCount(0);
        target.setEquipmentNo(equipmentNo);
        target.setCompleted(0);
        if (planDetailList.size() == 0) {
            return ResponseResultDto.ok().data("data", new ArrayList<>());
        }
        Map<String, Integer> planMap = new HashMap<>();
        Map<String, Integer> completedMap = new HashMap<>();
        for (PlanDetail planDetail : planDetailList) {
            //拿到各类矿石的总计划数
            String category = planDetail.getCategory();//矿石类别
            planMap.merge(category, planDetail.getCartsCount(), Integer::sum);
            //拿到子计划对应的任务中的各类矿石的已完成数
            Integer planDetailId = planDetail.getId();
            QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
            taskWrapper.eq("FK_PLAN_DETAIL", planDetailId);
            taskWrapper.ne("category", "4");//排除岩石的完成部分,完成量只统计矿石车数
            taskWrapper.gt("TASK_STATE", TaskState.LoadComplete.getState());//状态大于装车视为电铲已完成
            Integer completed = planTaskMapper.selectCount(taskWrapper);//子计划对应的已完成任务数
            completedMap.merge(category, completed, Integer::sum);
        }
        //按矿石类别,翻译所有子计划到视图类
        List<PlanDetailVo> resultList = new ArrayList<>();
        planMap.forEach((category, sum) -> {
            PlanDetailVo result = new PlanDetailVo();
            String categoryName = sysDictService.queryDict("raw_type", category);
            result.setCategoryName(categoryName);//矿石名称
            result.setCategory(category);//矿石编码
            result.setCartsCount(sum);//总计划数
            Integer complete = completedMap.get(category);
            result.setCompleted(complete != null ? complete : 0);//总完成数
            resultList.add(result);
        });
        return ResponseResultDto.ok().data("data", resultList);
    }

    @Override
    public ResponseResultDto getDetailByEquipNo(String equipmentNo) {
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        QueryWrapper<Plan> planWrapper = new QueryWrapper<>();
        planWrapper.eq("SCHEDULE_DATE", split[0]);
        planWrapper.eq("SHIFT_TYPE", split[1]);
        planWrapper.eq("DESTINATION", equipmentNo);
        Plan plan = planMapper.selectOne(planWrapper);
        if (plan == null) {
            return ResponseResultDto.ok().data("data", new ArrayList<PlanDetailVo>());
        }
        Integer planId = plan.getId();
        QueryWrapper<PlanDetail> planDetailWrapper = new QueryWrapper<>();
        planDetailWrapper.eq("PLAN_ID", planId);
        List<PlanDetail> detailList = planDetailMapper.selectList(planDetailWrapper);
        List<PlanDetailVo> result = new ArrayList<>();
        for (PlanDetail planDetail : detailList) {
            PlanDetailVo planDetailVo = new PlanDetailVo();
            String categoryName = sysDictService.queryDict("raw_type", planDetail.getCategory());
            planDetailVo.setCategoryName(categoryName);
            planDetailVo.setCategory(planDetail.getCategory());
            planDetailVo.setCartsCount(planDetail.getCartsCount());
            result.add(planDetailVo);
        }
        return ResponseResultDto.ok().data("data", result);
    }

    @Override
    public ResponseResultDto getPlanDetailByFieldNo(String equipmentNo) {
        //获取当前班次对应的场地的所有计划
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        QueryWrapper<Plan> planWrapper = new QueryWrapper<>();
        planWrapper.eq("SCHEDULE_DATE", split[0]);
        planWrapper.eq("SHIFT_TYPE", split[1]);
        planWrapper.eq("DESTINATION", equipmentNo);

        List<Plan> plans = planMapper.selectList(planWrapper);
        if (plans.size() == 0) {
            return ResponseResultDto.ok().data("data", new ArrayList<>());
        }
        Map<String, Integer> planMap = new HashMap<>();//存储当前计划中,各类矿石对应的总车辆数
        for (Plan plan : plans) {
            Integer planId = plan.getId();
            QueryWrapper<PlanDetail> planDetailWrapper = new QueryWrapper<>();
            planDetailWrapper.eq("PLAN_ID", planId);
            List<PlanDetail> details = planDetailMapper.selectList(planDetailWrapper);
            Map<String, List<PlanDetail>> collect = details.stream().collect(Collectors.groupingBy(PlanDetail::getCategory));
            collect.forEach((category, v) -> {
                int sum = v.stream().mapToInt(PlanDetail::getCartsCount).sum();
                planMap.merge(category, sum, Integer::sum);
            });
        }
        //取到当前场地对应的已完成的矿石种类和车数
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.eq("SCHEDULE_DATE", split[0]);
        taskWrapper.eq("SHIFT_TYPE", split[1]);
        taskWrapper.eq("DESTINATION", equipmentNo);
        taskWrapper.eq("TASK_STATE", TaskState.LeaveField.getState());
        List<PlanTaskDto> taskList = planTaskMapper.selectList(taskWrapper);
        Map<Integer, List<PlanTaskDto>> collect = taskList.stream().collect(Collectors.groupingBy(PlanTaskDto::getFkPlanDetail));
        Map<String, Integer> completedMap = new HashMap<>();
        collect.forEach((planDetailId, list) -> {
            int completed = list.size();
            PlanDetail planDetail = planDetailMapper.selectById(planDetailId);
            String category = planDetail.getCategory();
            completedMap.merge(category, completed, Integer::sum);
        });
        List<PlanDetailVo> result = new ArrayList<>();
        planMap.forEach((category, sum) -> {
            String categoryName = sysDictService.queryDict("raw_type", category);
            PlanDetailVo planDetailVo = new PlanDetailVo();
            planDetailVo.setCategory(category);//矿石编码
            planDetailVo.setCategoryName(categoryName);//矿石名
            Integer completed = completedMap.get(category);
            planDetailVo.setCompleted(completed != null ? completed : 0);//已完成数
            planDetailVo.setCartsCount(sum);//生产总量
            result.add(planDetailVo);
        });
        return ResponseResultDto.ok().data("data", result);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseResultDto deleteDetailPlan(String detailId) {
        PlanDetail planDetail = planDetailMapper.selectById(detailId);
        Integer planId = planDetail.getPlanId();
        Plan plan = planMapper.selectById(planId);
        String destination = plan.getDestination();
        //检查任务是否已经被接受
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.eq("FK_PLAN_DETAIL", detailId);
        taskWrapper.isNotNull("RECEIVER");
        List<PlanTaskDto> taskDtoList = planTaskMapper.selectList(taskWrapper);
        if (taskDtoList.size() > 0) {
            return ResponseResultDto.ServiceError("当前计划对应的任务已经被领取,不允许删除");
        }

        //通知电铲和矿破
        String shovelNo = planDetail.getEquipmentNo();
        String content = "本班次工作计划有调整";
        MessageDto msg = new MessageDto();
        msg.setContent(content);
        msg.setReceiver(shovelNo);
        msg.setFkTaskId(0);
        msg.setEquipmentType(EquipmentType.Shovel.getType());
        jPushService.sendMsg(msg);

        if (!"BC_PY".equals(destination) && !"NC_PY".equals(destination)) {
            msg.setReceiver(destination);
            msg.setEquipmentType(EquipmentType.Field.getType());
            jPushService.sendMsg(msg);
        }
        //没有被接受的任务,删除任务,子计划数据
        taskWrapper.clear();
        taskWrapper.eq("FK_PLAN_DETAIL", detailId);
        planTaskMapper.delete(taskWrapper);
        planDetailMapper.deleteById(detailId);
        return ResponseResultDto.ok();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseResultDto updateDetailPlan(PlanDetail detail) {
        /*
           子计划核心内容有
           CARTS_COUNT 对应任务数   可以减少,减少的前提是减少的任务数还未被领取,可以直接增加
           CATEGORY     矿石种类    任务未生成之前允许修改
           EQUIPMENT_NO 电铲编码    任务未生成之前允许修改
           TASK_MAXIMUM 最大任务数   随时可改
           CARTS_TO_FIND 推送车辆数  随时可改
         */
        Integer id = detail.getId();
        PlanDetail planDetailData = planDetailMapper.selectById(id);//数据库原始数据
        Integer newCartsCount = detail.getCartsCount();//新任务数
        Integer oldCartsCount = planDetailData.getCartsCount();//旧任务数

        Integer isUse = planDetailData.getIsUse();//是否已经生成任务
        String newCategory = detail.getCategory();
        String oldCategory = planDetailData.getCategory();
        if (isUse == 1 && !oldCategory.equals(newCategory)) {
            return ResponseResultDto.ServiceError("计划已经生成任务,无法修改矿石种类");
        }
        String newShovelNo = detail.getEquipmentNo();
        String oldShovelNo = planDetailData.getEquipmentNo();
        if (isUse == 1 && !newShovelNo.equals(oldShovelNo)) {
            return ResponseResultDto.ServiceError("计划已经生成任务,不可以调整电铲");
        }
        //检查未被领取的任务数是否大于减小的任务数
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.eq("FK_PLAN_DETAIL", id);
        taskWrapper.isNotNull("RECEIVER");
        Integer count = planTaskMapper.selectCount(taskWrapper);//被领取的任务数
        if (newCartsCount < count) {
            return ResponseResultDto.ServiceError("计划数必须大于已完成的任务数");
        }

        Integer planId = planDetailData.getPlanId();
        Plan plan = planMapper.selectById(planId);
        String destination = plan.getDestination();
        if (!oldCartsCount.equals(newCartsCount)) {
            //通知电铲和矿破
            String shovelNo = planDetailData.getEquipmentNo();
            String content = "本班次工作计划有调整";
            MessageDto msg = new MessageDto();
            msg.setContent(content);
            msg.setReceiver(shovelNo);
            msg.setFkTaskId(0);
            msg.setEquipmentType(EquipmentType.Shovel.getType());
            jPushService.sendMsg(msg);

            if (!"BC_PY".equals(destination) && !"NC_PY".equals(destination)) {
                msg.setReceiver(destination);
                msg.setEquipmentType(EquipmentType.Field.getType());
                jPushService.sendMsg(msg);
            }
        }
        SysConfig sysConfig = configMapper.selectOne(null);
        String pushMode = sysConfig.getPushMode();
        boolean trigger = "2".equals(pushMode);
        if (newCartsCount > oldCartsCount) {
            int newTaskNum = newCartsCount - oldCartsCount;
            PlanTaskDto task = new PlanTaskDto();
            task.setFkPlanDetail(detail.getId());//设置计划明细表外键
            task.setDestination(destination);//设置任务目的地
            task.setPublisher(oldShovelNo);//设置任务发布者为当前电铲车编码
            task.setTaskState(TaskState.Create.getState());//设置任务状态为创建状态
            task.setScheduleDate(plan.getScheduleDate());//设置任务日期
            task.setShiftType(plan.getShiftType());//设置任务班次
            task.setPriorityTrigger(trigger);
            task.setCategory(detail.getCategory());
            //设置运输距离
            QueryWrapper<LoadDistanceDto> distanceWrapper = new QueryWrapper<>();
            distanceWrapper.eq("SHOVEL_NO", oldShovelNo);
            distanceWrapper.eq("FIELD_NO", destination);
            LoadDistanceDto distanceDto = distanceMapper.selectOne(distanceWrapper);
            if (distanceDto != null) {
                task.setDistance(distanceDto.getDistance());
            }
            PlanTaskHistoryDto taskHistory = new PlanTaskHistoryDto();
            copyProperties(task, taskHistory);
            for (int i = 0; i < newTaskNum; i++) {
                taskMapper.insert(task);
                taskHistory.setTaskId(task.getId());
                taskHistoryMapper.insert(taskHistory);
            }
        } else {
            taskWrapper.clear();
            taskWrapper.eq("FK_PLAN_DETAIL", id);
            taskWrapper.isNull("RECEIVER");
            taskWrapper.lt("ROWNUM", oldCartsCount - newCartsCount + 1);
            List<PlanTaskDto> taskDtoList = planTaskMapper.selectList(taskWrapper);
            List<Integer> taskIdList = taskDtoList.stream().map(PlanTaskDto::getId).collect(Collectors.toList());
            if (taskIdList!=null && taskIdList.size()>0){
                QueryWrapper<PlanTaskHistoryDto> historyWrapper = new QueryWrapper<>();
                historyWrapper.in("ID",taskIdList);
                taskHistoryMapper.delete(historyWrapper);//删除减少的任务历史
            }
            planTaskMapper.delete(taskWrapper);//删除减少的任务
        }
        planDetailData.setEquipmentNo(newShovelNo);
        planDetailData.setCartsToFind(detail.getCartsToFind());
        planDetailData.setTaskMaximum(detail.getTaskMaximum());
        planDetailData.setCategory(newCategory);
        planDetailData.setCartsCount(newCartsCount);
        planDetailMapper.updateById(planDetailData);
        return ResponseResultDto.ok();
    }
}
