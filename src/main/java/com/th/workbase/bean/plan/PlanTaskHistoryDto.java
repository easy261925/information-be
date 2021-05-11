package com.th.workbase.bean.plan;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.th.workbase.bean.BaseDto;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author tangj
 * @since 2021-03-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PLAN_TASK_HISTORY")
@ApiModel(value = "PlanTask对象", description = "任务历史表")
@KeySequence(value = "SEQ_PLAN_TASK_HISTORY", clazz = Integer.class)
public class PlanTaskHistoryDto extends BaseDto {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableField("ID")
    private Integer id;


    @ApiModelProperty(value = "任务状态")
    @TableField("TASK_STATE")
    private String taskState;

    @ApiModelProperty(value = "任务接收方")
    @TableField("RECEIVER")
    private String receiver;

    @ApiModelProperty(value = "任务发布方")
    @TableField("PUBLISHER")
    private String publisher;

    @ApiModelProperty(value = "目的地")
    @TableField("DESTINATION")
    private String destination;

    @ApiModelProperty(value = "任务日期")
    @TableField("SCHEDULE_DATE")
    private String scheduleDate;

    @ApiModelProperty(value = "任务班次")
    @TableField("SHIFT_TYPE")
    private String shiftType;

    @ApiModelProperty(value = "接收人id")
    @TableField("RECEIVE_HANDLER")
    private Integer receiveHandler;


    @TableField("PRIORITY_TRIGGER")
    private String priorityTrigger;

    @TableField("TASK_ID")
    private Integer taskId;


}
