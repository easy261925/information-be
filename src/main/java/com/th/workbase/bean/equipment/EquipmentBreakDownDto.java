package com.th.workbase.bean.equipment;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;
import com.th.workbase.bean.BaseDto;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author tangj
 * @since 2021-03-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("EQUIPMENT_BREAK_DOWN")
@ApiModel(value="EquipmentBreakDown对象")
@KeySequence(value = "SEQ_EQUIPMENT_BREAK_DOWN", clazz = Integer.class)
public class EquipmentBreakDownDto extends BaseDto {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    private Integer id;

    @ApiModelProperty(value = "设备编码")
    @TableField("EQUIPMENT_NO")
    private String equipmentNo;

    @ApiModelProperty(value = "设备类型")
    @TableField("EQUIPMENT_TYPE")
    private String equipmentType;

    @ApiModelProperty(value = "计划日期")
    @TableField("SCHEDULE_DATE")
    private String scheduleDate;

    @ApiModelProperty(value = "班次")
    @TableField("SHIFT_TYPE")
    private String shiftType;

    @ApiModelProperty(value = "上报人id")
    @TableField("HANDLER_ID")
    private String handlerId;

    @TableField("REMARK")
    private String remark;

    @ApiModelProperty(value = "故障类型")
    @TableField("FAULT_TYPE")
    private String faultType;

    @ApiModelProperty(value = "故障详细类型")
    @TableField("FAULT_DETAIL_TYPE")
    private String faultDetailType;

    @ApiModelProperty(value = "故障状态")
    @TableField("FAULT_STATE")
    private String faultState;


}
