package com.th.workbase.bean.equipment;

import com.baomidou.mybatisplus.annotation.*;
import com.th.workbase.bean.BaseDto;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author tangj
 * @since 2021-03-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("MESSAGE")
@ApiModel(value="Message对象", description="")
@KeySequence(value = "SEQ_MESSAGE", clazz = Integer.class)
public class MessageDto extends BaseDto {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty(value = "发送方")
    @TableField("SENDER")
    private String sender;

    @ApiModelProperty(value = "接收方")
    @TableField("RECEIVER")
    private String receiver;

    @ApiModelProperty(value = "标题")
    @TableField("TITLE")
    private String title;

    @ApiModelProperty(value = "内容")
    @TableField("CONTENT")
    private String content;

    @ApiModelProperty(value = "信息类型")
    @TableField("TYPE")
    private String type;

    @ApiModelProperty(value = "接收设备类型")
    @TableField("EQUIPMENT_TYPE")
    private String equipmentType;

    @ApiModelProperty(value = "任务表外键",example = "1")
    @TableField("FK_TASK_ID")
    private Integer fkTaskId;

    @TableField("DT_DONE_DATE_TIME")
    private Date dtDoneDateTime;

    @TableField("REMARK")
    private String remark;

    @TableField(exist = false)
    private String uuid;


}
