package com.th.workbase.bean.plan;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;
import com.th.workbase.bean.BaseDto;

import java.util.Date;

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
@TableName("PLAN")
@ApiModel(value="Plan对象", description="")
@KeySequence(value = "SEQ_PLAN", clazz = Integer.class)
public class Plan extends BaseDto {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.INPUT)
    private Integer id;

    @ApiModelProperty(value = "目的地ID")
    @TableField("DESTINATION")
    private String destination;

    @ApiModelProperty(value = "排班日期")
    @TableField("SCHEDULE_DATE")
    private String scheduleDate;

    @ApiModelProperty(value = "排班产量")
    @TableField("AMOUNT")
    private String amount;

    @ApiModelProperty(value = "班次类型 0-白班 1-夜班")
    @TableField("SHIFT_TYPE")
    private String shiftType;


}
