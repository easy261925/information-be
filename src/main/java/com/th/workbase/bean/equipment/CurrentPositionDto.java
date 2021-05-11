package com.th.workbase.bean.equipment;


import com.baomidou.mybatisplus.annotation.*;
import com.th.workbase.bean.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author tangj
 * @since 2021-03-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("CURRENT_POSITION")
@ApiModel(value = "CurrentPosition对象")
@KeySequence(value = "SEQ_CURRENT_POSITION", clazz = Integer.class)
public class CurrentPositionDto extends BaseDto {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.INPUT)
    private Integer id;

    @ApiModelProperty(value = "设备编号")
    @TableField("EQUIPMENT_NO")
    private String equipmentNo;

    @TableField("LNG")
    private double lng;

    @TableField("LAT")
    private double lat;

    @TableField("STATUS")
    private String status;

    @ApiModelProperty(value = "速度",example = "1")
    @TableField("SPEED")
    private double speed;

    @ApiModelProperty(value = "海拔",example = "1")
    @TableField("ALTITUDE")
    private double altitude;

    @ApiModelProperty(value = "当前用户ID")
    @TableField("HANDLER_ID")
    private String handlerId;


}
