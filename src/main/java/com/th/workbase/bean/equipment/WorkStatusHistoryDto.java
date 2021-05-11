package com.th.workbase.bean.equipment;

import com.baomidou.mybatisplus.annotation.*;
import com.th.workbase.bean.BaseDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author tangj
 * @since 2021-03-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("WORK_STATUS_HISTORY")
@ApiModel(value = "WorkStatusHistory对象", description = "设备历史工作状态")
@KeySequence(value = "SEQ_WORK_STATUS_HISTORY", clazz = Integer.class)
public class WorkStatusHistoryDto extends BaseDto {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;

    @TableField("EQUIPMENT_NO")
    private String equipmentNo;

    @TableField("EQUIPMENT_TYPE")
    private String equipmentType;

    @TableField("STATUS")
    private String status;

    @TableField("REMARK")
    private String remark;

    @TableField("HANDLER_ID")
    private String handlerId;
}
