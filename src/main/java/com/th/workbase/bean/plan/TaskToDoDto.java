package com.th.workbase.bean.plan;

import com.baomidou.mybatisplus.annotation.*;
import com.th.workbase.bean.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author tangj
 * @since 2021-03-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("TASK_TO_DO")
@ApiModel(value = "TaskToDo对象")
@KeySequence(value = "SEQ_TASK_TO_DO", clazz = Integer.class)
public class TaskToDoDto extends BaseDto {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    private Integer id;

    @ApiModelProperty(value = "执行人")
    @TableField("EXECUTOR")
    private String executor;

    @ApiModelProperty(value = "执行目标")
    @TableField("TARGET")
    private String target;

    @ApiModelProperty(value = "任务完成状态")
    @TableField("COMPLETE_STATE")
    private String completeState;

    @ApiModelProperty(value = "计划日期")
    @TableField("SCHEDULE_DATE")
    private String scheduleDate;

    @ApiModelProperty(value = "班次")
    @TableField("SHIFT_TYPE")
    private String shiftType;

    @ApiModelProperty(value = "待办任务类型")
    @TableField("TASK_TYPE")
    private String taskType;

    @ApiModelProperty(value = "指定人")
    @TableField("HANDLER_ID")
    private String handlerId;

    @ApiModelProperty(value = "临时任务内容")
    @TableField("REMARK")
    private String remark;

}
