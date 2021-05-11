package com.th.workbase.service.plan;

import com.th.workbase.bean.plan.TaskToDoDto;
import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.system.ResponseResultDto;

/**
 *
 * @author tangj
 * @since 2021-03-19
 */
public interface TaskToDoService extends IService<TaskToDoDto> {

    ResponseResultDto assignCartTask(TaskToDoDto taskToDo);

    ResponseResultDto temporaryTaskFinished(String toDoId);
}
