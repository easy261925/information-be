package com.th.workbase.bean.system;

import com.baomidou.mybatisplus.annotation.*;
import com.th.workbase.bean.BaseDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author tangj
 * @since 2021-03-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("LOAD_WEIGHT")
@ApiModel(value = "LoadWeight对象")
@KeySequence(value = "SEQ_LOAD_WEIGHT", clazz = Integer.class)
public class LoadWeightDto extends BaseDto {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    private Integer id;

    @TableField("CART_TYPE")
    private String cartType;

    @TableField("STOCK_WEIGHT")
    private Integer stockWeight;

    @TableField("ORE_WEIGHT")
    private Integer oreWeight;

    @TableField("REMARK")
    private String remark;

    @TableField(exist = false)
    private String cartTypeName;

    @TableField(exist = false)
    private String categoryName;


}
