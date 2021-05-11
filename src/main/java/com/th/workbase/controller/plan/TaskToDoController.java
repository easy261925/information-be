package com.th.workbase.controller.plan;


import com.th.workbase.bean.plan.TaskToDoDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.service.plan.TaskToDoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author tangj
 * @since 2021-03-19
 */
@RestController
@Api(tags = {"待办任务管理"})
@RequestMapping("/taskToDo")
public class TaskToDoController {
    @Autowired
    TaskToDoService taskToDoService;
    @ApiOperation(value = "给大车指派任务", notes = "给大车指派任务")
    @PostMapping("/assignCart")
    public ResponseResultDto assignCartTask(@RequestBody TaskToDoDto taskToDo) {
        return taskToDoService.assignCartTask(taskToDo);
    }

    @ApiOperation(value = "大车完成临时任务", notes = "大车完成临时任务")
    @PutMapping("/finishTask/{toDoId}")
    public ResponseResultDto temporaryTaskFinished(@PathVariable("toDoId") String toDoId) {
        return taskToDoService.temporaryTaskFinished(toDoId);
    }
}

