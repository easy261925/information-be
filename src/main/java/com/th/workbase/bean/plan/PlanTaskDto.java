package com.th.workbase.bean.plan;

import com.baomidou.mybatisplus.annotation.*;
import com.th.workbase.bean.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 *
 * @author tangj
 * @since 2021-03-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PLAN_TASK")
@ApiModel(value = "PlanTask对象", description = "任务表,数据内容由计划详细表生成")
@KeySequence(value = "SEQ_PLAN_TASK", clazz = Integer.class)
public class PlanTaskDto extends BaseDto {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键",example = "1")
    @TableField("ID")
    @TableId(value = "ID", type = IdType.INPUT)
    private Integer id;

    @ApiModelProperty(value = "经度")
    @TableField("LONGITUDE")
    private BigDecimal longitude;

    @ApiModelProperty(value = "纬度")
    @TableField("LATITUDE")
    private BigDecimal latitude;

    @ApiModelProperty(value = "海拔")
    @TableField("ALTITUDE")
    private BigDecimal altitude;

    @ApiModelProperty(value = "任务状态")
    @TableField("TASK_STATE")
    private String taskState;

    @ApiModelProperty(value = "任务接收方")
    @TableField(value = "RECEIVER")
    private String receiver;

    @ApiModelProperty(value = "任务发布方")
    @TableField("PUBLISHER")
    private String publisher;

    @ApiModelProperty(value = "目的地")
    @TableField("DESTINATION")
    private String destination;

    @ApiModelProperty(value = "计划详细表关联外键",example = "1")
    @TableField("FK_PLAN_DETAIL")
    private Integer fkPlanDetail;

    @ApiModelProperty(value = "装车时备注信息")
    @TableField("LOAD_REMARK")
    private String loadRemark;

    @ApiModelProperty(value = "卸车备注信息")
    @TableField("UNLOAD_REMARK")
    private String unloadRemark;

    @ApiModelProperty(value = "任务日期")
    @TableField("SCHEDULE_DATE")
    private String scheduleDate;

    @ApiModelProperty(value = "任务班次")
    @TableField("SHIFT_TYPE")
    private String shiftType;

    @ApiModelProperty(value = "预留的类别字段,存储下拉框信息",example = "1")
    @TableField("TYPE")
    private Integer type;

    @ApiModelProperty(value = "接收人id",example = "1")
    @TableField("RECEIVE_HANDLER")
    private Integer receiveHandler;

    @ApiModelProperty(value = "运输距离 km" ,notes="默认值-1,生成任务的时候依据设置填充真实数据")
    @TableField("DISTANCE")
    private Double distance;

    @ApiModelProperty(value = "是否触发按今天已工作车辆优先找车的算法")
    @TableField("PRIORITY_TRIGGER")
    private Boolean priorityTrigger;

    @ApiModelProperty(value = "任务类型")
    @TableField(exist = false)
    private String taskType;

    @ApiModelProperty(value = "待办任务主键")
    @TableField("TO_DO_ID")
    private Integer toDoId;

    @ApiModelProperty(value = "设备类别")
    @TableField(exist = false)
    private String equipmentType;

    @ApiModelProperty(value = "矿石种类")
    @TableField("CATEGORY")
    private String category;

}
