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
 * @author tangj
 * @since 2021-04-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("CART_WORK_TARGET")
@ApiModel(value="CartWorkTarget对象", description="")
@KeySequence(value = "SEQ_CART_WORK_TARGET", clazz = Integer.class)
public class CartWorkTarget extends BaseDto {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    private BigDecimal id;

    @TableField("CART_NO")
    private String cartNo;

    @TableField("SHOVEL_NO")
    private String shovelNo;

    @TableField("SCHEDULE_DATE")
    private String scheduleDate;

    @TableField("SHIFT_TYPE")
    private String shiftType;

    @TableField("REMARK")
    private String remark;


}
