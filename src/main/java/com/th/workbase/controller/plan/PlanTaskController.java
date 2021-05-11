package com.th.workbase.controller.plan;


import com.th.workbase.bean.plan.PlanTaskDto;
import com.th.workbase.bean.system.LoadDistanceDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.service.plan.PlanTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author tangj
 * @since 2021-03-02
 */
@RestController
@Api(tags = {"任务管理"})
@RequestMapping("/planTask")
public class PlanTaskController {
    @Autowired
    PlanTaskService planTaskService;

    @ApiOperation(value = "获取历史任务列表", notes = "获取历史任务列表")
    @ApiImplicitParams(
            value = {
                    @ApiImplicitParam(name = "current", value = "当前页码", dataType = "string", required = true, example = "1"),
                    @ApiImplicitParam(name = "pageSize", value = "显示长度", dataType = "string", required = true, example = "20")
            }
    )
    @GetMapping("/planTaskHistory")
    public ResponseResultDto getPlanTaskByPage(@ApiIgnore @RequestBody PlanTaskDto task, int current, int pageSize) {
        return planTaskService.getPlanTaskByPage(task, current, pageSize);
    }

    @ApiOperation(value = "获取大车任务列表", notes = "获取大车任务列表")
    @ApiImplicitParam(name = "equipmentNo", value = "设备编码", dataType = "string", required = true, example = "CARTS_1")
    @GetMapping("/getTasks")
    public ResponseResultDto getCartTasks(String equipmentNo) {
        return planTaskService.getCartTasks(equipmentNo);
    }


    @ApiOperation(value = "获取电铲任务列表", notes = "获取电铲任务列表")
    @ApiImplicitParam(name = "equipmentNo", value = "电铲设备编码", dataType = "string", required = true, example = "SHOVEL_1")
    @GetMapping("/getShovelTasks")
    public ResponseResultDto getShovelTasks(String equipmentNo) {
        return planTaskService.getShovelTasks(equipmentNo);
    }

    @ApiOperation(value = "获取场地任务列表", notes = "获取场地任务列表")
    @ApiImplicitParam(name = "equipmentNo", value = "场地设备编码", dataType = "string", required = true, example = "KP_1")
    @GetMapping("/getFieldTasks")
    public ResponseResultDto getFieldTasks(String equipmentNo) {
        return planTaskService.getFieldTasks(equipmentNo);
    }

    @ApiOperation(value = "接受任务", notes = "大车接受任务")
    @ApiImplicitParams(
            value = {
                    @ApiImplicitParam(name = "equipmentNo", value = "大车设备编码", dataType = "string", required = true, example = "CARTS_1"),
                    @ApiImplicitParam(name = "receiveHandler", value = "接收人id", dataType = "string", required = true, example = "5"),
                    @ApiImplicitParam(name = "taskId", value = "任务主键", dataType = "string", required = true, example = "20")
            }
    )
    @PutMapping("/acceptTask")
    public ResponseResultDto acceptTask(@RequestBody PlanTaskDto task) {
        return planTaskService.acceptTask(task);
    }


    @ApiOperation(value = "大车到达电铲", notes = "大车到达电铲")
    @ApiImplicitParam(name = "taskId", value = "任务主键", dataType = "string", required = true, example = "20")
    @PutMapping("/arriveShovel/{taskId}")
    public ResponseResultDto arriveShovel(@PathVariable("taskId") String taskId) {
        return planTaskService.arriveShovel(taskId);
    }

    @ApiOperation(value = "电铲强制大车到达", notes = "电铲强制大车到达")
    @ApiImplicitParam(name = "taskId", value = "任务主键", dataType = "string", required = true, example = "20")
    @PutMapping("/forceCartArrive/{taskId}")
    public ResponseResultDto forceCartArrive(@PathVariable("taskId") String taskId) {
        return planTaskService.forceCartArrive(taskId);
    }


    @ApiOperation(value = "电铲装车完成", notes = "电铲装车完成")
    @PutMapping("/loadComplete")
    public ResponseResultDto loadComplete(@RequestBody PlanTaskDto task) {
        return planTaskService.loadComplete(task);
    }


    @ApiOperation(value = "大车拒绝离开电铲", notes = "大车拒绝离开电铲")
    @ApiImplicitParam(name = "taskId", value = "任务主键", dataType = "string", required = true, example = "20")
    @PutMapping("/refuseLeaveShovel/{taskId}")
    public ResponseResultDto refuseLeaveShovel(@PathVariable("taskId") String taskId) {
        return planTaskService.refuseLeaveShovel(taskId);
    }

    @ApiOperation(value = "大车离开电铲", notes = "大车离开电铲")
    @ApiImplicitParam(name = "taskId", value = "任务主键", dataType = "string", required = true, example = "20")
    @PutMapping("/leaveShovel/{taskId}")
    public ResponseResultDto leaveShovel(@PathVariable("taskId") String taskId) {
        return planTaskService.leaveShovel(taskId);
    }


    @ApiOperation(value = "到达场地", notes = "到达场地")
    @ApiImplicitParam(name = "taskId", value = "任务主键", dataType = "string", required = true, example = "20")
    @PutMapping("/arriveField/{taskId}")
    public ResponseResultDto arriveField(@PathVariable("taskId") String taskId) {
        return planTaskService.arriveField(taskId);
    }


    @ApiOperation(value = "大车卸车", notes = "大车卸车")
    @ApiImplicitParam(name = "taskId", value = "任务主键", dataType = "string", required = true, example = "20")
    @PutMapping("/allowUnloading/{taskId}")
    public ResponseResultDto allowUnloading(@PathVariable("taskId") String taskId) {
        return planTaskService.allowUnloading(taskId);
    }

    @ApiOperation(value = "大车离开场地", notes = "大车离开场地")
    @ApiImplicitParam(name = "taskId", value = "任务主键", dataType = "string", required = true, example = "20")
    @PutMapping("/leaveField/{taskId}")
    public ResponseResultDto leaveField(@PathVariable("taskId") String taskId) {
        return planTaskService.leaveField(taskId);
    }


    @ApiOperation(value = "电铲催促", notes = "电铲催促")
    @ApiImplicitParam(name = "taskId", value = "任务主键", dataType = "string", required = true, example = "20")
    @PutMapping("/supervise/{taskId}")
    public ResponseResultDto supervise(@PathVariable("taskId") String taskId) {
        return planTaskService.supervise(taskId);
    }

    @ApiOperation(value = "任务重发", notes = "任务重发")
    @ApiImplicitParam(name = "taskId", value = "任务主键", dataType = "string", required = true, example = "20")
    @PutMapping("/resendTask/{taskId}")
    public ResponseResultDto resendTask(@PathVariable("taskId") String taskId) {
        return planTaskService.resendTask(taskId);
    }

    @ApiOperation(value = "查询历史任务", notes = "根据人员id查询对应班次历史任务")
    @GetMapping("/getTaskHistory")
    public ResponseResultDto getTaskHistory(PlanTaskDto task, int current, int pageSize) {
        return planTaskService.getTaskHistory(task, current, pageSize);
    }

    @ApiOperation(value = "查询临时任务列表", notes = "查询临时任务列表")
    @GetMapping("/getTmpTaskHistory")
    public ResponseResultDto getTmpTaskHistory(PlanTaskDto task) {
        return planTaskService.getTmpTaskHistory(task);
    }

}

