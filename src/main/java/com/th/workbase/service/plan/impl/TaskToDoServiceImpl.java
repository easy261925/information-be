package com.th.workbase.service.plan.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.equipment.MessageDto;
import com.th.workbase.bean.plan.CartWorkTarget;
import com.th.workbase.bean.plan.PlanTaskDto;
import com.th.workbase.bean.plan.TaskToDoDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.common.commonEnum.EquipmentType;
import com.th.workbase.common.commonEnum.MessageType;
import com.th.workbase.common.commonEnum.TaskState;
import com.th.workbase.common.commonEnum.TaskType;
import com.th.workbase.mapper.equipment.MessageMapper;
import com.th.workbase.mapper.plan.CartWorkTargetMapper;
import com.th.workbase.mapper.plan.PlanTaskMapper;
import com.th.workbase.mapper.plan.TaskToDoMapper;
import com.th.workbase.service.common.DefineSwitchService;
import com.th.workbase.service.common.JPushService;
import com.th.workbase.service.plan.PlanTaskService;
import com.th.workbase.service.plan.TaskToDoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author tangj
 * @since 2021-03-19
 */
@Service
public class TaskToDoServiceImpl extends ServiceImpl<TaskToDoMapper, TaskToDoDto> implements TaskToDoService {
    @Resource
    TaskToDoMapper toDoMapper;
    @Autowired
    DefineSwitchService defineSwitchService;
    @Resource
    PlanTaskMapper taskMapper;
    @Autowired
    PlanTaskService planTaskService;
    @Autowired
    JPushService jPushService;
    @Resource
    MessageMapper messageMapper;
    @Resource
    CartWorkTargetMapper cartWorkTargetMapper;

    @Override
    public ResponseResultDto assignCartTask(TaskToDoDto taskToDo) {
        //检查指配任务的类型,存入待办表
        String cartNo = taskToDo.getExecutor();//执行车辆编码
        //检查大车有没有待执行的指配任务,如果有不允许新的指派
        QueryWrapper<TaskToDoDto> toDoWrapper = new QueryWrapper<>();
        toDoWrapper.eq("EXECUTOR", cartNo);
        toDoWrapper.eq("COMPLETE_STATE", TaskState.ToDoTaskCreate.getState());
        Integer count = toDoMapper.selectCount(toDoWrapper);
        if (count > 0) {
            return ResponseResultDto.ServiceError("此车辆已有待执行临时任务,请指派其他车辆");
        }
        //将当前车辆对应的未读消息全部删除
        QueryWrapper<MessageDto> msgWrapper = new QueryWrapper<>();
        msgWrapper.eq("RECEIVER", cartNo);
        msgWrapper.eq("IS_USE", 0);
        messageMapper.delete(msgWrapper);
        //添加待执行任务记录
        taskToDo.setCompleteState(TaskState.ToDoTaskCreate.getState());
        String defineSwitch = defineSwitchService.defineSwitch(new Date());
        String[] split = defineSwitch.split("_");
        taskToDo.setScheduleDate(split[0]);
        taskToDo.setShiftType(split[1]);
        toDoMapper.insert(taskToDo);

        String shovelNo = taskToDo.getTarget();
        if (StringUtils.isNotBlank(shovelNo)){
            //确认大车工作的目标对象
            QueryWrapper<CartWorkTarget> targetWrapper = new QueryWrapper<>();
            targetWrapper.eq("SCHEDULE_DATE", split[0]);
            targetWrapper.eq("SHIFT_TYPE", split[1]);
            targetWrapper.eq("CART_NO", cartNo);
            CartWorkTarget cartWorkTarget = cartWorkTargetMapper.selectOne(targetWrapper);
            //更新大车对应的工作目标表
            if (cartWorkTarget != null) {
                cartWorkTarget.setShovelNo(shovelNo);
                cartWorkTargetMapper.updateById(cartWorkTarget);
            }else {
                cartWorkTarget = new CartWorkTarget();
                cartWorkTarget.setShovelNo(shovelNo);
                cartWorkTarget.setCartNo(cartNo);
                cartWorkTarget.setScheduleDate(split[0]);
                cartWorkTarget.setShiftType(split[1]);
                cartWorkTargetMapper.insert(cartWorkTarget);
            }
        }
        //检查指配的车辆目前是否有任务,如果有任务,直接返回,待任务完成自动触发待办
        QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
        taskWrapper.eq("SCHEDULE_DATE", split[0]);
        taskWrapper.eq("SHIFT_TYPE", split[1]);
        taskWrapper.eq("RECEIVER", cartNo);
        taskWrapper.lt("TASK_STATE", TaskState.LeaveField.getState());
        count = taskMapper.selectCount(taskWrapper);
        if (count > 0) {
            return ResponseResultDto.ok();
        }
        //检查车辆目前有没有在执行的临时任务,有任务待任务结束自动触发待办
        toDoWrapper.clear();
        toDoWrapper.eq("EXECUTOR", cartNo);
        toDoWrapper.eq("COMPLETE_STATE", TaskState.ToDoTaskProcessing.getState());
        count = toDoMapper.selectCount(toDoWrapper);
        if (count > 0) {
            return ResponseResultDto.ok();
        }
        String taskType = taskToDo.getTaskType();//任务类型
        //没有任务直接推送
        if (TaskType.Normal.getType().equals(taskType)) {
            //普通任务,找到指定电铲任务,推送给车辆
            boolean sendSuccess = planTaskService.getNotReceivedTaskPublishToCart(shovelNo, cartNo, null, taskToDo.getId());
            if (!sendSuccess) {
                return ResponseResultDto.ServiceError("未找到指定电铲的任务");
            }
        } else {
            //临时任务,拿到消息内容推送给车辆
            MessageDto msg = new MessageDto();
            msg.setContent("临时任务:" + taskToDo.getRemark());
            msg.setSender(taskToDo.getHandlerId());
            msg.setType(MessageType.Business.getType());
            msg.setEquipmentType(EquipmentType.Cart.getType());
            msg.setReceiver(cartNo);
            msg.setFkTaskId(0);
            jPushService.sendMsg(msg);
        }
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto temporaryTaskFinished(String toDoId) {
        TaskToDoDto toDoDto = toDoMapper.selectById(toDoId);
        toDoDto.setCompleteState(TaskState.ToDoTaskCompleted.getState());
        toDoMapper.updateById(toDoDto);
        //获取此车辆的下一个任务
        return planTaskService.getNextTask(toDoDto.getExecutor());
    }
}
