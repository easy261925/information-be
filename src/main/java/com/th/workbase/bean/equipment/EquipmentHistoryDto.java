package com.th.workbase.bean.equipment;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;
import com.th.workbase.bean.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author cc
 * @since 2021-02-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("EQUIPMENT_HISTORY")
@ApiModel(value="EquipmentHistory对象", description="")
@KeySequence(value = "SEQ_EQUIPMENT_HISTORY", clazz = Long.class)
public class EquipmentHistoryDto extends BaseDto {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty(value = "设备编号")
    @TableField("EQUIPMENT_NO")
    private String equipmentNo;

    @TableField("LNG")
    private Double lng;

    @TableField("LAT")
    private Double lat;

    @TableField("STATUS")
    private String status;

    @ApiModelProperty(value = "速度",example = "1")
    @TableField("SPEED")
    private Double speed;

    @ApiModelProperty(value = "海拔",example = "1")
    @TableField("ALTITUDE")
    private Double altitude;

    @ApiModelProperty(value = "当前用户ID")
    @TableField("HANDLER_ID")
    private String handlerId;

    @ApiModelProperty(value = "开始时间")
    @TableField(exist = false)
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "任务主键")
    @TableField(exist = false)
    private String taskId;
}
