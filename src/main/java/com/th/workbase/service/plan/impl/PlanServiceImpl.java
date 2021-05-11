package com.th.workbase.service.plan.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.equipment.MessageDto;
import com.th.workbase.bean.plan.Plan;
import com.th.workbase.bean.plan.PlanDetail;
import com.th.workbase.bean.plan.PlanTaskDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.vo.PlanVo;
import com.th.workbase.common.commonEnum.EquipmentType;
import com.th.workbase.common.commonEnum.TaskState;
import com.th.workbase.mapper.plan.PlanDetailMapper;
import com.th.workbase.mapper.plan.PlanMapper;
import com.th.workbase.mapper.plan.PlanTaskMapper;
import com.th.workbase.service.common.DefineSwitchService;
import com.th.workbase.service.common.JPushService;
import com.th.workbase.service.equipment.EquipmentMainService;
import com.th.workbase.service.plan.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.beans.BeanUtils.copyProperties;

/**
 * @author cc
 * @since 2021-02-26
 */
@Service
public class PlanServiceImpl extends ServiceImpl<PlanMapper, Plan> implements PlanService {

    @Resource
    private PlanDetailMapper planDetailMapper;
    @Resource
    private PlanMapper planMapper;
    @Resource
    PlanTaskMapper taskMapper;
    @Autowired
    DefineSwitchService defineSwitchService;
    @Autowired
    JPushService jPushService;
    @Autowired
    EquipmentMainService equipmentMainService;

    @Override
    public ResponseResultDto createPlan(HttpServletRequest request, PlanVo planVo) {
        //检查计划主表主键是否存在
        Plan plan = new Plan();
        copyProperties(planVo, plan);
        if (planVo.getId() == null) {
            planMapper.insert(plan);
        } else {
            planMapper.updateById(plan);
        }
        boolean checkUnsaturated = false;
        if (planVo.getPlanDetails().size() > 0) {
            for (PlanDetail item : planVo.getPlanDetails()) {
                PlanDetail planDetail = new PlanDetail();
                planDetail.setCartsCount(item.getCartsCount());
                planDetail.setCategory(item.getCategory());
                planDetail.setEquipmentNo(item.getEquipmentNo());
                planDetail.setPlanId(plan.getId());
                planDetail.setTaskMaximum(item.getTaskMaximum());
                planDetail.setCartsToFind(item.getCartsToFind());
                if (item.getId() == null) {
                    planDetailMapper.insert(planDetail);
                    //若是当前班次,通知电铲有新的计划产生
                    String defineSwitch = defineSwitchService.defineSwitch(new Date());
                    String[] split = defineSwitch.split("_");//当前实际班次
                    if (split[0].equals(plan.getScheduleDate()) && split[1].equals(plan.getShiftType())) {
                        //制定计划的时间就是当前班次对应时间段,通知电铲有新的计划产生
                        String shovelNo = planDetail.getEquipmentNo();
                        String content = "本班次有新的工作计划";
                        MessageDto msg = new MessageDto();
                        msg.setContent(content);
                        msg.setReceiver(shovelNo);
                        msg.setFkTaskId(0);
                        msg.setEquipmentType(EquipmentType.Shovel.getType());
                        jPushService.sendMsg(msg);
                        String destination = plan.getDestination();
                        if (!"BC_PY".equals(destination) && !"NC_PY".equals(destination)){
                            msg.setReceiver(destination);
                            msg.setEquipmentType(EquipmentType.Field.getType());
                            jPushService.sendMsg(msg);
                        }
                        checkUnsaturated = true;
                    }
                } else {
                    planDetail.setId(item.getId());
                    planDetailMapper.updateById(planDetail);
                }
            }
        }
        if (checkUnsaturated) {
            equipmentMainService.checkUnsaturatedDetailPlanAndSendTask();
        }
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto getPlan(HttpServletRequest request, PlanVo planVo) {
        QueryWrapper<Plan> planWrapper = new QueryWrapper<>();
        planWrapper.eq("SCHEDULE_DATE", planVo.getScheduleDate())
                .eq("SHIFT_TYPE", planVo.getShiftType())
                .eq("DESTINATION", planVo.getDestination());
        Plan plan = planMapper.selectOne(planWrapper);
        ArrayList<PlanDetail> planDetails = new ArrayList<>();
        if (plan != null) {
            Integer planId = plan.getId();
            QueryWrapper<PlanDetail> planDetailQueryWrapper = new QueryWrapper<>();
            planDetailQueryWrapper.eq("PLAN_ID", planId).orderByDesc("DT_CREA_DATE_TIME");
            List<PlanDetail> planDetailsResult = planDetailMapper.selectList(planDetailQueryWrapper);
            if (planDetailsResult.size() > 0) {
                for (PlanDetail planDetail : planDetailsResult) {
                    //找到子计划的完成数量
                    Integer detailId = planDetail.getId();
                    QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
                    taskWrapper.eq("FK_PLAN_DETAIL", detailId);
                    taskWrapper.ne("category", "4");//排除岩石的完成部分,完成量只统计矿石车数
                    taskWrapper.eq("TASK_STATE", TaskState.LeaveField.getState());
                    Integer count = taskMapper.selectCount(taskWrapper);
                    planDetail.setCompleted(count == null ? 0 : count);
                }
                planDetails.addAll(planDetailsResult);
            }
            PlanVo result = new PlanVo();
            copyProperties(plan, result);
            result.setPlanDetails(planDetails);
            return ResponseResultDto.ok().data("data", result);
        } else {
            return ResponseResultDto.ok().data("data", null);
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseResultDto deletePlan(String planId) {
        //检查主计划对应的任务是否已被领取,已被领取不允许删除
        QueryWrapper<PlanDetail> detailWrapper = new QueryWrapper<>();
        detailWrapper.eq("PLAN_ID", planId);
        List<PlanDetail> details = planDetailMapper.selectList(detailWrapper);
        //如果还没有生成任务,删除主计划和对应的子计划
        boolean generatedTask = details.stream().anyMatch(d -> d.getIsUse() == 1);
        if (!generatedTask) {
            planDetailMapper.delete(detailWrapper);
            planMapper.deleteById(planId);
            return ResponseResultDto.ok();
        }
        //检查任务是否已经被接受
        for (PlanDetail detail : details) {
            Integer detailId = detail.getId();
            QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
            taskWrapper.eq("FK_PLAN_DETAIL", detailId);
            taskWrapper.isNotNull("RECEIVER");
            List<PlanTaskDto> taskDtoList = taskMapper.selectList(taskWrapper);
            if (taskDtoList.size() > 0) {
                return ResponseResultDto.ServiceError("主计划对应的任务已经被领取,不允许删除");
            }
        }
        //没有被接受的任务,删除任务,子计划,主计划数据
        for (PlanDetail detail : details) {
            Integer detailId = detail.getId();
            QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
            taskWrapper.eq("FK_PLAN_DETAIL", detailId);
            taskMapper.delete(taskWrapper);
        }
        planDetailMapper.delete(detailWrapper);
        planMapper.deleteById(planId);
        return ResponseResultDto.ok();
    }
}
