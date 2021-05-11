package com.th.workbase.service.plan;

import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.plan.Plan;
import com.th.workbase.bean.plan.PlanTaskDto;
import com.th.workbase.bean.plan.PlanTaskVo;
import com.th.workbase.bean.system.LoadDistanceDto;
import com.th.workbase.bean.system.ResponseResultDto;

import java.util.List;

/**
 * @author tangj
 * @since 2021-03-02
 */
public interface PlanTaskService extends IService<PlanTaskDto> {

    /**
     * 通过计划生成任务
     *
     * @param shovelNo 电铲编码
     */
    ResponseResultDto generateTaskByPlan(String shovelNo);

    /**
     * 找到电铲在当前班次对应的任务
     *
     * @param shovelNo 电铲编码
     */
    List<PlanTaskDto> getTaskByPublisher(String shovelNo);

    /**
     * 找到电铲附近的车辆
     *
     * @param shovelNo       电铲编码
     * @param destination    任务目的地
     * @param enablePriority 是否启用客户提出的优先查找车辆类型标志位,在系统配置中选择模式2此处即为true
     */
    List<String> getDeviceNearBy(String shovelNo, String destination, boolean enablePriority);

    /**
     * 找到大车的任务列表,在大车成功领取任务时,显示当前任务细节,若未领取任务,显示待领取的任务列表
     *
     * @param cartNo 大车编码
     */
    ResponseResultDto getCartTasks(String cartNo);

    /**
     * 查找当前班次的所有计划
     */
    List<Plan> getCurrentSwitchPlans();

    /**
     * 电铲重新发送指定任务
     *
     * @param taskId 任务主键
     */
    ResponseResultDto resendTask(String taskId);

    /**
     * 大车接受任务
     *
     * @param task 主要接收对象中的任务主键和大车编码
     */
    ResponseResultDto acceptTask(PlanTaskDto task);

    /**
     * 电铲催促大车尽快到达
     *
     * @param taskId 任务主键
     */
    ResponseResultDto supervise(String taskId);

    /**
     * 大车到达电铲
     *
     * @param taskId 任务主键
     */
    ResponseResultDto arriveShovel(String taskId);

    /**
     * 电铲强制指定大车已经到达电铲位置,适用于大车已到达场地,但是由于GPS位置漂移无法到达现场的情况
     *
     * @param taskId 任务主键
     */
    ResponseResultDto forceCartArrive(String taskId);

    /**
     * 电铲装车已完成,电铲会指定装车完成的车辆,通知其装车已完成可以离开场地
     */
    ResponseResultDto loadComplete(PlanTaskDto taskDto);

    /**
     * 大车拒绝离开电铲,适用于电铲点击错误,
     * 例如:给A装车之后通知B可以离场,此时B可以拒绝离开,同时通知电铲,
     * 电铲人员需要进行确认自己点击的任务是否正确
     *
     * @param taskId 任务主键
     */
    ResponseResultDto refuseLeaveShovel(String taskId);

    /**
     * 大车装车完毕,离开电铲
     *
     * @param taskId 任务主键
     */
    ResponseResultDto leaveShovel(String taskId);

    /**
     * 大车通知场地,大车已经到达,准备卸车
     *
     * @param taskId 任务主键
     */
    ResponseResultDto arriveField(String taskId);

    /**
     * 场地允许大车卸车
     *
     * @param taskId 任务主键
     */
    ResponseResultDto allowUnloading(String taskId);

    /**
     * 大车卸车完成离开场地
     *
     * @param taskId 任务主键
     */
    ResponseResultDto leaveField(String taskId);

    /**
     * 获取电铲任务列表
     *
     * @param shovelNo 电铲编码
     */
    ResponseResultDto getShovelTasks(String shovelNo);

    /**
     * 获取场地任务列表
     *
     * @param fieldNo 场地编码
     */
    ResponseResultDto getFieldTasks(String fieldNo);

    ResponseResultDto getNextTask(String cartNo);

    ResponseResultDto getPlanTaskByPage(PlanTaskDto task, int current, int pageSize);

    List<PlanTaskVo> cartTaskDto2TaskVo(List<PlanTaskDto> taskList, String prefixContent);

    boolean getNotReceivedTaskPublishToCart(String shovelNo, String cartNo, String destination, Integer toDoId);

    ResponseResultDto getTaskHistory(PlanTaskDto task, int current, int pageSize);

    ResponseResultDto getTmpTaskHistory(PlanTaskDto task);
}
