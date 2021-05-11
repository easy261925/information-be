package com.th.workbase.bean.equipment;

import com.baomidou.mybatisplus.annotation.*;
import com.th.workbase.bean.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author tangj
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PROCESS_BREAK_HISTORY")
@ApiModel(value="ProcessBreakHistory对象")
@KeySequence(value = "SEQ_PROCESS_BREAK_HISTORY", clazz = Integer.class)
public class ProcessBreakHistoryDto extends BaseDto {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    private Integer id;

    @ApiModelProperty(value = "故障设备编码")
    @TableField("EQUIPMENT_DOWN")
    private String equipmentDown;

    @ApiModelProperty(value = "代替设备编码")
    @TableField("EQUIPMENT_UP")
    private String equipmentUp;

    @ApiModelProperty(value = "日期")
    @TableField("SCHEDULE_DATE")
    private String scheduleDate;

    @ApiModelProperty(value = "班次")
    @TableField("SHIFT_TYPE")
    private String shiftType;

    @ApiModelProperty(value = "故障表主键")
    @TableField("BREAK_DOWN_ID")
    private Integer breakDownId;

    @TableField("REMARK")
    private String remark;


}
