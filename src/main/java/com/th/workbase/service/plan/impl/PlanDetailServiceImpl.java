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
        //?????????????????????????????????
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        QueryWrapper<Plan> planWrapper = new QueryWrapper<>();
        planWrapper.eq("SHIFT_TYPE", split[1]);
        planWrapper.eq("SCHEDULE_DATE", split[0]);
        List<Plan> plans = planMapper.selectList(planWrapper);
        //???????????????????????????????????????????????????
        List<PlanDetail> planDetailList = new ArrayList<>();
        for (Plan plan : plans) {
            QueryWrapper<PlanDetail> planDetailWrapper = new QueryWrapper<>();
            planDetailWrapper.eq("EQUIPMENT_NO", equipmentNo);
            planDetailWrapper.eq("plan_id", plan.getId());
            List<PlanDetail> planDetails = planDetailMapper.selectList(planDetailWrapper);
            if (planDetails.size() == 0) {
                continue;
            }
            planDetailList.addAll(planDetails);//????????????????????????
        }
        //?????????????????????,??????????????????0????????????
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
            //?????????????????????????????????
            String category = planDetail.getCategory();//????????????
            planMap.merge(category, planDetail.getCartsCount(), Integer::sum);
            //???????????????????????????????????????????????????????????????
            Integer planDetailId = planDetail.getId();
            QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
            taskWrapper.eq("FK_PLAN_DETAIL", planDetailId);
            taskWrapper.ne("category", "4");//???????????????????????????,??????????????????????????????
            taskWrapper.gt("TASK_STATE", TaskState.LoadComplete.getState());//???????????????????????????????????????
            Integer completed = planTaskMapper.selectCount(taskWrapper);//????????????????????????????????????
            completedMap.merge(category, completed, Integer::sum);
        }
        //???????????????,?????????????????????????????????
        List<PlanDetailVo> resultList = new ArrayList<>();
        planMap.forEach((category, sum) -> {
            PlanDetailVo result = new PlanDetailVo();
            String categoryName = sysDictService.queryDict("raw_type", category);
            result.setCategoryName(categoryName);//????????????
            result.setCategory(category);//????????????
            result.setCartsCount(sum);//????????????
            Integer complete = completedMap.get(category);
            result.setCompleted(complete != null ? complete : 0);//????????????
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
        //????????????????????????????????????????????????
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
        Map<String, Integer> planMap = new HashMap<>();//?????????????????????,?????????????????????????????????
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
        //????????????????????????????????????????????????????????????
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
            planDetailVo.setCategory(category);//????????????
            planDetailVo.setCategoryName(categoryName);//?????????
            Integer completed = completedMap.get(category);
            planDetailVo.setCompleted(completed != null ? completed : 0);//????????????
            planDetailVo.setCartsCount(sum);//????????????
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
        //?????????????????????????????????
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.eq("FK_PLAN_DETAIL", detailId);
        taskWrapper.isNotNull("RECEIVER");
        List<PlanTaskDto> taskDtoList = planTaskMapper.selectList(taskWrapper);
        if (taskDtoList.size() > 0) {
            return ResponseResultDto.ServiceError("??????????????????????????????????????????,???????????????");
        }

        //?????????????????????
        String shovelNo = planDetail.getEquipmentNo();
        String content = "??????????????????????????????";
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
        //????????????????????????,????????????,???????????????
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
           ????????????????????????
           CARTS_COUNT ???????????????   ????????????,???????????????????????????????????????????????????,??????????????????
           CATEGORY     ????????????    ?????????????????????????????????
           EQUIPMENT_NO ????????????    ?????????????????????????????????
           TASK_MAXIMUM ???????????????   ????????????
           CARTS_TO_FIND ???????????????  ????????????
         */
        Integer id = detail.getId();
        PlanDetail planDetailData = planDetailMapper.selectById(id);//?????????????????????
        Integer newCartsCount = detail.getCartsCount();//????????????
        Integer oldCartsCount = planDetailData.getCartsCount();//????????????

        Integer isUse = planDetailData.getIsUse();//????????????????????????
        String newCategory = detail.getCategory();
        String oldCategory = planDetailData.getCategory();
        if (isUse == 1 && !oldCategory.equals(newCategory)) {
            return ResponseResultDto.ServiceError("????????????????????????,????????????????????????");
        }
        String newShovelNo = detail.getEquipmentNo();
        String oldShovelNo = planDetailData.getEquipmentNo();
        if (isUse == 1 && !newShovelNo.equals(oldShovelNo)) {
            return ResponseResultDto.ServiceError("????????????????????????,?????????????????????");
        }
        //????????????????????????????????????????????????????????????
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.eq("FK_PLAN_DETAIL", id);
        taskWrapper.isNotNull("RECEIVER");
        Integer count = planTaskMapper.selectCount(taskWrapper);//?????????????????????
        if (newCartsCount < count) {
            return ResponseResultDto.ServiceError("??????????????????????????????????????????");
        }

        Integer planId = planDetailData.getPlanId();
        Plan plan = planMapper.selectById(planId);
        String destination = plan.getDestination();
        if (!oldCartsCount.equals(newCartsCount)) {
            //?????????????????????
            String shovelNo = planDetailData.getEquipmentNo();
            String content = "??????????????????????????????";
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
            task.setFkPlanDetail(detail.getId());//???????????????????????????
            task.setDestination(destination);//?????????????????????
            task.setPublisher(oldShovelNo);//?????????????????????????????????????????????
            task.setTaskState(TaskState.Create.getState());//?????????????????????????????????
            task.setScheduleDate(plan.getScheduleDate());//??????????????????
            task.setShiftType(plan.getShiftType());//??????????????????
            task.setPriorityTrigger(trigger);
            task.setCategory(detail.getCategory());
            //??????????????????
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
                taskHistoryMapper.delete(historyWrapper);//???????????????????????????
            }
            planTaskMapper.delete(taskWrapper);//?????????????????????
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
