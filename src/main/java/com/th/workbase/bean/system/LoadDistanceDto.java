package com.th.workbase.bean.system;

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
 * <p>
 * 
 * </p>
 *
 * @author tangj
 * @since 2021-03-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("LOAD_DISTANCE")
@ApiModel(value="LoadDistance对象")
@KeySequence(value = "SEQ_LOAD_DISTANCE", clazz = Integer.class)
public class LoadDistanceDto extends BaseDto {

    private static final long serialVersionUID = 1L;

    @TableField("ID")
    private Integer id;

    @ApiModelProperty(value = "电铲编码")
    @TableField("SHOVEL_NO")
    private String shovelNo;

    @ApiModelProperty(value = "场地编码")
    @TableField("FIELD_NO")
    private String fieldNo;

    @ApiModelProperty(value = "电铲名称")
    @TableField(exist = false)
    private String shovelName;

    @ApiModelProperty(value = "场地名称")
    @TableField(exist = false)
    private String fieldName;

    @ApiModelProperty(value = "运输距离")
    @TableField("DISTANCE")
    private Double distance;

    @ApiModelProperty(value = "日期")
    @TableField("SCHEDULE_DATE")
    private String scheduleDate;

    @ApiModelProperty(value = "班次")
    @TableField("SHIFT_TYPE")
    private String shiftType;

    @TableField("REMARK")
    private String remark;


}
