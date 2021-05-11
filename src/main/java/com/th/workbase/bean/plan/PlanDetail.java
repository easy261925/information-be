package com.th.workbase.bean.plan;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;
import com.th.workbase.bean.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author cc
 * @since 2021-02-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PLAN_DETAIL")
@ApiModel(value="PlanDetail对象", description="")
@KeySequence(value = "SEQ_PLAN_DETAIL", clazz = Integer.class)
public class PlanDetail extends BaseDto {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.INPUT)
    private Integer id;

    @ApiModelProperty(value = "计划主表ID",example = "1")
    @TableField("PLAN_ID")
    private Integer planId;

    @ApiModelProperty(value = "车辆数量",example = "1")
    @TableField("CARTS_COUNT")
    private Integer cartsCount;

    @ApiModelProperty(value = "原料品类（数据字典）")
    @TableField("CATEGORY")
    private String category;

    @ApiModelProperty(value = "电铲编号")
    @TableField("EQUIPMENT_NO")
    private String equipmentNo;

    @ApiModelProperty(value = "最大任务数",example = "1")
    @TableField("TASK_MAXIMUM")
    private Integer taskMaximum;

    @ApiModelProperty(value = "查找的周围车辆数目",example = "1")
    @TableField("CARTS_TO_FIND")
    private Integer cartsToFind;

    @ApiModelProperty(value = "子计划完成数",example = "1")
    @TableField(exist = false)
    private Integer completed;

}
